package com.metapatrol.gitlab.ci.runner.entrypoint;

import com.metapatrol.gitlab.ci.runner.client.GitlabCIClient;
import com.metapatrol.gitlab.ci.runner.client.messages.common.HttpStatus;
import com.metapatrol.gitlab.ci.runner.client.messages.request.RegisterRunnerRequest;
import com.metapatrol.gitlab.ci.runner.client.messages.response.RegisterRunnerResponse;
import com.metapatrol.gitlab.ci.runner.commands.RegisterCommand;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.RegisterRunnerResponsePayload;
import com.metapatrol.gitlab.ci.runner.configuration.RunnerConfiguration;
import com.metapatrol.gitlab.ci.runner.etc.Etc;
import org.apache.log4j.Logger;

import java.io.IOException;


/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class RegisterEntryPoint extends EntryPoint<RegisterCommand> {
    private Logger log = Logger.getLogger(getClass());

    @Override
    public boolean enter(RegisterCommand command) {

        GitlabCIClient gitlabCIClient = new GitlabCIClient(command.getUrl());
        gitlabCIClient.setURL(command.getUrl());

        RegisterRunnerRequest registerRunnerRequest = new RegisterRunnerRequest();
        registerRunnerRequest.setTagList(command.getTags());
        registerRunnerRequest.setToken(command.getToken());
        registerRunnerRequest.setDescription(command.getDescription());

        RegisterRunnerResponse registerRunnerResponse = gitlabCIClient.send(registerRunnerRequest);

        HttpStatus httpStatus = HttpStatus.getStatus(registerRunnerResponse.getStatusCode());
        if(httpStatus.isError()) {
            log.fatal(httpStatus.toString());
            return false;
        }

        RegisterRunnerResponsePayload payload = registerRunnerResponse.getPayload();

        RunnerConfiguration runnerConfiguration = new RunnerConfiguration();
        runnerConfiguration.setDescription(command.getDescription());
        runnerConfiguration.setTags(command.getTags());
        runnerConfiguration.setId(payload.getId());
        runnerConfiguration.setToken(payload.getToken());
        runnerConfiguration.setUrl(command.getUrl().toString());

        try {
            Etc.getInstance().write(runnerConfiguration);
        } catch (IOException e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }
}
