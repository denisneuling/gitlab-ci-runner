package com.metapatrol.gitlab.ci.runner.etc.format.ini;

import com.metapatrol.gitlab.ci.runner.etc.annotation.Embedded;
import com.metapatrol.gitlab.ci.runner.etc.format.impl.FormatReaderImpl;
import org.apache.log4j.Logger;
import org.ini4j.Config;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class IniFormatReader extends FormatReaderImpl {
    private Logger log = Logger.getLogger(getClass());

    @Override
    public String[] canHandleFileNameSuffixes() {
        return IniFormat.SUFFIXES;
    }

    @Override
    public <T> T read(File file, T confObjectClass) throws IOException {
        Config cfg = new Config();

        cfg.setMultiOption(true);
        Ini ini = new Ini();

        ini.setConfig(cfg);
        ini.load(file);

        for(Field field : confObjectClass.getClass().getDeclaredFields()){
            if(field.isAnnotationPresent(Embedded.class)){
                field.setAccessible(true);
                Object to = null;
                try {
                    to = field.get(confObjectClass);
                    if(to!=null) {
                        ini.get(field.getName()).to(to);
                        field.set(confObjectClass, to);
                    }
                } catch (IllegalAccessException e) {
                    if(log.isDebugEnabled()){
                        log.debug(String.format("Could not set '%s': ", field.getName(), e.getMessage()));
                    }
                }
            }
        }

        return confObjectClass;
    }
}
