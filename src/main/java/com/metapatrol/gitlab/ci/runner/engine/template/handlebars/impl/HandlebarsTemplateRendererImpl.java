package com.metapatrol.gitlab.ci.runner.engine.template.handlebars.impl;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.MarkdownHelper;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.context.FieldValueResolver;
import com.github.jknack.handlebars.context.JavaBeanValueResolver;
import com.github.jknack.handlebars.context.MapValueResolver;
import com.github.jknack.handlebars.context.MethodValueResolver;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import com.github.jknack.handlebars.io.CompositeTemplateLoader;
import com.github.jknack.handlebars.io.FileTemplateLoader;
import com.github.jknack.handlebars.io.ServletContextTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.metapatrol.gitlab.ci.runner.engine.template.handlebars.HandlebarsTemplateRenderer;
import com.metapatrol.gitlab.ci.runner.engine.template.handlebars.service.HandlebarsTemplateHelperService;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

@Service
public class HandlebarsTemplateRendererImpl implements HandlebarsTemplateRenderer, ApplicationContextAware {
    protected Logger log = Logger.getLogger(this.getClass());

    private static final String HANDLEBARS_REGISTER_HELPER = "Handlebars.registerHelper('%s', %s)";

    private ApplicationContext applicationContext;

    @Autowired
    private HandlebarsTemplateHelperService handlebarsTemplateHelperService;

    @Override
    public String renderTemplateFromResourceLocation(String resourceLocation, Map<String, Object> model) throws IOException {
        Assert.notNull(resourceLocation, "template must not be null!");
        Assert.notNull(model, "model must not be null!");

        Handlebars handlebars = newHandlebars();

        Template handlebarsTemplate = handlebars.compile(resourceLocation);
        Context context = Context
            .newBuilder(model)
            .resolver(
                    MapValueResolver.INSTANCE
                    , FieldValueResolver.INSTANCE
                    , JavaBeanValueResolver.INSTANCE
                    , MethodValueResolver.INSTANCE
            )
        .build();

        return handlebarsTemplate.apply(context);
    }

    public String renderTemplate(String template, Map<String, Object> model) throws IOException {
        Assert.notNull(template, "template must not be null!");
        Assert.notNull(model, "model must not be null!");

        Handlebars handlebars = newHandlebars();

        Template handlebarsTemplate = handlebars.compileInline(template);
        Context context = Context
            .newBuilder(model)
            .resolver(
                MapValueResolver.INSTANCE
            ,   FieldValueResolver.INSTANCE
            ,   JavaBeanValueResolver.INSTANCE
            ,   MethodValueResolver.INSTANCE
            )
        .build();

        return handlebarsTemplate.apply(context);
    }

