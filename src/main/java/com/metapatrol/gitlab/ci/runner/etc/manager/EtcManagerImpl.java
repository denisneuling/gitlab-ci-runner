package com.metapatrol.gitlab.ci.runner.etc.manager;

import com.metapatrol.gitlab.ci.runner.etc.component.ConfObjectProcessor;
import com.metapatrol.gitlab.ci.runner.etc.exception.UnsupportedFormatException;
import com.metapatrol.gitlab.ci.runner.etc.format.FormatAdvisor;
import com.metapatrol.gitlab.ci.runner.etc.format.FormatReader;
import com.metapatrol.gitlab.ci.runner.etc.format.FormatWriter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class EtcManagerImpl implements EtcManager {

    private FormatAdvisor formatAdvisor;
    private ConfObjectProcessor confObjectProcessor;

    public EtcManagerImpl(
        Set<FormatWriter> formatWriters
    ,   Set<FormatReader> formatReaders
    ,   ConfObjectProcessor confObjectProcessor
    ,   FormatAdvisor formatAdvisor
    ){
        this.formatWriters = formatWriters;
        this.formatReaders = formatReaders;
        this.confObjectProcessor = confObjectProcessor;
        this.formatAdvisor = formatAdvisor;
    }

    private Set<FormatWriter> formatWriters = new HashSet<FormatWriter>();
    private Set<FormatReader> formatReaders = new HashSet<FormatReader>();

    @Override
    public <T> T write(String path, T confObject) throws IOException {
        File file = new File(path);
        return write(file, confObject);
    }

    @Override
    public <T> T read(String path, T confObject) throws IOException {
        File file = new File(path);
        return read(file, confObject);
    }

    @Override
    public <T> T write(File file, T confObject) throws IOException {
        FormatWriter formatWriter = formatAdvisor.resolveWriter(file.getAbsolutePath());
        if(formatWriter==null){
            throw new UnsupportedFormatException(file.getAbsolutePath());
        }

        if(!file.exists()){
            file.createNewFile();
        }

        confObject = confObjectProcessor.process(confObject);

        return formatWriter.write(file, confObject);
    }

    @Override
    public <T> T read(File file, T confObject) throws IOException {
        FormatReader formatReader = formatAdvisor.resolveReader(file.getAbsolutePath());
        if(formatReader==null){
            throw new UnsupportedFormatException(file.getAbsolutePath());
        }

        if(!file.exists()){
            return write(file, confObject);
        }

        confObject = formatReader.read(file, confObject);
        confObject = confObjectProcessor.process(confObject);

        return confObject;
    }
}
