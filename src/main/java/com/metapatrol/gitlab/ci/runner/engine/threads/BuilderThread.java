package com.metapatrol.gitlab.ci.runner.engine.threads;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.RegisterBuildResponsePayload;
import com.metapatrol.gitlab.ci.runner.engine.components.ErrorStateHolder;
import com.metapatrol.gitlab.ci.runner.engine.components.MessageHolder;
import com.metapatrol.gitlab.ci.runner.engine.components.RunnerConfigurationProvider;
import com.metapatrol.gitlab.ci.runner.engine.service.DockerService;
import com.metapatrol.gitlab.ci.runner.engine.service.DockerfileService;
import com.metapatrol.gitlab.ci.runner.engine.service.EventService;
import com.metapatrol.gitlab.ci.runner.engine.service.ProgressStateListener;
import com.metapatrol.gitlab.ci.runner.engine.service.ProjectBuildGitService;
import com.metapatrol.gitlab.ci.runner.engine.service.ProjectGitService;
import com.metapatrol.gitlab.ci.runner.engine.util.DockerNamingUtil;
import com.metapatrol.gitlab.ci.runner.engine.util.StringUtil;
import com.metapatrol.gitlab.ci.runner.fs.FileSystem;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerInfo;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.ansi;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class BuilderThread implements Runnable {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private FileSystem fileSystem;

    @Autowired
    private DockerService dockerService;

    @Autowired
    private EventService eventService;

    @Autowired
    private RunnerConfigurationProvider runnerConfigurationProvider;

    @Autowired
    private ProjectGitService projectGitService;

    @Autowired
    private ProjectBuildGitService projectBuildGitService;

    @Autowired
    private DockerfileService dockerfileService;

    private transient RegisterBuildResponsePayload registerBuildResponsePayload;
    private transient ErrorStateHolder errorStateHolder;
    private transient MessageHolder messageHolder;

    public RegisterBuildResponsePayload getRegisterBuildResponsePayload() {
        return registerBuildResponsePayload;
    }

    public void setRegisterBuildResponsePayload(RegisterBuildResponsePayload registerBuildResponsePayload) {
        this.registerBuildResponsePayload = registerBuildResponsePayload;
    }

    public ErrorStateHolder getErrorStateHolder() {
        return errorStateHolder;
    }

    public void setErrorStateHolder(ErrorStateHolder errorStateHolder) {
        this.errorStateHolder = errorStateHolder;
    }

    public MessageHolder getMessageHolder() {
        return messageHolder;
    }

    public void setMessageHolder(MessageHolder messageHolder) {
        this.messageHolder = messageHolder;
    }

    @Override
    public void run() {
        if(log.isDebugEnabled()){
            log.debug("Builder running.");
        }
        try{
            doWork();
        }catch (InterruptedException e){
            if(log.isDebugEnabled()){
                log.debug("Builder killed.");
            }
            cleanUp();
        }
        if(log.isDebugEnabled()){
            log.debug("Builder exited.");
        }
    }

    // /source/<projectgroup>/<projectname>
    private File projectGitSourceDirectory;
    // /builds/<projectgroup>/<projectname>
    private File projectBuildDirectory;
    // /builds/<projectgroup>/<projectname>/<sha>
    private File projectBuildShaDirectory;
    // /builds/<projectgroup>/<projectname>/<sha>/<unixtime>
    private File projectBuildShaDateDirectory;
    // /builds/<projectgroup>/<projectname>/<sha>/<unixtime>/app
    private File projectBuildShaDateAppDirectory;

    private ContainerInfo containerInfo;
    private String imageId;

    private void doWork() throws InterruptedException {
        MDC.put("project", String.format("%s ", registerBuildResponsePayload.getProjectName()));
        MDC.put("sha", String.format("%s ", registerBuildResponsePayload.getSha()));
        MDC.put("build", String.format("%s ", registerBuildResponsePayload.getId()));

        GitProgressStateListener gitProgressStateListener = new GitProgressStateListener(messageHolder);
        DockerProgressStateListener dockerProgressStateListener = new DockerProgressStateListener(messageHolder);
        CommandProgressStateListener commandProgressStateListener = new CommandProgressStateListener(messageHolder, new CommandErrorHandler(){
            @Override
            public void errored() {
                errorStateHolder.setErrored(true);
            }
        });

        URI uri = URI.create(registerBuildResponsePayload.getRepositoryURL());
        String projectName = DockerNamingUtil.nameFromURI(uri, null, false);
        String containerName = DockerNamingUtil.nameFromURI(uri, "_" + registerBuildResponsePayload.getSha() + "_" + new Date().getTime(), true);

        // /source/<projectgroup>/<projectname>
        projectGitSourceDirectory = fileSystem.getProjectGitDirectory(projectName);
        // /builds/<projectgroup>/<projectname>
        projectBuildDirectory = fileSystem.getProjectBuildDirectory(projectName);
        // /builds/<projectgroup>/<projectname>/<sha>
        projectBuildShaDirectory = fileSystem.getProjectBuildShaDirectory(projectBuildDirectory, registerBuildResponsePayload.getSha());
        // /builds/<projectgroup>/<projectname>/<sha>/<unixtime>
        projectBuildShaDateDirectory = fileSystem.getProjectBuildShaDateDirectory(projectBuildShaDirectory, new Date());
        // /builds/<projectgroup>/<projectname>/<sha>/<unixtime>/app
        projectBuildShaDateAppDirectory = fileSystem.getProjectBuildShaDateAppDirectory(projectBuildShaDateDirectory);

        String logmessage;

        logmessage = ansi().fg(GREEN).a("$ git clone " + uri.getScheme() + "://" + uri.getHost() + (uri.getPort()>0&&uri.getPort()!=80&&uri.getPort()!=443?":"+uri.getPort():"") + uri.getPath() + " " + projectBuildDirectory.getAbsolutePath()).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        // TODO errorhandling
        Git git = projectGitService.ensureProjectGitDirectory(projectGitSourceDirectory, uri.toString(), gitProgressStateListener);
        git.close();

        String sha = registerBuildResponsePayload.getSha();
        logmessage = ansi().fg(GREEN).a("$ git checkout " + sha).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        try {
            git = projectBuildGitService.ensureProjectBuildGitDirectory(projectBuildShaDateAppDirectory, projectGitSourceDirectory.toURI().toString(), sha, gitProgressStateListener);
        } catch (IOException e) {
            log.warn(e.getMessage());
            messageHolder.append(e.getMessage());
            return;
        } catch (GitAPIException e) {
            log.warn(e.getMessage());
            messageHolder.append(e.getMessage());
            return;
        }
        git.close();

        String image = runnerConfigurationProvider.get().getDefaultDockerBuildImage();
        RegisterBuildResponsePayload.Options options = registerBuildResponsePayload.getOptions();
        if(options !=null && options.getImage() !=null && !options.getImage().isEmpty()){
            image = options.getImage();
        }

        List<String> adds = new LinkedList<String>();
        adds.add("app /app");

        List<RegisterBuildResponsePayload.Variable> variables = registerBuildResponsePayload.getVariables();
        List<RegisterBuildResponsePayload.Variable> envs = new LinkedList<RegisterBuildResponsePayload.Variable>();
        if(variables!=null){
            for(RegisterBuildResponsePayload.Variable variable: variables){
                if(variable.getPublic()!=null && variable.getPublic().booleanValue()){
                    envs.add(variable);
                }
            }
        }
        envs.add(new RegisterBuildResponsePayload.Variable("CI_BUILD_REF_NAME", registerBuildResponsePayload.getRef()));
        try {
            dockerfileService.renderDockerFile(projectBuildShaDateDirectory, image, adds, envs);
        } catch (IOException e) {
            log.warn(e.getMessage());
            messageHolder.append(e.getMessage());
            return;
        }


        logmessage = ansi().fg(GREEN).a("$ docker build -t " + containerName + " "+projectBuildShaDateDirectory.getAbsolutePath()).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        try {
            imageId = dockerService.createImage(projectBuildShaDateDirectory, containerName, dockerProgressStateListener);
        } catch (IOException e) {
            log.warn(e.getMessage());
            messageHolder.append(e.getMessage());
            return;
        } catch (DockerException e) {
            log.warn(e.getMessage());
            messageHolder.append(e.getMessage());
            return;
        }

        String[] keepAliveCommands = new String[]{
                "sh", "-c", "while :; do sleep 1; done"
        };
        logmessage = ansi().fg(GREEN).a("$ docker create --name=" + containerName+" "+image+" "+StringUtil.join(Arrays.asList(keepAliveCommands), " ")).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        try {
            containerInfo = dockerService.createContainer(imageId, containerName, keepAliveCommands);
        } catch (DockerException e) {
            log.warn(e.getMessage());
            messageHolder.append(e.getMessage());
            return;
        }


        logmessage = ansi().fg(GREEN).a("$ docker start " + containerInfo.id()).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        try {
            dockerService.startContainer(containerInfo.id());
        } catch (DockerException e) {
            log.warn(e.getMessage());
            messageHolder.append(e.getMessage());
            return;
        }


        String originalCommands = registerBuildResponsePayload.getCommands();
        StringBuffer buffer = new StringBuffer();
        List<String> commandList = new ArrayList<String>(Arrays.asList(originalCommands.split("\n")));
        buffer.append(StringUtil.join(commandList, " && "));
        buffer.append(" 2>&1 || echo \"Build Failed.\" 1>&2");
        String[] commands = new String[]{
                "sh", "-c", "cd /app && "+buffer.toString()
        };
        logmessage = ansi().fg(GREEN).a("$ docker exec " + containerInfo.id() + " " + StringUtil.join(Arrays.asList(commands), " && ")).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        try {
            dockerService.execCreate(
                containerInfo.id()
            ,   commands
            ,   commandProgressStateListener
            );
        } catch (DockerException e) {
            log.warn(e.getMessage());
            messageHolder.append(e.getMessage());
            return;
        }

        cleanUp();

        eventService.sendBuildFinishedEvent(
            registerBuildResponsePayload.getId()
        ,   registerBuildResponsePayload.getProjectName()
        ,   registerBuildResponsePayload.getSha()
        ,   errorStateHolder.isErrored()
        ,   messageHolder.getMessages()
        );
    }

    private void cleanUp(){
        String logmessage = ansi().fg(GREEN).a("$ docker stop " + containerInfo.id() + " --time=1").reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        try {
            dockerService.stopContainer(containerInfo.id());
        } catch (DockerException e) {
            // fuck it
        } catch (InterruptedException e) {
            // fuck it
        }


        logmessage = ansi().fg(GREEN).a("$ docker rm -f " + containerInfo.id()).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        try {
            dockerService.removeContainer(containerInfo.id());
        } catch (DockerException e) {
            // fuck it
        } catch (InterruptedException e) {
            // fuck it
        }

        logmessage = ansi().fg(GREEN).a("$ docker rmi -f " + imageId).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        try {
            dockerService.removeImage(imageId);
        } catch (DockerException e) {
            // fuck it
        } catch (InterruptedException e) {
            // fuck it
        }

        try {
            FileUtils.deleteDirectory(projectBuildShaDateDirectory);
        } catch (IOException e) {
            // fuck it
        }
    }

    static class GitProgressStateListener extends ProgressStateListener {
        private MessageHolder messageHolder;
        public GitProgressStateListener(MessageHolder messageHolder) {
            this.messageHolder = messageHolder;
        }

        @Override
        public void enterState(State state) {
            messageHolder.append(state.getMessage());
        }
    }

    static class DockerProgressStateListener extends ProgressStateListener{
        private MessageHolder messageHolder;
        public DockerProgressStateListener(MessageHolder messageHolder) {
            this.messageHolder = messageHolder;
        }

        private String message = "";

        private void flush() {
            messageHolder.append(message);
            message = "";
        }

        @Override
        public void enterState(State state) {
            StringReader stringReader = new StringReader(state.getMessage());

            int read = -1;
            try {
                while ((read = stringReader.read()) > -1) {
                    if (read == 10) {
                        flush();
                        continue;
                    }

                    message += (char) read;
                }
            } catch (Throwable throwable) {
            }

            stringReader.close();
        }
    }

    static class CommandProgressStateListener extends DockerProgressStateListener{
        private CommandErrorHandler commandErrorHandler;
        public CommandProgressStateListener(MessageHolder messageHolder, CommandErrorHandler commandErrorHandler) {
            super(messageHolder);

            this.commandErrorHandler = commandErrorHandler;
        }

        @Override
        public void enterState(State state) {
            super.enterState(state);

            if(state.getStream() == State.Stream.STDERR){
                commandErrorHandler.errored();
            }
        }
    }

    interface CommandErrorHandler{
        public void errored();
    }
}
