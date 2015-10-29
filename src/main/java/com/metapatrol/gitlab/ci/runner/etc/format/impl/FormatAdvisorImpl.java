package com.metapatrol.gitlab.ci.runner.etc.format.impl;

import com.metapatrol.gitlab.ci.runner.etc.format.FormatAdvisor;
import com.metapatrol.gitlab.ci.runner.etc.format.FormatReader;
import com.metapatrol.gitlab.ci.runner.etc.format.FormatWriter;

import java.util.Set;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class FormatAdvisorImpl implements FormatAdvisor{

    private Set<FormatReader> formatReaders;
    private Set<FormatWriter> formatWriters;

    public FormatAdvisorImpl(Set<FormatReader> formatReaders, Set<FormatWriter> formatWriters){
        this.formatReaders = formatReaders;
        this.formatWriters = formatWriters;
    }

    @Override
    public FormatWriter resolveWriter(String fileName) {
        for(FormatWriter writer : formatWriters){
            for(String suffix : writer.canHandleFileNameSuffixes()){
                if(fileName.endsWith(suffix)){
                    return writer;
                }
            }
        }
        return null;
    }

    @Override
    public FormatReader resolveReader(String fileName) {
        for(FormatReader reader : formatReaders){
            for(String suffix : reader.canHandleFileNameSuffixes()){
                if(fileName.endsWith(suffix)){
                    return reader;
                }
            }
        }
        return null;
    }
}
