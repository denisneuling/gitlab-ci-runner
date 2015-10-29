package com.metapatrol.gitlab.ci.runner.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.metapatrol.gitlab.ci.runner.client.exception.ValidationConstraintViolationException;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Body;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Default;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Header;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Query;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Required;
import com.metapatrol.gitlab.ci.runner.client.messages.request.api.Request;
import com.metapatrol.gitlab.ci.runner.client.messages.response.api.Response;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * RequestUtil class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
public class RequestUtil {
	private final static Logger log = Logger.getLogger(RequestUtil.class);

	private static final String EMPTY = "{}";
	private static final String preMessage = "Request breaks constraints.";

	/**
	 * <p>
	 * getInstanceOfParameterizedType.
	 * </p>
	 *
	 * @param request
	 * @param <T>
	 *            a T object.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends Response<?>> T getInstanceOfParameterizedType(Request<T> request) {
		Type superclazz = request.getClass().getGenericSuperclass();
		try {
			Type parameterizedTypeClazz = ((ParameterizedType) superclazz).getActualTypeArguments()[0];
			Class<T> clazz = (Class<T>) parameterizedTypeClazz;
			if(clazz.equals(Response.class)){
				/*
				 * make possible to return abstract responses
				 */
				return (T) new Response(){
					private static final long serialVersionUID = -3888647009300563413L;
				};
			}
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static Gson g = new GsonBuilder().create();
	/**
	 * <p>
	 * getRequestPayload.
	 * </p>
	 *
	 * @param request
	 *            a {@link Request} object.
	 * @param <T>
	 *            a T object.
	 * @return a {@link String} object.
	 */
	@SuppressWarnings("unused")
	public static <T extends Response<?>> String getRequestPayload(Request<T> request) {
		Class<?> referenceClazz = request.getClass();
		List<Field> fields = ClassUtil.getAnnotatedFields(referenceClazz, Body.class);
		List<String> objects = new LinkedList<String>();
		for (Field field : fields) {
			Object payload = ClassUtil.getValueOf(field, request, referenceClazz, Object.class);
			if(payload instanceof String){
				return (String) payload;
			}else{
				return g.toJson(payload);
			}
		}
		return EMPTY;
	}

	/**
	 * <p>
	 * resolveQueryPart.
	 * </p>
	 *
	 * @param request
	 *            a {@link Request} object.
	 * @param <T>
	 *            a T object.
	 * @return a {@link HashMap} object.
	 */
	public static <T extends Response<?>> HashMap<String, String> resolveQueryPart(Request<T> request) {
		HashMap<String, String> queryParts = new HashMap<String, String>();
		Class<?> referenceClazz = request.getClass();
		List<Field> fields = ClassUtil.getAnnotatedFields(referenceClazz, Query.class);
		for (Field field : fields) {
			if(Map.class.isAssignableFrom(field.getType())){
				Map<?, ?> currentQueries = ClassUtil.getValueOf(field, request, referenceClazz, Map.class);
				if(currentQueries==null){
					log.warn("Could not retrieve value of field " + referenceClazz.getName() + "#" + field.getName());
					continue;
				}

				for(Object currentQueriesKey : currentQueries.keySet()){
					queryParts.put(currentQueriesKey.toString(), currentQueries.get(currentQueriesKey).toString());
				}
				continue;
			}

			Query query = field.getAnnotation(Query.class);
			String key = query.value();

			if (key == null || (key != null && key.isEmpty())) {
				key = field.getName();
			}

			Object value = ClassUtil.getValueOf(field, request, referenceClazz, Object.class);
			String valueToSet = null;
			if (value == null) {
				Default defaultValue = field.getAnnotation(Default.class);
				if (defaultValue != null) {
					valueToSet = defaultValue.value();
				}
			} else {
				// TODO marshalling
				valueToSet = value.toString();
			}
			queryParts.put(key, valueToSet);
		}

		return queryParts;
	}

	/**
	 * <p>
	 * resolveQueryPart.
	 * </p>
	 *
	 * @param request
	 *            a {@link Request} object.
	 * @param <T>
	 *            a T object.
	 * @return a {@link HashMap} object.
	 */
	public static <T extends Response<?>> HashMap<String, String> resolveHeaders(Request<T> request) {
		HashMap<String, String> headers = new HashMap<String, String>();
		Class<?> referenceClazz = request.getClass();
		List<Field> fields = ClassUtil.getAnnotatedFields(referenceClazz, Header.class);
		for (Field field : fields) {
			if(Map.class.isAssignableFrom(field.getType())){
				Map<?, ?> currentHeaders = ClassUtil.getValueOf(field, request, referenceClazz, Map.class);
				if(currentHeaders==null){
					log.warn("Could not retrieve value of field " + referenceClazz.getName() + "#" + field.getName());
					continue;
				}

				for(Object currentHeadersKey : currentHeaders.keySet()){
					headers.put(currentHeadersKey.toString(), currentHeaders.get(currentHeadersKey).toString());
				}
				continue;
			}

			Header header = field.getAnnotation(Header.class);
			String key = header.value();

			if (key == null || (key != null && key.isEmpty())) {
				key = field.getName();
			}

			Object value = ClassUtil.getValueOf(field, request, referenceClazz, Object.class);
			String valueToSet = null;
			if (value == null) {
				Default defaultValue = field.getAnnotation(Default.class);
				if (defaultValue != null) {
					valueToSet = defaultValue.value();
				}
			} else {
				valueToSet = value.toString();
			}
			headers.put(key, valueToSet);
		}

		return headers;
	}

	/**
	 * <p>
	 * validate.
	 * </p>
	 *
	 * @param request
	 *            a {@link Request} object.
	 * @param <T>
	 *            a T object.
	 */
	public static <T extends Response<?>> void validate(Request<T> request) throws ValidationConstraintViolationException {
		if (request != null) {
			Class<?> clazz = request.getClass();
			List<ValidationConstraintViolationException.ConstraintViolation> leafs = new LinkedList<ValidationConstraintViolationException.ConstraintViolation>();

			Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if (null != field.getAnnotation(Required.class) ) {

					Default defaultValue = null;
					if((defaultValue = field.getAnnotation(Default.class)) != null){
						String defaultValuesValue = defaultValue.value();
						if(defaultValuesValue != null && !defaultValuesValue.isEmpty()){
							continue;
						}
					}

					field.setAccessible(true);
					Object value = null;
					try {
						value = field.get(request);
					} catch (Exception e) {
						// not cool...
						throw new ValidationConstraintViolationException(e);
					}

					if (value == null || value instanceof String && ((String) value).isEmpty()) {
						ValidationConstraintViolationException.ConstraintViolation violation = ValidationConstraintViolationException.ConstraintViolation.newConstraintViolation("@" + Required.class.getSimpleName(), field);
						leafs.add(violation);
					}
				}
			}

			if (!leafs.isEmpty()) {
				throw new ValidationConstraintViolationException(preMessage, leafs);
			}
		}
	}
}
