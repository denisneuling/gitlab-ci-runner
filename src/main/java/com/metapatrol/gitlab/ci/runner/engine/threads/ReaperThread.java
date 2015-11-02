package com.metapatrol.gitlab.ci.runner.engine.threads;

import com.metapatrol.gitlab.ci.runner.engine.components.ErrorStateHolder;
import com.metapatrol.gitlab.ci.runner.engine.components.MessageHolder;
import com.metapatrol.gitlab.ci.runner.engine.service.EventService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ReaperThread implements Runnable {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private EventService eventService;

    private transient String buildId;
    private transient Long timeout = 0L;
    private transient ErrorStateHolder errorStateHolder;
    private transient MessageHolder messageHolder;

    public ErrorStateHolder getErrorStateHolder() {
        return errorStateHolder;
    }

    public void setErrorStateHolder(ErrorStateHolder errorStateHolder) {
        this.errorStateHolder = errorStateHolder;
    }

    public MessageHolder getMessageHolder() {
        return messageHolder;
    }

    public void setMessageHolder(MessageHolder messageHolder) {
        this.messageHolder = messageHolder;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    public String getBuildId() {
        return buildId;
    }

    public void setBuildId(String buildId) {
        this.buildId = buildId;
    }

    @Override
    public void run() {
        try {
            doWork();
        } catch (InterruptedException e) {
            log.info("Reaper killed.");
        }
    }

    private void doWork() throws InterruptedException {
        Thread.sleep(timeout);

        errorStateHolder.setErrored(true);

        eventService.sendBuildFinishedEvent(buildId, errorStateHolder.isErrored(), messageHolder.getMessages());
    }
}
