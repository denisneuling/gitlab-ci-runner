package com.metapatrol.gitlab.ci.runner.etc;

import com.metapatrol.gitlab.ci.runner.etc.component.ConfObjectProcessor;
import com.metapatrol.gitlab.ci.runner.etc.component.impl.ConfObjectProcessorImpl;
import com.metapatrol.gitlab.ci.runner.etc.converters.StringBooleanConverter;
import com.metapatrol.gitlab.ci.runner.etc.converters.StringConverter;
import com.metapatrol.gitlab.ci.runner.etc.converters.StringIntegerConverter;
import com.metapatrol.gitlab.ci.runner.etc.converters.StringStringConverter;
import com.metapatrol.gitlab.ci.runner.etc.format.FormatAdvisor;
import com.metapatrol.gitlab.ci.runner.etc.format.FormatReader;
import com.metapatrol.gitlab.ci.runner.etc.format.FormatWriter;
import com.metapatrol.gitlab.ci.runner.etc.format.impl.FormatAdvisorImpl;
import com.metapatrol.gitlab.ci.runner.etc.format.ini.IniFormatReader;
import com.metapatrol.gitlab.ci.runner.etc.format.ini.IniFormatWriter;
import com.metapatrol.gitlab.ci.runner.etc.format.json.JsonFormatReader;
import com.metapatrol.gitlab.ci.runner.etc.format.json.JsonFormatWriter;
import com.metapatrol.gitlab.ci.runner.etc.format.yaml.YamlFormatReader;
import com.metapatrol.gitlab.ci.runner.etc.format.yaml.YamlFormatWriter;
import com.metapatrol.gitlab.ci.runner.etc.manager.EtcManager;
import com.metapatrol.gitlab.ci.runner.etc.manager.EtcManagerImpl;
import com.metapatrol.gitlab.ci.runner.fs.FileSystem;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class Etc {

    private static File configurationFile;

    private static Etc INSTANCE;

    private EtcManager etcManager;

    private Etc(){}
    public static Etc getInstance() throws IOException {
        if(INSTANCE == null){
            INSTANCE = new Etc();

            INSTANCE.setUp();
            // TODO SETUP
        }

        return INSTANCE;
    }

    protected void setUp() throws IOException {
        Set<FormatWriter> formatWriters = new HashSet<FormatWriter>();
        formatWriters.add(new IniFormatWriter());
        formatWriters.add(new YamlFormatWriter());
        formatWriters.add(new JsonFormatWriter());

        Set<FormatReader> formatReaders = new HashSet<FormatReader>();
        formatReaders.add(new IniFormatReader());
        formatReaders.add(new YamlFormatReader());
        formatReaders.add(new JsonFormatReader());

        List<StringConverter> stringConverters = new LinkedList<StringConverter>();
        stringConverters.add(new StringBooleanConverter());
        stringConverters.add(new StringIntegerConverter());
        stringConverters.add(new StringStringConverter());

        FormatAdvisor formatAdvisor = new FormatAdvisorImpl(formatReaders, formatWriters);

        ConfObjectProcessor confObjectProcessor = new ConfObjectProcessorImpl(stringConverters);

        etcManager = new EtcManagerImpl(
            formatWriters
        ,   formatReaders
        ,   confObjectProcessor
        ,   formatAdvisor
        );
    }

    public <T> T write(T confObject) throws IOException {
        return etcManager.write(FileSystem.getInstance().getConfigurationFile(), confObject);
    }

    public <T> T read(T confObject) throws IOException {
        return etcManager.read(FileSystem.getInstance().getConfigurationFile(), confObject);
    }
    // delegate end
}
