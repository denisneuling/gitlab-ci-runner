package com.metapatrol.gitlab.ci.runner.engine.template.handlebars.service.impl;

import com.metapatrol.gitlab.ci.runner.engine.template.handlebars.service.HandlebarsTemplateHelperService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
@Service
public class HandlebarsTemplateHelperServiceImpl implements HandlebarsTemplateHelperService {
    @Override
    public Map<String, String> buildHandlebarsTemplateHelper() {
        return new HashMap<String, String>();
    }
}
