package com.metapatrol.gitlab.ci.runner.client.messages.marshal.exception;

public class MarshallingException extends Exception{
	private static final long serialVersionUID = 7358510101782000074L;

	public MarshallingException() {
		super();
	}

	public MarshallingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MarshallingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MarshallingException(String message) {
		super(message);
	}

	public MarshallingException(Throwable cause) {
		super(cause);
	}
}
