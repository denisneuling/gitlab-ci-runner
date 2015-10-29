package com.metapatrol.gitlab.ci.runner.etc.component;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public interface ConfObjectProcessor {

    public <T> T process(T object);
}
