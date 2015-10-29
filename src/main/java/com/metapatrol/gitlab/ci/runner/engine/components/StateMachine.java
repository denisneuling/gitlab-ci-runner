package com.metapatrol.gitlab.ci.runner.engine.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class StateMachine {

    private State state = State.AVAILABLE;

    private Integer runningBuilds = 0;

    @Autowired
    private RunnerConfigurationProvider runnerConfigurationProvider;

    public synchronized State take(){
        runningBuilds++;

        Integer maxParallelBuilds = runnerConfigurationProvider.get().getParallelBuilds();

        State currentState = null;

        if(runningBuilds > maxParallelBuilds){
            runningBuilds = maxParallelBuilds;

            currentState = setState(State.FULL);

        }else if(runningBuilds == maxParallelBuilds){
            currentState = getState();
            setState(State.FULL);
        }else{
            currentState = setState(State.AVAILABLE);
        }

        return currentState;
    }

    public synchronized State release(){
        runningBuilds--;
        if(runningBuilds <= 0){
            runningBuilds = 0;
        }

        Integer maxParallelBuilds = runnerConfigurationProvider.get().getParallelBuilds();

        if(runningBuilds >= maxParallelBuilds){
            runningBuilds = maxParallelBuilds;

            setState(State.FULL);
        }else{
            setState(State.AVAILABLE);
        }

        return getState();
    }

    public Integer getRunningBuilds() {
        return runningBuilds;
    }

    private synchronized State setState(State state){
        this.state = state;
        return state;
    }

    public synchronized State getState(){
        return state;
    }

    public enum State{
        AVAILABLE,
        FULL
    }
}
