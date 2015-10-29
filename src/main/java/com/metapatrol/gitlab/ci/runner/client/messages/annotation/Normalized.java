package com.metapatrol.gitlab.ci.runner.client.messages.annotation;

import com.metapatrol.gitlab.ci.runner.client.messages.normalize.Normalizer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>
 * Normalized class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Normalized {

	Class<? extends Normalizer> value() default Normalizer.class;
}
