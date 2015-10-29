package com.metapatrol.gitlab.ci.runner.etc.format.yaml;

import com.metapatrol.gitlab.ci.runner.etc.format.impl.FormatWriterImpl;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class YamlFormatWriter extends FormatWriterImpl {

    @Override
    public String[] canHandleFileNameSuffixes() {
        return YamlFormat.SUFFIXES;
    }

    @Override
    public <T> T write(File file, T confObject) throws IOException {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setSplitLines(true);
        /*
        options.setWidth(80);
        options.setIndent(4);
        options.setPrettyFlow(true);
        options.setSplitLines(true);
        options.setExplicitStart(true);
        options.setExplicitEnd(true);
        */
        options.setAllowUnicode(true);

        Yaml yaml = new Yaml(options);
        yaml.dump(confObject, new FileWriter(file));
        return confObject;
    }
}
