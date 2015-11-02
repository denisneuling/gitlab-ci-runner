package com.metapatrol.gitlab.ci.runner.engine.components;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class ErrorStateHolder {
    boolean errored = false;
    public synchronized boolean isErrored(){
        return errored;
    }
    public synchronized void setErrored(boolean errored){
        this.errored = errored;
    }
}
