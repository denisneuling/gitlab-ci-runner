package com.metapatrol.gitlab.ci.runner.engine.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metapatrol.gitlab.ci.runner.client.GitlabCIClient;
import com.metapatrol.gitlab.ci.runner.client.messages.common.HttpStatus;
import com.metapatrol.gitlab.ci.runner.client.messages.request.RegisterBuildRequest;
import com.metapatrol.gitlab.ci.runner.client.messages.response.RegisterBuildResponse;
import com.metapatrol.gitlab.ci.runner.engine.components.RunnerConfigurationProvider;
import com.metapatrol.gitlab.ci.runner.engine.components.StateMachine;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class GitlabCIBuildsRegisterPollingService {
    private Logger log = Logger.getLogger(getClass());

    @Autowired
    private RunnerConfigurationProvider runnerConfigurationProvider;

    @Autowired
    private StateMachine stateMachine;

    @Autowired
    private EventService eventService;

    @Autowired
    private SimpleAsyncTaskExecutor simpleAsyncTaskExecutor;

    public GitlabCIClient gitlabCIClient(){
        return new GitlabCIClient(runnerConfigurationProvider.get().getUrl());
    }

    @Scheduled(fixedDelay = 5 * 1000)
    public void awaitBuild(){
        if(stateMachine.getState() == StateMachine.State.AVAILABLE && stateMachine.take() == StateMachine.State.AVAILABLE){
            RegisterBuildRequest registerBuildRequest = new RegisterBuildRequest();
            registerBuildRequest.setToken(runnerConfigurationProvider.get().getToken());
            final RegisterBuildResponse registerBuildResponse = gitlabCIClient().send(registerBuildRequest);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            log.info(gson.toJson(gson.fromJson(registerBuildResponse.getResult(), Map.class)));

            HttpStatus status = HttpStatus.getStatus(registerBuildResponse.getStatusCode());
            if(!status.isError()) {
                simpleAsyncTaskExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        eventService.sendTriggerBuildEvent(registerBuildResponse.getPayload());
                    }
                });
            }else{
                stateMachine.release();
            }
        }
    }

}
