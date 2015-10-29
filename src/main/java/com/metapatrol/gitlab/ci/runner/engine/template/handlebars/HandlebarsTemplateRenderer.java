package com.metapatrol.gitlab.ci.runner.engine.template.handlebars;

import com.metapatrol.gitlab.ci.runner.engine.template.TemplateRenderer;

import java.io.IOException;
import java.util.Map;

public interface HandlebarsTemplateRenderer extends TemplateRenderer {

    public String renderTemplateFromResourceLocation(String resourceLocation, Map<String, Object> model) throws IOException;

    public String renderTemplate(String template, Map<String, Object> model) throws IOException;

}
