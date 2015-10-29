package com.metapatrol.gitlab.ci.runner.engine.events;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.RegisterBuildResponsePayload;
import org.springframework.context.ApplicationEvent;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class BuildTriggerEvent extends ApplicationEvent {
    private RegisterBuildResponsePayload payload;
    public BuildTriggerEvent(Object source, RegisterBuildResponsePayload payload) {
        super(source);

        this.payload = payload;
    }

    public RegisterBuildResponsePayload getPayload() {
        return payload;
    }
}
