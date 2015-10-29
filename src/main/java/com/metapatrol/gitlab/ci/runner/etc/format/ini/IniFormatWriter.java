package com.metapatrol.gitlab.ci.runner.etc.format.ini;

import com.metapatrol.gitlab.ci.runner.etc.annotation.Embedded;
import com.metapatrol.gitlab.ci.runner.etc.format.impl.FormatWriterImpl;
import org.apache.log4j.Logger;
import org.ini4j.Config;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class IniFormatWriter extends FormatWriterImpl {
    private Logger log = Logger.getLogger(getClass());

    @Override
    public String[] canHandleFileNameSuffixes() {
        return IniFormat.SUFFIXES;
    }

    @Override
    public <T> T write(File file, T confObject) throws IOException {
        Config cfg = new Config();

        cfg.setMultiOption(true);
        Ini ini = new Ini();

        ini.setConfig(cfg);

        for(Field field : confObject.getClass().getDeclaredFields()){
            if(field.isAnnotationPresent(Embedded.class)){
                field.setAccessible(true);
                Object to = null;
                try {
                    to = field.get(confObject);
                    if(to!=null) {
                        Ini.Section sec = ini.add(field.getName());
                        sec.from(to);
                    }
                } catch (IllegalAccessException e) {
                    if(log.isDebugEnabled()){
                        log.debug(String.format("Could not set '%s': ", field.getName(), e.getMessage()));
                    }
                }
            }
        }

        ini.store(file);

        return confObject;
    }
}
