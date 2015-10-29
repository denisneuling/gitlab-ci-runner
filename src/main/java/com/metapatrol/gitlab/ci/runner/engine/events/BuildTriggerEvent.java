package com.metapatrol.gitlab.ci.runner.engine.events;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.BuildPayload;
import org.springframework.context.ApplicationEvent;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class BuildTriggerEvent extends ApplicationEvent {
    private BuildPayload payload;
    public BuildTriggerEvent(Object source, BuildPayload payload) {
        super(source);

        this.payload = payload;
    }

    public BuildPayload getPayload() {
        return payload;
    }

    public void setPayload(BuildPayload payload) {
        this.payload = payload;
    }
}
