package com.metapatrol.gitlab.ci.runner.etc.format;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public interface SuffixAware {

    public String[] canHandleFileNameSuffixes();
}
