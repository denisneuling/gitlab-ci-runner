package com.metapatrol.gitlab.ci.runner.engine.events;

import org.springframework.context.ApplicationEvent;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class BuildCanceledEvent extends ApplicationEvent {
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the component that published the event (never <code>null</code>)
     */
    public BuildCanceledEvent(Object source) {
        super(source);
    }
}
