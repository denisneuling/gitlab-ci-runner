package com.metapatrol.gitlab.ci.runner.engine.service;

import com.metapatrol.gitlab.ci.runner.client.GitlabCIClient;
import com.metapatrol.gitlab.ci.runner.client.messages.request.UpdateBuildRequest;
import com.metapatrol.gitlab.ci.runner.client.messages.response.UpdateBuildResponse;
import com.metapatrol.gitlab.ci.runner.engine.components.RunnerConfigurationProvider;
import com.metapatrol.gitlab.ci.runner.engine.components.StateMachine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class GitlabCIService {

    @Autowired
    private RunnerConfigurationProvider runnerConfigurationProvider;

    public GitlabCIClient gitlabCIClient(){
        return new GitlabCIClient(runnerConfigurationProvider.get().getUrl());
    }

    public void updateBuild(String buildId, String state, String trace){
        UpdateBuildRequest updateBuildRequest = new UpdateBuildRequest();
        updateBuildRequest.setToken(runnerConfigurationProvider.get().getToken());
        updateBuildRequest.setId(buildId);
        updateBuildRequest.setState(state);
        updateBuildRequest.setTrace(trace);

        gitlabCIClient().send(updateBuildRequest);
    }
}
