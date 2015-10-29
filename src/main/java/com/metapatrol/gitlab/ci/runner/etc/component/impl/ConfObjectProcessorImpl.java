package com.metapatrol.gitlab.ci.runner.etc.component.impl;

import com.metapatrol.gitlab.ci.runner.etc.annotation.Default;
import com.metapatrol.gitlab.ci.runner.etc.annotation.Embedded;
import com.metapatrol.gitlab.ci.runner.etc.component.ConfObjectProcessor;
import com.metapatrol.gitlab.ci.runner.etc.converters.StringConverter;
import com.metapatrol.gitlab.ci.runner.etc.exception.PreprocessingException;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Denis Neuling (denisneuling@gmail.com)
 */
public class ConfObjectProcessorImpl implements ConfObjectProcessor {
    private Logger log = Logger.getLogger(getClass());

    private HashMap<Class<?>, List<StringConverter<?>>> converters = new HashMap<Class<?>, List<StringConverter<?>>>();

    private ConfObjectProcessorImpl(){}
    public ConfObjectProcessorImpl(List<StringConverter> converters){
        for(StringConverter converter : converters){
            register(converter);
        }
    }

    @Override
    public <T> T process(T object) {
        try {
            for(Field field : object.getClass().getDeclaredFields()){
                if(field.isAnnotationPresent(Embedded.class)){
                    field.setAccessible(true);
                    Object value = field.get(object);
                    if(value!=null){
                        field.set(object, process(field.get(object)));
                    }
                }else if(field.isAnnotationPresent(Default.class)){
                    field.setAccessible(true);
                    if(field.get(object)==null) {
                        Default defaultAnnotation = field.getAnnotation(Default.class);
                        String value = defaultAnnotation.value();

                        field.set(object, box(field.getType(), value));
                    }
                }
            }
        } catch (Throwable throwable) {
            throw new PreprocessingException(throwable);
        }

        return object;
    }

    private <T> T box(Class<T> clazz, String value){
        List<StringConverter<?>> converterList = converters.get(clazz);
        for(StringConverter<?> converter : converterList){
            try{
                return (T) converter.convert(value);
            }catch(Throwable throwable){
                if(log.isDebugEnabled()){
                    log.debug(String.format("Converting '%s' to type '%s' issues exception: %s", value.getClass().getName(), clazz.getName(), throwable.getMessage()), throwable);
                }
            }
        }

        return null;
    }

    private void register(StringConverter converter){
        Class<?> type = converter.getTargetClass();
        do{
            registerInternal(converter, type);
            type = type.getSuperclass();
        }while(!type.equals(Object.class));
    }

    private void registerInternal(StringConverter converter, Class<?> type){
        if(log.isDebugEnabled()) {
            log.debug(String.format("Registering %s for type %s", converter.getClass().getName(), type.getName()));
        }

        List<StringConverter<?>> converterList = converters.get(type);
        if(converterList == null){
            converterList = new LinkedList<StringConverter<?>>();
        }
        converterList.add(converter);
        converters.put(type, converterList);
    }
}
