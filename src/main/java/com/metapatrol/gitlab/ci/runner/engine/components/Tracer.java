package com.metapatrol.gitlab.ci.runner.engine.components;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.constants.BuildState;
import com.metapatrol.gitlab.ci.runner.engine.service.GitlabCIService;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class Tracer {
    private GitlabCIService gitlabCIService;
    private String buildId;
    public Tracer(String buildId, GitlabCIService gitlabCIService){
        this.gitlabCIService = gitlabCIService;
        this.buildId = buildId;
    }
    public void remoteTrace(String trace){
        if(trace!=null) {
            gitlabCIService.updateBuild(buildId, BuildState.running, trace);
        }
    }
}
