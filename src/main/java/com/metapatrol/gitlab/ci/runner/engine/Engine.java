package com.metapatrol.gitlab.ci.runner.engine;

import com.metapatrol.gitlab.ci.runner.configuration.RunnerConfiguration;
import com.metapatrol.gitlab.ci.runner.engine.components.RunnerConfigurationProvider;
import com.metapatrol.gitlab.ci.runner.engine.components.RunnerConfigurationProviderFactoryBean;
import com.metapatrol.gitlab.ci.runner.engine.threads.BuilderThread;
import com.metapatrol.gitlab.ci.runner.engine.threads.FlusherThread;
import com.metapatrol.gitlab.ci.runner.engine.threads.ReaperThread;
import com.metapatrol.gitlab.ci.runner.engine.threads.ThreadFactory;
import com.metapatrol.gitlab.ci.runner.etc.Etc;
import com.metapatrol.gitlab.ci.runner.fs.FileSystem;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.support.ExecutorServiceAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SimpleThreadPoolTaskExecutor;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Configuration
@EnableScheduling
@ComponentScan(basePackageClasses = Engine.class)
public class Engine implements ApplicationListener<ContextRefreshedEvent>{
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
    public SimpleApplicationEventMulticaster applicationEventMulticaster(){
        SimpleApplicationEventMulticaster simpleApplicationEventMulticaster = new SimpleApplicationEventMulticaster();

        ThreadPoolTaskExecutor simpleApplicationEventMulticasterThreadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        simpleApplicationEventMulticasterThreadPoolTaskExecutor.setThreadNamePrefix("event-");
        simpleApplicationEventMulticasterThreadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        simpleApplicationEventMulticasterThreadPoolTaskExecutor.initialize();

        simpleApplicationEventMulticaster.setTaskExecutor(simpleApplicationEventMulticasterThreadPoolTaskExecutor);

        return simpleApplicationEventMulticaster;
    }

    @Bean
    @Autowired
    public AsyncTaskExecutor executorService(RunnerConfigurationProvider runnerConfigurationProvider){
        SimpleThreadPoolTaskExecutor simpleThreadPoolTaskExecutor = new SimpleThreadPoolTaskExecutor();
        simpleThreadPoolTaskExecutor.setWaitForJobsToCompleteOnShutdown(true);

        RunnerConfiguration runnerConfiguration = runnerConfigurationProvider.get();

        // multiplied by 5 due to buildthread, reaperthread, flusherthread and 2 backoff
        Integer threadCount = runnerConfiguration.getParallelBuilds() * 5;

        simpleThreadPoolTaskExecutor.setThreadCount(threadCount);
        simpleThreadPoolTaskExecutor.setThreadNamePrefix("builder");
        return simpleThreadPoolTaskExecutor;

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
}
