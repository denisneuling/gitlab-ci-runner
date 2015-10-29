package com.metapatrol.gitlab.ci.runner.engine.components;

import com.metapatrol.gitlab.ci.runner.configuration.RunnerConfiguration;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class RunnerConfigurationProvider {
    private RunnerConfiguration runnerConfiguration;

    public synchronized RunnerConfiguration get(){
        return runnerConfiguration;
    }

    public synchronized RunnerConfiguration set(RunnerConfiguration runnerConfiguration){
        return this.runnerConfiguration = runnerConfiguration;
    }
}
