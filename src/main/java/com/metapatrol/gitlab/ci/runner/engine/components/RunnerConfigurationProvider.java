package com.metapatrol.gitlab.ci.runner.engine.components;

import com.metapatrol.gitlab.ci.runner.configuration.RunnerConfiguration;
import org.apache.log4j.Logger;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class RunnerConfigurationProvider {
    private Logger log = Logger.getLogger(getClass());

    private RunnerConfiguration runnerConfiguration;

    public synchronized RunnerConfiguration get(){
        return runnerConfiguration;
    }

    public synchronized RunnerConfiguration set(RunnerConfiguration runnerConfiguration){
        return this.runnerConfiguration = runnerConfiguration;
    }
}
