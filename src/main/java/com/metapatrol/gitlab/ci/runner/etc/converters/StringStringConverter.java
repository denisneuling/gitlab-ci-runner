package com.metapatrol.gitlab.ci.runner.etc.converters;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class StringStringConverter extends StringConverter<String> {

    @Override
    public String convert(String value) {
        return value;
    }
}
