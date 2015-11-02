package com.metapatrol.gitlab.ci.runner.engine.listener;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.constants.BuildState;
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
        if(buildFinishedEvent.isFailed()) {
            gitlabCIService.updateBuild(buildFinishedEvent.getBuildId(), BuildState.failed, buildFinishedEvent.getTrace());
        }else{
            gitlabCIService.updateBuild(buildFinishedEvent.getBuildId(), BuildState.success, buildFinishedEvent.getTrace());
        }

        log.info("Building " +buildFinishedEvent.getBuildId() +(buildFinishedEvent.isFailed() ? " failed." : " succeeded."));

        stateMachine.release();
    }
}
