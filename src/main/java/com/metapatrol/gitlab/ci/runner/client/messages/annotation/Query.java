package com.metapatrol.gitlab.ci.runner.client.messages.annotation;

import com.metapatrol.gitlab.ci.runner.client.messages.marshal.DefaultMarshaller;
import com.metapatrol.gitlab.ci.runner.client.messages.marshal.Marshaller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Query class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Query {

	String value() default "";

	Class<? extends Marshaller<?,?>> marshaller() default DefaultMarshaller.class;
}
