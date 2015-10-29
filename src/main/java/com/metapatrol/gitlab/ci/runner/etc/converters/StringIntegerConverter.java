package com.metapatrol.gitlab.ci.runner.etc.converters;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class StringIntegerConverter extends StringConverter<Integer> {

    @Override
    public Integer convert(String value) {
        return Integer.parseInt(value);
    }
}
