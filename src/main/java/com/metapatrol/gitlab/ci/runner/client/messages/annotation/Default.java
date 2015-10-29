package com.metapatrol.gitlab.ci.runner.client.messages.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Default class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Default {

	String value();

	public static final String NOTSET = "__NOT_SET__";
	public static final String EMPTY = "";
}
