package com.metapatrol.gitlab.ci.runner.client.util;

import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Default;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Path;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.PathVariable;
import com.metapatrol.gitlab.ci.runner.client.messages.request.api.Request;
import com.metapatrol.gitlab.ci.runner.client.messages.response.api.Response;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * PathUtil class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
public class PathUtil {
	private final static Logger log = Logger.getLogger(PathUtil.class);

	private static final String PATTERN = "\\$\\{(.*?)*\\}(.*?)";

	/**
	 * <p>
	 * resolveResourcePath.
	 * </p>
	 *
	 * @param request
	 *            object.
	 * @param <T>
	 *            a T object.
	 * @return a {@link String} object.
	 */
	public static <T extends Response<?>> String resolveResourcePath(Request<T> request) {
		Class<?> clazz = request.getClass();

		String unresolvedPath = ClassUtil.getClassAnnotationValue(clazz, Path.class, "value", String.class);

		Map<String, String> patternMap = new HashMap<String, String>();
		for (Field field : clazz.getDeclaredFields()) {
			PathVariable part = field.getAnnotation(PathVariable.class);
			if (part != null) {
				try {
					String pattern = part.value();
					field.setAccessible(true);
					Object valueObject = field.get(request);
					if (valueObject == null) {
						Default defaultValue = field.getAnnotation(Default.class);
						if (defaultValue != null) {
							valueObject = defaultValue.value();
						} else {
							log.warn("Could not resolve pathVariable " + pattern + " of request type " + clazz.getName() + ". This could end up in unexpected behavior.");
						}
					}
					patternMap.put(pattern, valueObject.toString());
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}

		Pattern pattern = Pattern.compile(PATTERN);
		Matcher matcher = pattern.matcher(unresolvedPath);

		LinkedHashSet<String> placeholder = new LinkedHashSet<String>();
		while (matcher.find()) {
			placeholder.add(matcher.group());
		}

		for (String key : placeholder) {
			String found = patternMap.get(key);
			if (found != null) {
				unresolvedPath = unresolvedPath.replace((String) key, found);
			}
		}

		if (unresolvedPath.endsWith("/")) {
			unresolvedPath = unresolvedPath.substring(0, unresolvedPath.length() - 1);
		}

		return unresolvedPath;
	}
}
