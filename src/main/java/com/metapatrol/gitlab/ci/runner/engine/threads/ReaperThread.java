package com.metapatrol.gitlab.ci.runner.engine.threads;

import com.metapatrol.gitlab.ci.runner.engine.components.ErrorStateHolder;
import com.metapatrol.gitlab.ci.runner.engine.components.MessageHolder;
import com.metapatrol.gitlab.ci.runner.engine.service.EventService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.ansi;

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
    private transient String projectName;
    private transient String sha;
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

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public void run() {
        if(log.isDebugEnabled()){
            log.debug("Reaper started.");
        }
        try {
            doWork();
        } catch (InterruptedException e) {
            if(log.isDebugEnabled()){
                log.debug("Reaper killed.");
            }
        }
        if(log.isDebugEnabled()){
            log.debug("Reaper exited.");
        }
    }

    private void doWork() throws InterruptedException {
        Thread.sleep(timeout);

        errorStateHolder.setErrored(true);

        messageHolder.append(ansi().fg(RED).a("Build timed out.").reset().toString());

        eventService.sendBuildFinishedEvent(buildId, projectName, sha, errorStateHolder.isErrored(), messageHolder.getMessages());
    }
}
