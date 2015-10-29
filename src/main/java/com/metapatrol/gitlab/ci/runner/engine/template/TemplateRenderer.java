package com.metapatrol.gitlab.ci.runner.engine.template;

import java.io.IOException;
import java.util.Map;

public interface TemplateRenderer {

    public String renderTemplateFromResourceLocation(
            String resourceLocation
            , Map<String, Object> model
    ) throws IOException;


    public String renderTemplate(
            String template
            , Map<String, Object> model
    ) throws IOException;
}
