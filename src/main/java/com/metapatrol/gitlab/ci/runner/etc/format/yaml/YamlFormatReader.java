package com.metapatrol.gitlab.ci.runner.etc.format.yaml;

import com.metapatrol.gitlab.ci.runner.etc.format.impl.FormatReaderImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class YamlFormatReader extends FormatReaderImpl {

    @Override
    public String[] canHandleFileNameSuffixes() {
        return YamlFormat.SUFFIXES;
    }

    @Override
    public <T> T read(File file, T confObject) throws IOException {
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

        try {
            BeanUtils.copyProperties(confObject, (T) yaml.loadAs(new FileReader(file), confObject.getClass()));
        } catch (ReflectiveOperationException e) {
            throw new IOException(e.getMessage(), e);
        }

        return confObject;
    }
}
