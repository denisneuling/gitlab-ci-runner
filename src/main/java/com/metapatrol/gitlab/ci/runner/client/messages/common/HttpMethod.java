package com.metapatrol.gitlab.ci.runner.client.messages.common;

/**
 * <p>
 * HttpMethod class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
public enum HttpMethod {

	/**
	 * Http GET method descriptor.
	 *
	 * @see <a
	 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#Get">rfc2616
	 *      GET</a>
	 */
	GET("GET"),

	/**
	 * HTTP POST method descriptor.
	 *
	 * @see <a
	 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#Post">rfc2616
	 *      POST</a>
	 */
	POST("POST"),

	/**
	 * HTTP PUT method descriptor.
	 *
	 * @see <a
	 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#Put">rfc2616
	 *      PUT</a>
	 */
	PUT("PUT"),

	/**
	 * HTTP DELETE method descriptor.
	 *
	 * @see <a
	 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#Delete">rfc2616
	 *      DELETE</a>
	 */
	DELETE("DELETE"),

	/**
	 * HTTP HEAD method descriptor.
	 *
	 * @see <a
	 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#Head">rfc2616
	 *      HEAD</a>
	 */
	HEAD("HEAD"),

	/**
	 * HTTP TRACE method descriptor.
	 *
	 * @see <a
	 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#Trace">rfc2616
	 *      TRACE</a>
	 */
	TRACE("TRACE"),

	/**
	 * HTTP CONNECT method descriptor.
	 *
	 * @see <a
	 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#Connect">rfc2616
	 *      CONNECT</a>
	 */
	CONNECT("CONNECT"),

	/**
	 * HTTP OPTIONS method descriptor.
	 *
	 * @see <a
	 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec9.html#Options">rfc2616
	 *      OPTIONS</a>
	 */
	OPTIONS("OPTIONS");

	private String code;
	private HttpMethod(String code){
		this.code = code;
	}

	@Override
	public String toString(){
		return code;
	}

}
