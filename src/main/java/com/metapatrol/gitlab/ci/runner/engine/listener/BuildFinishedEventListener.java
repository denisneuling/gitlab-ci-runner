package com.metapatrol.gitlab.ci.runner.engine.listener;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.BuildPayload;
import com.metapatrol.gitlab.ci.runner.engine.components.RunnerConfigurationProvider;
import com.metapatrol.gitlab.ci.runner.engine.components.StateMachine;
import com.metapatrol.gitlab.ci.runner.engine.events.BuildFinishedEvent;
import com.metapatrol.gitlab.ci.runner.engine.service.GitlabCIService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class BuildFinishedEventListener implements ApplicationListener<BuildFinishedEvent> {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private StateMachine stateMachine;

    @Autowired
    private RunnerConfigurationProvider runnerConfigurationProvider;

    @Autowired
    private GitlabCIService gitlabCIService;

    @Override
    public void onApplicationEvent(BuildFinishedEvent buildFinishedEvent) {
        BuildPayload buildPayload = buildFinishedEvent.getPayload();

        if(buildFinishedEvent.isFailed()) {
            gitlabCIService.updateBuild(buildPayload.getId(), "failed", null);
        }else{
            gitlabCIService.updateBuild(buildPayload.getId(), "success", null);
        }

        log.info("Building " +buildPayload.getProjectName()+" @ " + buildPayload.getSha() + (buildFinishedEvent.isFailed()?" failed.":" succeeded.") + " (Worker "+stateMachine.getRunningBuilds()+" of "+runnerConfigurationProvider.get().getParallelBuilds()+")");

        stateMachine.release();
    }
}
