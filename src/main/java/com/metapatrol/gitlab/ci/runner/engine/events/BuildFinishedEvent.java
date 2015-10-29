package com.metapatrol.gitlab.ci.runner.engine.events;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.BuildPayload;
import org.springframework.context.ApplicationEvent;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class BuildFinishedEvent extends ApplicationEvent {
    private BuildPayload payload;
    private boolean failed;
    public BuildFinishedEvent(Object source, BuildPayload payload, boolean failed) {
        super(source);

        this.payload = payload;
        this.failed = failed;
    }

    public BuildPayload getPayload() {
        return payload;
    }

    public void setPayload(BuildPayload payload) {
        this.payload = payload;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }
}
