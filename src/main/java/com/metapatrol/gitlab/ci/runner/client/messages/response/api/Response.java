package com.metapatrol.gitlab.ci.runner.client.messages.response.api;

import com.metapatrol.gitlab.ci.runner.client.messages.annotation.Body;
import com.metapatrol.gitlab.ci.runner.client.messages.annotation.StatusCode;
import com.metapatrol.gitlab.ci.runner.client.messages.payload.api.Payload;

import java.io.Serializable;

/**
 * HTTP Response DTO (DTO DataTransferObject)
 *
 * @author Denis Neuling (denisneuling@gmail.com)
 *
 * @param <T>
 */
public abstract class Response<T extends Payload> implements Serializable {
	private static final long serialVersionUID = -1067547688524545568L;

	@StatusCode
	protected Integer statusCode;
	@Body
	protected T payload;

	protected String result;
	protected String statusMessage;
	protected long responseTime;

	public T getPayload() {
		return payload;
	}

	public void setPayload(T entity) {
		this.payload = entity;
	}


	public final String getResult() {
		return result;
	}

	public final void setResult(String result) {
		this.result = result;
	}

	public final String getStatusMessage() {
		return statusMessage;
	}

	public final void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	public final int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public final long getResponseTime() {
		return responseTime;
	}

	public final void setResponseTime(long responseTime) {
		this.responseTime = responseTime;
	}

	@Override
	public String toString() {
		return "Response [result=" + result + ", statusMessage=" + statusMessage + ", statusCode=" + statusCode + ", responseTime=" + responseTime + "]";
	}
}
