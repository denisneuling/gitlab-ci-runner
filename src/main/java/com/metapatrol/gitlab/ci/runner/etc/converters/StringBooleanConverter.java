package com.metapatrol.gitlab.ci.runner.etc.converters;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class StringBooleanConverter extends StringConverter<Boolean> {

    @Override
    public Boolean convert(String value) {
        return Boolean.parseBoolean(value);
    }
}
