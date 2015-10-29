package com.metapatrol.gitlab.ci.runner.engine.service;

import com.metapatrol.gitlab.ci.runner.engine.template.TemplateRenderer;
import com.metapatrol.gitlab.ci.runner.fs.FileSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class DockerfileService {

    private static final String DOCKERFILE = "config/Dockerfile.tpl";

    @Autowired
    private TemplateRenderer templateRenderer;

    @Autowired
    private FileSystem fileSystem;

    public File renderDockerFile(File projectBuildDirectory, String image, List<String> adds) {

        Model model = model()
            .put("image", image)
            .put("adds", adds);

        String rendered = null;
        try {
            rendered = templateRenderer.renderTemplateFromResourceLocation("/config/Dockerfile.tpl", model);
        } catch (IOException e) {
            e.printStackTrace();
        }

        File dockerfile = new File(projectBuildDirectory.getAbsolutePath(), "Dockerfile");
        PrintWriter writer = null;
        try {
            dockerfile.createNewFile();
            writer = new PrintWriter(dockerfile, "UTF-8");
        }catch(Throwable throwable){}

        writer.append(rendered);
        writer.flush();
        writer.close();

        return dockerfile;
    }

    private static class Model extends HashMap<String, Object>{
        public Model put(String key, Object value){
            super.put(key, value);
            return this;
        }
    }

    private Model model(){
        return new Model();
    }
}
