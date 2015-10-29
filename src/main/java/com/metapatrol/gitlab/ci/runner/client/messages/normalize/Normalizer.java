package com.metapatrol.gitlab.ci.runner.client.messages.normalize;

/**
 * <p>
 * Normalizer class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
public class Normalizer {

	/**
	 * <p>
	 * normalize.
	 * </p>
	 *
	 * @param jsonResponse
	 *            a {@link String} object.
	 * @return a {@link String} object.
	 */
	public String normalize(String jsonResponse) {
		return "{\"payload\":"+jsonResponse+"}";
	}
}
