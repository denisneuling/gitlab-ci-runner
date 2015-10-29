package com.metapatrol.gitlab.ci.runner.client.transport.httpclient;

import com.metapatrol.gitlab.ci.runner.client.transport.TransportResult;
import com.metapatrol.gitlab.ci.runner.client.transport.exception.TransportException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.log4j.Logger;

import java.io.IOException;

/**
 * <p>HttpClientTransportResult class.</p>
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 */
public class HttpClientTransportResult implements TransportResult {
	protected Logger log = Logger.getLogger(getClass());

	private HttpResponse response;

	private int statusCode;
	private String result;

	/**
	 * <p>Constructor for HttpClientTransportResult.</p>
	 *
	 * @param response a {@link org.apache.http.HttpResponse} object.
	 */
	public HttpClientTransportResult(HttpResponse response){
		this.response = response;

		readIn();
	}

	private void readIn(){
		String content = null;
		this.statusCode = response.getStatusLine().getStatusCode();
		try {
			if(this.statusCode!=204){
				content = IOUtils.toString(response.getEntity().getContent());
				this.result = content;
			}
		} catch (IOException e) {
			throw new TransportException(e);
		} finally {
			try {
				if(this.statusCode!=204){
					response.getEntity().getContent().close();
				}
			} catch (IllegalStateException e) {
				// fuck it
			} catch (IOException e) {
				// fuck it
			}
		}
		if (log.isDebugEnabled()) {
			log.debug(this.toString());
		}
	}

	/**
	 * <p>Getter for the field <code>statusCode</code>.</p>
	 *
	 * @return a int.
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/** {@inheritDoc} */
	public String getResult() {
		return result;
	}

	@Override
	public String toString() {
		return statusCode + (result!=null? " " + (result.length()<=300?result:result.substring(0, 300)+"..."):"");
	}
}
