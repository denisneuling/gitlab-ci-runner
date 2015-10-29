package com.metapatrol.gitlab.ci.runner.client.transport;

import com.metapatrol.gitlab.ci.runner.client.messages.request.api.Request;
import com.metapatrol.gitlab.ci.runner.client.transport.exception.TransportException;

import java.net.URI;
import java.util.Map;

public abstract class Transport {

	/** Constant <code>contentType="application/json"</code> */
	protected static final String contentType = "application/json";

	/**
	 * <p>Constructor for Transport.</p>
	 */
	public Transport() {
	}

	/**
	 * <p>doGet.</p>
	 */
	public abstract TransportResult doGet(URI uri, Map<String, String> headers, Request<?> request) throws TransportException;

	/**
	 * <p>doPost.</p>
	 */
	public abstract TransportResult doPost(URI uri, Map<String, String> headers, String payload, Request<?> request) throws TransportException;

	/**
	 * <p>doPut.</p>
	 */
	public abstract TransportResult doPut(URI uri, Map<String, String> headers, String payload, Request<?> request) throws TransportException;

	/**
	 * <p>doDelete.</p>
	 */
	public abstract TransportResult doDelete(URI uri, Map<String, String> headers, Request<?> request) throws TransportException;


}
