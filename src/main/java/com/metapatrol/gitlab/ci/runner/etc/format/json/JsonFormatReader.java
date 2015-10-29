package com.metapatrol.gitlab.ci.runner.etc.format.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metapatrol.gitlab.ci.runner.etc.format.impl.FormatReaderImpl;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class JsonFormatReader extends FormatReaderImpl {
    private Logger log = Logger.getLogger(getClass());

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String[] canHandleFileNameSuffixes() {
        return JsonFormat.SUFFIXES;
    }

    @Override
    public <T> T read(File file, T confObject) throws IOException {
        String contents = null;
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            contents = sb.toString();
        } finally {
            br.close();
        }

        try {
            BeanUtils.copyProperties(confObject, (T) gson.fromJson(contents, confObject.getClass()));
        } catch (ReflectiveOperationException e) {
            throw new IOException(e.getMessage(), e);
        }

        return confObject;
    }
}
