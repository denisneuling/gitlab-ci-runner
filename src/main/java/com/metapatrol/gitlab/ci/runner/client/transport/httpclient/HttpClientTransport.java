package com.metapatrol.gitlab.ci.runner.client.transport.httpclient;

import com.metapatrol.gitlab.ci.runner.client.messages.request.api.Request;
import com.metapatrol.gitlab.ci.runner.client.transport.Transport;
import com.metapatrol.gitlab.ci.runner.client.transport.TransportResult;
import com.metapatrol.gitlab.ci.runner.client.transport.exception.TransportException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import java.net.URI;
import java.util.Map;

/**
 * <p>
 * HttpClientTransport class.
 * </p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
public class HttpClientTransport extends Transport {
	protected Logger log = Logger.getLogger(getClass());

	/**
	 * <p>
	 * Constructor for HttpClientTransport.
	 * </p>
	 */
	public HttpClientTransport() {
	}

	private DefaultHttpClient client = new DefaultHttpClient();

	/** {@inheritDoc} */
	@Override
	public TransportResult doGet(URI uri, Map<String, String> headers, Request<?> request) {
		headers.put("Content-Type", contentType);
		headers.put("Accept", contentType);

		HttpGet httpRequest = new HttpGet(uri);
		fillInHeaders(httpRequest, headers);

		if (log.isDebugEnabled()) {
			log.debug("GET " + httpRequest.getURI());
		}
		try {
			HttpResponse response = client.execute(httpRequest);
			return new HttpClientTransportResult(response);
		} catch (Throwable t) {
			throw new TransportException(t);
		}
	}

	/** {@inheritDoc} */
	@Override
	public TransportResult doPost(URI uri, Map<String, String> headers, String payload, Request<?> request) {
		headers.put("Content-Type", contentType);
		headers.put("Accept", contentType);


		HttpPost httpRequest = new HttpPost(uri);
		fillInHeaders(httpRequest, headers);

		if (log.isDebugEnabled()) {
			log.debug("POST " + httpRequest.getURI() + " application/json: " + payload);
		}
		try {
			httpRequest.setEntity(new StringEntity(payload));

			HttpResponse response = client.execute(httpRequest);
			return new HttpClientTransportResult(response);
		} catch (Throwable t) {
			throw new TransportException(t);
		}
	}

	/** {@inheritDoc} */
	@Override
	public TransportResult doPut(URI uri, Map<String, String> headers, String payload, Request<?> request) {
		headers.put("Content-Type", contentType);
		headers.put("Accept", contentType);

		HttpPut httpRequest = new HttpPut(uri);

		fillInHeaders(httpRequest, headers);

		if (log.isDebugEnabled()) {
			log.debug("PUT " + httpRequest.getURI() + " application/json: " + payload);
		}
		try {
			httpRequest.setEntity(new StringEntity(payload));

			HttpResponse response = client.execute(httpRequest);
			return new HttpClientTransportResult(response);
		} catch (Throwable t) {
			throw new TransportException(t);
		}
	}

	/** {@inheritDoc} */
	@Override
	public TransportResult doDelete(URI uri, Map<String, String> headers, Request<?> request) {
		headers.put("Content-Type", contentType);
		headers.put("Accept", contentType);

		HttpDelete httpRequest = new HttpDelete(uri);

		fillInHeaders(httpRequest, headers);

		if (log.isDebugEnabled()) {
			log.debug("DELETE " + httpRequest.getURI());
		}
		try {
			HttpResponse response = client.execute(httpRequest);
			return new HttpClientTransportResult(response);
		} catch (Throwable t) {
			throw new TransportException(t);
		}
	}

	private void fillInHeaders(HttpMessage httpMessage, Map<String, String> headers){
		if(headers!=null){
			for(String key : headers.keySet()){
				httpMessage.setHeader(key, headers.get(key));
			}
		}
	}
}
