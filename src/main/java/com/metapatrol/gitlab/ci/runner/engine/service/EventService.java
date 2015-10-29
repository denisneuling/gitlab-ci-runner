package com.metapatrol.gitlab.ci.runner.engine.service;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.RegisterBuildResponsePayload;
import com.metapatrol.gitlab.ci.runner.engine.events.BuildFinishedEvent;
import com.metapatrol.gitlab.ci.runner.engine.events.BuildTriggerEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class EventService implements ApplicationEventPublisherAware {

    private ApplicationEventPublisher applicationEventPublisher;

    public void sendTriggerBuildEvent(RegisterBuildResponsePayload registerBuildResponsePayload) {
        BuildTriggerEvent buildTriggerEvent = new BuildTriggerEvent(this, registerBuildResponsePayload);

        applicationEventPublisher.publishEvent(buildTriggerEvent);
    }

    public void sendBuildFinishedEvent(String buildId, String projectName, String sha, boolean failed, String trace) {

        BuildFinishedEvent buildFinishedEvent = new BuildFinishedEvent(this, buildId, projectName, sha, failed, trace);
        applicationEventPublisher.publishEvent(buildFinishedEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
