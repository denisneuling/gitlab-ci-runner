package com.metapatrol.gitlab.ci.runner.client.util;

import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Normalized;
import com.metapatrol.gitlab.ci.runner.client.messages.normalize.Normalizer;

import org.apache.log4j.Logger;
/**
 * <p>
 * NormalizationUtil class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
public class NormalizationUtil {
	private final static Logger log = Logger.getLogger(NormalizationUtil.class);

	/**
	 * <p>
	 * getNormalizer.
	 * </p>
	 *
	 * @param response
	 * @param <T>
	 *            a T object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> Normalizer getNormalizer(T response) {
		Normalizer normalizer = new Normalizer();
		Class<Normalizer> normalizerClazz = ClassUtil.getClassAnnotationValue(response.getClass(), Normalized.class, "value", Class.class);
		if (normalizerClazz != null) {
			try {
				normalizer = normalizerClazz.newInstance();
			} catch (InstantiationException e) {
				log.warn(e.getMessage(), e);
			} catch (IllegalAccessException e) {
				log.warn(e.getMessage(), e);
			}
		}
		return normalizer;
	}
}
