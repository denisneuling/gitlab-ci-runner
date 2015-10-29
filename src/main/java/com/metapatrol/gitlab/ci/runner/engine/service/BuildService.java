package com.metapatrol.gitlab.ci.runner.engine.service;

import com.google.common.base.Joiner;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.BuildPayload;
import com.metapatrol.gitlab.ci.runner.engine.components.RunnerConfigurationProvider;
import com.metapatrol.gitlab.ci.runner.engine.util.DockerNamingUtil;
import com.metapatrol.gitlab.ci.runner.fs.FileSystem;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.ContainerInfo;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import static org.fusesource.jansi.Ansi.*;
import static org.fusesource.jansi.Ansi.Color.*;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class BuildService {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private GitService gitService;

    @Autowired
    private FileSystem fileSystem;

    @Autowired
    private DockerService dockerService;

    @Autowired
    private EventService eventService;

    @Autowired
    private RunnerConfigurationProvider runnerConfigurationProvider;

    @Autowired
    private GitlabCIService gitlabCIService;

    @Autowired
    private SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

    @Autowired
    private ProjectGitService projectGitService;

    @Autowired
    private ProjectBuildGitService projectBuildGitService;

    @Autowired
    private DockerfileService dockerfileService;

    static class ErrorStateHolder{
        boolean errored = false;
        public boolean isErrored(){
            return errored;
        }
        public void setErrored(boolean errored){
            this.errored = errored;
        }
    }

    public void build(BuildPayload buildPayload) throws DockerException, InterruptedException {
        final ErrorStateHolder errorStateHolder = new ErrorStateHolder();
        final MessageHolder messageHolder = new MessageHolder();
        final Tracer tracer = new Tracer(buildPayload.getId(), gitlabCIService);
        final Flusher flusher = new Flusher(messageHolder, tracer);

        Future<?> flusherFuture = flusherFuture = simpleAsyncTaskExecutor.submit(flusher);

        try {
            errorStateHolder.setErrored(!buildInternal(buildPayload, errorStateHolder, messageHolder, flusher));
        }catch(Throwable throwable){
            log.error(throwable.getMessage(), throwable);
            errorStateHolder.setErrored(true);
        }

        if(flusherFuture!=null){
            try {
                flusherFuture.cancel(true);
            }catch(Throwable throwable){}
        }

        eventService.sendBuildFinishedEvent(buildPayload, errorStateHolder.isErrored());
    }

    private boolean buildInternal(
        final BuildPayload buildPayload
    ,   final ErrorStateHolder errorStateHolder
    ,   final MessageHolder messageHolder
    ,   final Flusher flusher
    ) throws DockerException, InterruptedException {
        GitProgressStateListener gitProgressStateListener = new GitProgressStateListener(messageHolder);
        DockerProgressStateListener dockerProgressStateListener = new DockerProgressStateListener(messageHolder);
        CommandProgressStateListener commandProgressStateListener = new CommandProgressStateListener(messageHolder, new CommandErrorHandler(){
            @Override
            public void errored() {
            errorStateHolder.setErrored(true);
            }
        });

        URI uri = URI.create(buildPayload.getRepositoryURL());
        String projectName = DockerNamingUtil.nameFromURI(uri, null, false);
        String containerName = DockerNamingUtil.nameFromURI(uri, "_"+buildPayload.getSha()+"_"+new Date().getTime(), true);

        // /source/<projectgroup>/<projectname>
        File projectGitDirectory = fileSystem.getProjectGitDirectory(projectName);
        // /builds/<projectgroup>/<projectname>/<sha>/<unixtime>
        File projectBuildDirectory = fileSystem.getProjectBuildDirectory(projectName, buildPayload.getSha());
        // /builds/<projectgroup>/<projectname>/<sha>/<unixtime>/app
        File projectBuildGitDirectory = fileSystem.getProjectBuildGitDirectory(projectBuildDirectory);


        String logmessage;

        logmessage = ansi().fg(GREEN).a("$ git clone " + uri.getScheme() + "://" + uri.getHost() + (uri.getPort()!=80&&uri.getPort()!=443?":"+uri.getPort():"") + uri.getPath() + " " + projectBuildDirectory.getAbsolutePath()).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        Git git = projectGitService.ensureProjectGitDirectory(projectGitDirectory, uri.toString(), gitProgressStateListener);
        git.close();

        String sha = buildPayload.getSha();
        logmessage = ansi().fg(GREEN).a("$ git checkout " + sha).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        git = projectBuildGitService.ensureProjectBuildGitDirectory(projectBuildGitDirectory, projectGitDirectory.toURI().toString(), sha, gitProgressStateListener);
        git.close();

        String image = runnerConfigurationProvider.get().getDefaultDockerBuildImage();
        BuildPayload.Options options = buildPayload.getOptions();
        if(options !=null && options.getImage() !=null && !options.getImage().isEmpty()){
            image = options.getImage();
        }

        List<String> adds = new LinkedList<String>();
        adds.add("app /app");
        dockerfileService.renderDockerFile(projectBuildDirectory, image, adds);


        logmessage = ansi().fg(GREEN).a("$ docker build -t " + containerName + " "+projectBuildDirectory.getAbsolutePath()).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        String imageId = dockerService.createImage(projectBuildDirectory, containerName, dockerProgressStateListener);


        String[] keepAliveCommands = new String[]{
            "sh", "-c", "while :; do sleep 1; done"
        };
        logmessage = ansi().fg(GREEN).a("$ docker create --name=" + containerName+" "+image+" "+join(Arrays.asList(keepAliveCommands), " ")).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        ContainerInfo containerInfo = dockerService.createContainer(imageId, containerName, keepAliveCommands);


        logmessage = ansi().fg(GREEN).a("$ docker start " + containerInfo.id()).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        dockerService.startContainer(containerInfo.id());


        String originalCommands = buildPayload.getCommands();
        StringBuffer buffer = new StringBuffer();
        List<String> commandList = new ArrayList<String>(Arrays.asList(originalCommands.split("\n")));
        buffer.append(join(commandList, " && "));
        buffer.append(" 2>&1 || echo \"Build Failed.\" 1>&2");
        String[] commands = new String[]{
                "sh", "-c", "cd /app && "+buffer.toString()
        };
        logmessage = ansi().fg(GREEN).a("$ docker exec " + containerInfo.id() + " " + join(Arrays.asList(commands), " && ")).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        dockerService.execCreate(
            containerInfo.id()
        ,   commands
        ,   commandProgressStateListener
        );


        logmessage = ansi().fg(GREEN).a("$ docker stop " + containerInfo.id() + " --time=1").reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        dockerService.stopContainer(containerInfo.id());


        logmessage = ansi().fg(GREEN).a("$ docker rm -f " + containerInfo.id()).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        dockerService.removeContainer(containerInfo.id());

        logmessage = ansi().fg(GREEN).a("$ docker rmi -f " + imageId).reset().toString();
        log.info(logmessage);
        messageHolder.append(logmessage);
        dockerService.removeImage(imageId);

        try {
            FileUtils.deleteDirectory(projectBuildDirectory);
        } catch (IOException e) {
            log.warn(e.getMessage());
        }

        if(errorStateHolder.isErrored()){
            flusher.halt();
            flusher.flush();
            return false;
        }else{
            flusher.halt();
            flusher.flush();
            return true;
        }
    }

    private static String join(List<String> args, String join){
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iterator = args.iterator();
        while(iterator.hasNext()){
            buffer.append(iterator.next());
            if(iterator.hasNext() && buffer.length()>0){
                buffer.append(join);
            }
        }
        return buffer.toString();
    }

    static class MessageHolder {
        private int length = 0;
        private StringBuffer stringBuffer = new StringBuffer();

        public synchronized void append(String message){
            stringBuffer.append(message);
            stringBuffer.append("\n");
            length++;
        }

        public synchronized int getLength(){
            return length;
        }
        public synchronized String getMessages(){
            return stringBuffer.toString();
        }
    }

    static class Tracer{
        private GitlabCIService gitlabCIService;
        private String buildId;
        public Tracer(String buildId, GitlabCIService gitlabCIService){
            this.gitlabCIService = gitlabCIService;
            this.buildId = buildId;
        }
        public void remoteTrace(String trace){
            if(trace!=null) {
                gitlabCIService.updateBuild(buildId, "running", trace);
            }
        }
    }

    static class Flusher implements Runnable {
        private boolean _stop = false;

        private Tracer tracer;
        private MessageHolder messageHolder;
        public Flusher(MessageHolder messageHolder, Tracer tracer){
            this.messageHolder = messageHolder;
            this.tracer = tracer;
        }

        public void halt(){
            _stop = true;
        }

        public void flush(){
            String messages = messageHolder.getMessages();
            tracer.remoteTrace(messages);
        }

        @Override
        public void run() {
            while(true){
                try {
                    Thread.currentThread().sleep(2000);
                } catch (InterruptedException e) {
                }

                if(_stop){
                    break;
                }

                if(messageHolder.getLength()>0){
                    String messages = messageHolder.getMessages();
                    tracer.remoteTrace(messages);
                }
            }
        }
    }

    static class GitProgressStateListener extends ProgressStateListener{
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
