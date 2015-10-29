package com.metapatrol.gitlab.ci.runner.etc.converters;

import java.lang.reflect.ParameterizedType;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public abstract class StringConverter<TO> {

    private Class<TO> targetClass;

    public StringConverter(){
        this.targetClass = (Class<TO>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    public Class<TO> getTargetClass(){
        return targetClass;
    }

    public abstract TO convert(String value);
}
