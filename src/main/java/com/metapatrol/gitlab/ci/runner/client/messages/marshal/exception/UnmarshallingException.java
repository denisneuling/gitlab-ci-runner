package com.metapatrol.gitlab.ci.runner.client.messages.marshal.exception;

public class UnmarshallingException extends Exception{
	private static final long serialVersionUID = 7358510101782000074L;

	public UnmarshallingException() {
		super();
	}

	public UnmarshallingException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public UnmarshallingException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnmarshallingException(String message) {
		super(message);
	}

	public UnmarshallingException(Throwable cause) {
		super(cause);
	}
}
