package com.metapatrol.gitlab.ci.runner.engine.service;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.RegisterBuildResponsePayload;
import com.metapatrol.gitlab.ci.runner.engine.events.BuildFinishedEvent;
import com.metapatrol.gitlab.ci.runner.engine.threads.BuilderThread;
import com.metapatrol.gitlab.ci.runner.engine.components.ErrorStateHolder;
import com.metapatrol.gitlab.ci.runner.engine.threads.FlusherThread;
import com.metapatrol.gitlab.ci.runner.engine.components.MessageHolder;
import com.metapatrol.gitlab.ci.runner.engine.threads.ReaperThread;
import com.metapatrol.gitlab.ci.runner.engine.threads.ThreadFactory;
import com.metapatrol.gitlab.ci.runner.engine.components.Tracer;
import com.spotify.docker.client.DockerException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class BuildService  implements DisposableBean, ApplicationListener<BuildFinishedEvent> {

    @Autowired
    private GitlabCIService gitlabCIService;

    @Autowired
    private AsyncTaskExecutor asyncTaskExecutor;

    @Autowired
    private ThreadFactory threadFactory;

    private Map<String, Futures> futureMap = new ConcurrentHashMap<String, Futures>();

    @Override
    public void destroy() throws Exception {
        for(Futures futures: futureMap.values()){
            futures.cancelAll();
        }
    }

    @Override
    public void onApplicationEvent(BuildFinishedEvent event) {
        String buildId = event.getBuildId();
        Futures futures = futureMap.get(buildId);
        if(futures!=null) {
            futures.cancelAll();
            futureMap.remove(buildId);
        }
    }

    public void build(final RegisterBuildResponsePayload registerBuildResponsePayload) throws DockerException, InterruptedException {
        final Long secondsToTimeoutBuild = registerBuildResponsePayload.getTimeout() * 1000L;

        final ErrorStateHolder errorStateHolder = new ErrorStateHolder();
        final MessageHolder messageHolder = new MessageHolder();
        final Tracer tracer = new Tracer(registerBuildResponsePayload.getId(), gitlabCIService);

        FlusherThread flusherThread = threadFactory.flusherThread(
            messageHolder
        ,   tracer
        );

        ReaperThread reaperThread = threadFactory.reaperThread(
            registerBuildResponsePayload.getId()
        ,   secondsToTimeoutBuild
        ,   messageHolder
        ,   errorStateHolder
        );

        BuilderThread builderThread = threadFactory.builderThread(
            registerBuildResponsePayload
        ,   errorStateHolder
        ,   messageHolder
        );

        final Futures futures = new Futures();
        futureMap.put(
            registerBuildResponsePayload.getId()
        ,   futures
        );

        futures.flusherFuture = asyncTaskExecutor.submit(flusherThread);
        futures.reaperFuture = asyncTaskExecutor.submit(reaperThread);
        futures.buildFuture= asyncTaskExecutor.submit(builderThread);
    }

    protected static class Futures {
        public Future<?> flusherFuture;
        public Future<?> buildFuture;
        public Future<?> reaperFuture;

        public void cancelAll(){
            if(buildFuture!=null) {
                buildFuture.cancel(true);
            }
            if(flusherFuture!=null) {
                flusherFuture.cancel(true);
            }
            if(reaperFuture!=null) {
                reaperFuture.cancel(true);
            }
        }
    }
}
