package com.metapatrol.gitlab.ci.runner.engine.service;

import com.metapatrol.gitlab.ci.runner.engine.components.RunnerConfigurationProvider;
import com.spotify.docker.client.ContainerNotFoundException;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.ImageNotFoundException;
import com.spotify.docker.client.LogMessage;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.messages.AuthConfig;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.ContainerInfo;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.ProgressMessage;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class DockerService {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private RunnerConfigurationProvider runnerConfigurationProvider;

    private DockerClient docker(){
        final DockerClient docker = DefaultDockerClient.builder().authConfig(
            AuthConfig.builder().serverAddress(
                runnerConfigurationProvider.get().getDockerRegistryURL()
            ).username(
                runnerConfigurationProvider.get().getDockerUsername()
            ).password(
                runnerConfigurationProvider.get().getDockerPassword()
            ).build()
        ).uri(
            runnerConfigurationProvider.get().getDockerURI()
        ).build();

        return docker;
    }

    public String createImage(File projectBuildDirectory, String containerName, final ProgressStateListener progressStateListener) {
        final DockerClient client = docker();

        String imageId = null;
        try {
            imageId = client.build(projectBuildDirectory.toPath(), containerName, new ModifiedProgressHandler(progressStateListener), DockerClient.BuildParameter.NO_RM);
        } catch (DockerException e) {
            log.error(e.getMessage(), e);
            progressStateListener.enterState(progressStateListener.fromStdErr(e.getMessage()));
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            progressStateListener.enterState(progressStateListener.fromStdErr(e.getMessage()));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            progressStateListener.enterState(progressStateListener.fromStdErr(e.getMessage()));
        }

        return imageId;
    }

    public void removeImage(String imageId) {
        final DockerClient client = docker();

        try{
            client.removeImage(imageId, true, false);
        }catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        } catch (DockerException e) {
            log.error(e.getMessage(), e);
        }
    }

    static class PullProgress{
        protected String id;
        protected String status;

        public PullProgress(String id, String status) {
            this.id = id;
            this.status = status;
        }
    }

    public void pull(String image) throws DockerException, InterruptedException {
        pull(image, new SimpleProgressStateListener());
    }
    public void pull(String image, final ProgressStateListener progressStateListener) throws DockerException, InterruptedException {
        final Map<String, PullProgress> progresses = new HashMap<String, PullProgress>();
        docker().pull(image, new ProgressHandler() {
            @Override
            public void progress(ProgressMessage message) throws DockerException {
                PullProgress pullProgress = progresses.get(message.id());

                if (pullProgress == null) {
                    pullProgress = new PullProgress(message.id(), message.status());
                    progresses.put(message.id(), pullProgress);

                    progressStateListener.enterState(
                            new ProgressStateListener.State(
                                    ProgressStateListener.State.Stream.STDOUT
                                    , String.format("%s%s\n", message.id() != null ? message.id() + ": " : "", message.status())
                            )
                    );
                } else {
                    if (!pullProgress.status.equals(message.status())) {
                        pullProgress.status = message.status();
                        progressStateListener.enterState(
                                new ProgressStateListener.State(
                                        ProgressStateListener.State.Stream.STDOUT
                                        , String.format("%s%s\n", message.id() != null ? message.id() + ": " : "", message.status())
                                )
                        );
                    }
                }
            }
        });
    }

    public boolean existsImage(String image){
        final DockerClient client = docker();

        try {
            client.inspectImage(image);
        }catch(ImageNotFoundException e){
            return false;
        } catch (InterruptedException e) {
            return false;
        } catch (DockerException e) {
            return false;
        }

        return true;
    }

    public ContainerInfo createContainer(String image, String containerName, String[] keepAliveCommand) throws DockerException, InterruptedException {
        Set<String> dockerVolumes = runnerConfigurationProvider.get().getDockerVolumes();
        if(dockerVolumes==null){
            dockerVolumes = new HashSet<String>();
        }
        final ContainerConfig containerConfig = ContainerConfig.builder()
                .image(image)
                .volumes(dockerVolumes)
                .cmd(keepAliveCommand) // keep container running
                .build();

        final DockerClient client = docker();

        final ContainerCreation creation = client.createContainer(containerConfig, containerName);
        final String id = creation.id();

        return docker().inspectContainer(id);
    }

    public void startContainer(String containerId) throws DockerException, InterruptedException {
        final DockerClient client = docker();

        HostConfig hostConfig = HostConfig.builder().privileged(true).build();
        
        client.startContainer(containerId, hostConfig);
    }

    public void execCreate(String containerId, String[] command, ProgressStateListener progressStateListener) throws DockerException, InterruptedException {
        final DockerClient client = docker();

        final String execId = client.execCreate(containerId, command, DockerClient.ExecParameter.STDOUT, DockerClient.ExecParameter.STDERR);
        final LogStream output = client.execStart(execId);

        while(output.hasNext()){
            LogMessage logMessage = output.next();

            final ByteBuffer clone = (logMessage.content().isDirect()) ?
            ByteBuffer.allocateDirect(logMessage.content().capacity()) :
            ByteBuffer.allocate(logMessage.content().capacity());

            logMessage.content().flip();
            clone.put(logMessage.content());
            clone.flip();

            if(logMessage.stream() == LogMessage.Stream.STDOUT){
                String fragment = new String(clone.array());

                progressStateListener.enterState(
                    new ProgressStateListener.State(
                        ProgressStateListener.State.Stream.STDOUT
                    ,   fragment
                    )
                );

                continue;
            }

            if(logMessage.stream() == LogMessage.Stream.STDERR){
                String fragment = new String(clone.array());

                progressStateListener.enterState(
                    new ProgressStateListener.State(
                        ProgressStateListener.State.Stream.STDERR
                    ,   fragment
                    )
                );

                break;
            }
        }
    }

    public void stopContainer(String containerId) throws DockerException, InterruptedException {
        final DockerClient client = docker();

        try{
            client.stopContainer(containerId, 1);
        }catch(ContainerNotFoundException e){
        }catch(Throwable throwable){
            log.error(throwable.getMessage());
        }

        try {
            client.killContainer(containerId);
        }catch(ContainerNotFoundException e){
        }catch(Throwable throwable){
            log.error(throwable.getMessage());
        }
    }

    public void removeContainer(String containerId) throws DockerException, InterruptedException {
        final DockerClient client = docker();

        try {
            client.removeContainer(containerId);
        }catch(ContainerNotFoundException e){
            // okay
        }
    }

    private static class ModifiedProgressHandler implements ProgressHandler{
        final Map<String, PullProgress> progresses = new HashMap<String, PullProgress>();

        private ProgressStateListener progressStateListener;

        public ModifiedProgressHandler(ProgressStateListener progressStateListener){
            this.progressStateListener = progressStateListener;
        }

        @Override
        public void progress(ProgressMessage message) throws DockerException {
            PullProgress currentProgress = progresses.get(message.id());

            if (currentProgress == null) {
                if(message.status()!=null) {
                    currentProgress = new PullProgress(message.id(), message.status());
                    progresses.put(message.id(), currentProgress);

                    progressStateListener.enterState(
                        progressStateListener.fromStdOut(String.format("%s%s\n", message.id() != null ? message.id() + ": " : "", message.status()))
                    );
                }
            } else {
                if (message!=null && message.status()!= null && message.status()!=null && currentProgress!=null && (currentProgress.status==null||!currentProgress.status.equals(message.status()))) {
                    currentProgress.status = message.status();
                    progressStateListener.enterState(
                        progressStateListener.fromStdOut(String.format("%s%s\n", message.id() != null ? message.id() + ": " : "", message.status()))
                    );
                }
            }
        }
    }

    private static class SimpleProgressStateListener extends ProgressStateListener{
        private Logger log = Logger.getLogger(getClass());

        @Override
        public void enterState(State state) {
            if(log.isDebugEnabled()){
                log.debug(state.getStream()+"|"+state.getMessage());
            }
        }
    }
}
