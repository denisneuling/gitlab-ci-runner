package com.metapatrol.gitlab.ci.runner.etc.format.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metapatrol.gitlab.ci.runner.etc.format.impl.FormatWriterImpl;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class JsonFormatWriter extends FormatWriterImpl {
    private Logger log = Logger.getLogger(getClass());

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public String[] canHandleFileNameSuffixes() {
        return JsonFormat.SUFFIXES;
    }

    @Override
    public <T> T write(File file, T confObject) throws IOException {
        String json = gson.toJson(confObject);

        FileWriter writer = new FileWriter(file);
        writer.write(json);
        writer.close();

        return confObject;
    }
}
