package com.metapatrol.gitlab.ci.runner.engine.threads;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.RegisterBuildResponsePayload;
import com.metapatrol.gitlab.ci.runner.engine.components.ErrorStateHolder;
import com.metapatrol.gitlab.ci.runner.engine.components.MessageHolder;
import com.metapatrol.gitlab.ci.runner.engine.components.Tracer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class ThreadFactory implements ApplicationContextAware{

    private ApplicationContext applicationContext;

    public BuilderThread builderThread(final RegisterBuildResponsePayload registerBuildResponsePayload, final ErrorStateHolder errorStateHolder, final MessageHolder messageHolder){
        BuilderThread builderThread = applicationContext.getBean(BuilderThread.class);

        builderThread.setRegisterBuildResponsePayload(registerBuildResponsePayload);
        builderThread.setErrorStateHolder(errorStateHolder);
        builderThread.setMessageHolder(messageHolder);

        return builderThread;
    }

    public ReaperThread reaperThread(String buildId, Long timeoutInMs, final MessageHolder messageHolder, final ErrorStateHolder errorStateHolder){
        ReaperThread reaperThread = applicationContext.getBean(ReaperThread.class);
        reaperThread.setMessageHolder(messageHolder);
        reaperThread.setErrorStateHolder(errorStateHolder);
        reaperThread.setBuildId(buildId);
        reaperThread.setTimeout(timeoutInMs);

        return reaperThread;
    }

    public FlusherThread flusherThread(final MessageHolder messageHolder, final Tracer tracer){
        FlusherThread flusherThread = applicationContext.getBean(FlusherThread.class);
        flusherThread.setMessageHolder(messageHolder);
        flusherThread.setTracer(tracer);

        return flusherThread;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
