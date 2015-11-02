package com.metapatrol.gitlab.ci.runner.engine.threads;

import com.metapatrol.gitlab.ci.runner.engine.components.MessageHolder;
import com.metapatrol.gitlab.ci.runner.engine.components.Tracer;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class FlusherThread implements Runnable {
    private Logger log = Logger.getLogger(getClass());

    private transient Tracer tracer;
    private transient MessageHolder messageHolder;

    public MessageHolder getMessageHolder() {
        return messageHolder;
    }

    public void setMessageHolder(MessageHolder messageHolder) {
        this.messageHolder = messageHolder;
    }

    public Tracer getTracer() {
        return tracer;
    }

    public void setTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void run() {
        log.info("Flusher started.");
        try {
            doWork();
        } catch (InterruptedException e) {
            log.info("Flusher killed.");
        }
    }

    private void doWork() throws InterruptedException {
        while(true){
            Thread.sleep(2000);

            if(messageHolder.getLength()>0){
                String messages = messageHolder.getMessages();
                tracer.remoteTrace(messages);
            }
        }
    }

}
