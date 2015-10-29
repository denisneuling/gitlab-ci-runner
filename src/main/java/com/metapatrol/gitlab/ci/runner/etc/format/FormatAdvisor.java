package com.metapatrol.gitlab.ci.runner.etc.format;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public interface FormatAdvisor {

    public FormatWriter resolveWriter(String fileName);

    public FormatReader resolveReader(String fileName);
}
