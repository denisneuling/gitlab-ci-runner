package com.metapatrol.gitlab.ci.runner.engine;

import com.metapatrol.gitlab.ci.runner.engine.components.RunnerConfigurationProviderFactoryBean;
import com.metapatrol.gitlab.ci.runner.etc.Etc;
import com.metapatrol.gitlab.ci.runner.fs.FileSystem;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Configuration
@EnableScheduling
@ComponentScan(basePackageClasses = Engine.class)
public class Engine implements ApplicationListener<ContextRefreshedEvent>, DisposableBean{
    private Logger log = Logger.getLogger(getClass());

    @Bean
    public FileSystem fileSystem() throws IOException {
        return FileSystem.getInstance();
    }

    @Bean
    public Etc etc() throws IOException {
        return Etc.getInstance();
    }

    @Bean
    public RunnerConfigurationProviderFactoryBean runnerConfigurationProvider(){
        return new RunnerConfigurationProviderFactoryBean();
    }

    @Bean
    public SimpleAsyncTaskExecutor simpleAsyncTaskExecutor(){
        return new SimpleAsyncTaskExecutor();
    }

    public void start(){
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext(
            Engine.class
        );
        annotationConfigApplicationContext.registerShutdownHook();
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info("Gitlab CI Runner ready and waiting for work.");
    }

    @Override
    public void destroy() throws Exception {
        log.info("Bye.");
    }
}
