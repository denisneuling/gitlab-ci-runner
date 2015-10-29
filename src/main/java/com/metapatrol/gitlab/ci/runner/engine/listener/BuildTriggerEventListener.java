package com.metapatrol.gitlab.ci.runner.engine.listener;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.BuildPayload;
import com.metapatrol.gitlab.ci.runner.engine.components.RunnerConfigurationProvider;
import com.metapatrol.gitlab.ci.runner.engine.components.StateMachine;
import com.metapatrol.gitlab.ci.runner.engine.events.BuildTriggerEvent;
import com.metapatrol.gitlab.ci.runner.engine.service.BuildService;
import com.metapatrol.gitlab.ci.runner.engine.service.GitlabCIService;
import com.spotify.docker.client.DockerException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class BuildTriggerEventListener implements ApplicationListener<BuildTriggerEvent> {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private BuildService buildService;

    @Autowired
    private RunnerConfigurationProvider runnerConfigurationProvider;

    @Autowired
    private StateMachine stateMachine;

    @Autowired
    private GitlabCIService gitlabCIService;

    @Override
    public void onApplicationEvent(BuildTriggerEvent buildTriggerEvent) {
        BuildPayload buildPayload = buildTriggerEvent.getPayload();

        gitlabCIService.updateBuild(buildPayload.getId(), "running", null);

        log.info("Building "+buildPayload.getProjectName()+" @ " + buildPayload.getSha()+ " ... (Worker "+stateMachine.getRunningBuilds()+" of "+runnerConfigurationProvider.get().getParallelBuilds()+")");

        try {
            buildService.build(buildTriggerEvent.getPayload());
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