    private Handlebars newHandlebars(){
        String suffix = "";
        String templatePath = "";

        LinkedList<TemplateLoader> templateLoaders = new LinkedList<TemplateLoader>();

        TemplateLoader templateLoader = null;

        try {
            if (log.isDebugEnabled()){
                log.debug("Adding " + FileTemplateLoader.class.getName() + " (" + (new File(templatePath.isEmpty() ? "/" : templatePath) + ", " + (suffix != null ? suffix : "") + ")"));
            }
            templateLoaders.add(new FileTemplateLoader(new File(templatePath.isEmpty() ? "/" : templatePath), suffix != null ? suffix : ""));
        } catch (Throwable throwable) {
            if (log.isDebugEnabled()) {
                log.debug("Skipping FileTemplateLoader due to error: " + throwable.getMessage());
            }
        }

        if (log.isDebugEnabled()) {
            log.debug("Adding " + ClassPathTemplateLoader.class.getName() + " (" + templatePath + ", " + (suffix != null ? suffix : "") + ")");
        }
        templateLoaders.add(new ClassPathTemplateLoader(templatePath, suffix != null ? suffix : ""));

        Object servletContext = null;
        if (isServletContextActivated()) {
            servletContext = getServletContextFromApplicationContext();
            if (servletContext != null) {
                ServletContextTemplateLoader servletContextTemplateLoader = newServletContextTemplateLoader(servletContext, templatePath, suffix != null ? suffix : "");
                if (servletContextTemplateLoader != null) {
                    if (log.isDebugEnabled()) {
                        log.debug("Adding " + ServletContextTemplateLoader.class.getName() + " (" + (servletContext + ", " + templatePath + ", " + (suffix != null ? suffix : "") + ")"));
                    }
                    templateLoaders.add(servletContextTemplateLoader);
                }
            }
        }

        if (templateLoaders.isEmpty()) {
            throw new RuntimeException("Could not set up TemplateLoader for prefix:'" + templatePath + "' suffix:'" + suffix + "' (neither ServletContextTemplateLoader, nor ClassPathTemplateLoader, nor FileTemplateLoader)");
        }

        if (templateLoaders.size() == 1) {
            templateLoader = templateLoaders.getFirst();
        } else {
            templateLoader = new CompositeTemplateLoader(templateLoaders.toArray(new TemplateLoader[templateLoaders.size()]));
        }

        Handlebars handlebars = new Handlebars(
            templateLoader
        )
        .registerHelper(
            "md"
        ,   new MarkdownHelper()
        );

        for(Map.Entry<String, String> entry: handlebarsTemplateHelperService.buildHandlebarsTemplateHelper().entrySet()){
            try{
                handlebars.registerHelpers(
                    entry.getKey()
                ,   new ByteArrayInputStream(
                        String.format(
                            HANDLEBARS_REGISTER_HELPER
                        ,   entry.getKey()
                        ,   entry.getValue()
                        ).getBytes(
                            StandardCharsets.UTF_8
                        )
                    )
                );
            }catch(Throwable throwable){
                log.warn(String.format("Could not add handlebars helper function '%s': %s", entry.getKey(), throwable.getMessage()));
            }
        }

        if (log.isDebugEnabled()){
            log.debug("Handlebars set up done - using TemplateLoaders: " + templateLoaders.toString());
        }

        return handlebars;
    }

    private Object getServletContextFromApplicationContext(){
        try {
            Map<String, ?> servletContextBeans = applicationContext.getBeansOfType(loadServletContextClass());

            Collection<?> servletContextBeanValues = servletContextBeans.values();
            if (servletContextBeanValues.isEmpty()) {
                if(log.isDebugEnabled()) {
                    log.debug("Resolving servletContext could not find candidate. Skipping");
                }
                return null;
            } else if (servletContextBeanValues.size() == 1) {
                return servletContextBeanValues.iterator().next();
            } else if (servletContextBeanValues.size() > 1) {
                if(log.isDebugEnabled()) {
                    log.debug("Resolving servletContext issued " + servletContextBeanValues.size() + " candidates " + servletContextBeans.keySet().toString() + " - using first");
                }
                return servletContextBeanValues.iterator().next();
            }
        }catch (Throwable throwable){
            log.warn(throwable.getMessage(), throwable);
        }
        return null;
    }

    private ServletContextTemplateLoader newServletContextTemplateLoader(Object servletContext, String templatePath, String suffix){
        if(servletContext!=null) {
            Class<?> servletLoaderClass = loadServletContextClass();
            if (servletLoaderClass != null) {
                try {
                    Constructor<ServletContextTemplateLoader> constructor = ServletContextTemplateLoader.class.getConstructor(servletLoaderClass, String.class, String.class);
                    return constructor.newInstance(servletContext, templatePath, suffix != null ? suffix : "");
                } catch (Throwable throwable) {
                    return null;
                }
            }
        }
        return null;
    }

    private boolean isServletContextActivated(){
        try{
            Class.forName("javax.servlet.ServletContext");
            return true;
        }catch(Throwable throwable){
            return false;
        }
    }

    private Class<?> loadServletContextClass(){
        try {
            return Class.forName("javax.servlet.ServletContext");
        }catch(Throwable throwable){
            return null;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
