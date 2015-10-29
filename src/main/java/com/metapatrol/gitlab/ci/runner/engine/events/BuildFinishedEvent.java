package com.metapatrol.gitlab.ci.runner.engine.events;

import org.springframework.context.ApplicationEvent;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class BuildFinishedEvent extends ApplicationEvent {
    private boolean failed;
    private String trace;
    private String buildId;
    private String projectName;
    private String sha;

    public BuildFinishedEvent(Object source, String buildId, String projectName, String sha, boolean failed, String trace) {
        super(source);

        this.buildId = buildId;
        this.projectName = projectName;
        this.sha = sha;
        this.failed = failed;
        this.trace = trace;
    }

    public String getBuildId() {
        return buildId;
    }

    public boolean isFailed() {
        return failed;
    }

    public String getTrace() {
        return trace;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getSha() {
        return sha;
    }
}
