package com.metapatrol.gitlab.ci.runner.engine.service;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.BuildPayload;
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

    public void sendTriggerBuildEvent(BuildPayload buildPayload) {
        BuildTriggerEvent buildTriggerEvent = new BuildTriggerEvent(this, buildPayload);

        applicationEventPublisher.publishEvent(buildTriggerEvent);
    }

    public void sendBuildFinishedEvent(BuildPayload buildPayload, boolean failed) {

        BuildFinishedEvent buildFinishedEvent = new BuildFinishedEvent(this, buildPayload, failed);

        applicationEventPublisher.publishEvent(buildFinishedEvent);
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }
}
