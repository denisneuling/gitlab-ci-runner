package com.metapatrol.gitlab.ci.runner.engine.service;

import com.metapatrol.gitlab.ci.runner.client.messages.payload.response.RegisterBuildResponsePayload;
import com.metapatrol.gitlab.ci.runner.engine.template.TemplateRenderer;
import org.apache.log4j.Logger;
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
    private Logger log = Logger.getLogger(getClass());

    private static final String DOCKERFILE = "config/Dockerfile.tpl";

    @Autowired
    private TemplateRenderer templateRenderer;

    public File renderDockerFile(File projectBuildShaDateDirectory, String image, List<String> adds, List<RegisterBuildResponsePayload.Variable> envs) throws IOException {

        Model model = model()
            .put("image", image)
            .put("adds", adds)
            .put("envs", envs);

        String rendered = null;
        try {
            rendered = templateRenderer.renderTemplateFromResourceLocation(DOCKERFILE, model);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        File dockerfile = new File(projectBuildShaDateDirectory.getAbsolutePath(), "Dockerfile");
        PrintWriter writer = null;

        dockerfile.createNewFile();
        writer = new PrintWriter(dockerfile, "UTF-8");


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
