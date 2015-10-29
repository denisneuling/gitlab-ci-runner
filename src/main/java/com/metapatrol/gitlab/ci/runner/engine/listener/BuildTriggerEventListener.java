package com.metapatrol.gitlab.ci.runner.engine.listener;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.constants.BuildState;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.RegisterBuildResponsePayload;
import com.metapatrol.gitlab.ci.runner.engine.components.RunnerConfigurationProvider;
import com.metapatrol.gitlab.ci.runner.engine.components.StateMachine;
import com.metapatrol.gitlab.ci.runner.engine.events.BuildTriggerEvent;
import com.metapatrol.gitlab.ci.runner.engine.service.BuildService;
import com.metapatrol.gitlab.ci.runner.engine.service.GitlabCIService;
import com.spotify.docker.client.DockerException;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
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
        MDC.put("project", String.format("[%s] ", buildTriggerEvent.getPayload().getProjectName()));
        MDC.put("sha", String.format("[%s] ", buildTriggerEvent.getPayload().getSha()));
        MDC.put("build", String.format("[%s] ", buildTriggerEvent.getPayload().getId()));

        RegisterBuildResponsePayload registerBuildResponsePayload = buildTriggerEvent.getPayload();

        gitlabCIService.updateBuild(registerBuildResponsePayload.getId(), BuildState.running, null);

        log.info("Building...");

        try {
            buildService.build(buildTriggerEvent.getPayload());
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
