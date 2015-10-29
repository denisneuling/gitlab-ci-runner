package com.metapatrol.gitlab.ci.runner.client.util;

import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Method;
import com.metapatrol.gitlab.ci.runner.client.messages.common.HttpMethod;
import com.metapatrol.gitlab.ci.runner.client.messages.request.api.Request;
import com.metapatrol.gitlab.ci.runner.client.messages.response.api.Response;
import org.apache.log4j.Logger;


/**
 * <p>
 * HttpMethodUtil class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
public class HttpMethodUtil {
	private final static Logger log = Logger.getLogger(HttpMethodUtil.class);

	/**
	 * <p>
	 * retrieveMethod.
	 * </p>
	 *
	 */
	public static <T extends Response<?>> HttpMethod retrieveMethod(Request<T> request) {
		if (request == null) {
			return null;
		}

		Class<?> clazz = request.getClass();

		Method method = clazz.getAnnotation(Method.class);
		HttpMethod httpMethod = null;
		if(method == null){
			log.warn("Could not retrieve HttpMethod of type " + request.getClass().getName() + ", defaulting to HttpMethod.GET.");
			httpMethod = HttpMethod.GET;
		}else{
			httpMethod = method.value();
		}
		return httpMethod;
	}
}
