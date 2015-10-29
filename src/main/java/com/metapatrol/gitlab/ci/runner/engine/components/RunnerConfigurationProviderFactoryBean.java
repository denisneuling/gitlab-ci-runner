package com.metapatrol.gitlab.ci.runner.engine.components;

import com.metapatrol.gitlab.ci.runner.configuration.RunnerConfiguration;
import com.metapatrol.gitlab.ci.runner.etc.Etc;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class RunnerConfigurationProviderFactoryBean implements FactoryBean<RunnerConfigurationProvider> {

    @Autowired
    private Etc etc;

    @Override
    public RunnerConfigurationProvider getObject() throws Exception {
        RunnerConfigurationProvider runnerConfigurationProvider = new RunnerConfigurationProvider();
        RunnerConfiguration runnerConfiguration = new RunnerConfiguration();
        runnerConfigurationProvider.set(etc.read(runnerConfiguration));
        return runnerConfigurationProvider;
    }

    @Override
    public Class<?> getObjectType() {
        return RunnerConfigurationProvider.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
