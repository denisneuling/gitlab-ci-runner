package com.metapatrol.gitlab.ci.runner.client.transport.exception;

import com.metapatrol.gitlab.ci.runner.client.exception.ClientException;

public class TransportException extends ClientException {
	private static final long serialVersionUID = -1437773368782858652L;

	public TransportException() {
		super();
	}

	public TransportException(Throwable th) {
		super(th);
	}

	public TransportException(String message) {
		super(message);
	}

	public TransportException(String message, Throwable throwable){
		super(message, throwable);
	}
}
