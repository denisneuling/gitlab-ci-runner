package com.metapatrol.gitlab.ci.runner.client.exception;

/**
 * Created by ska on 03.01.15.
 */
public abstract class ClientException  extends RuntimeException{
    private static final long serialVersionUID = 1811670033746317057L;

    public ClientException() {
        super();
    }

    public ClientException(Throwable th) {
        super(th);
    }

    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable throwable){
        super(message, throwable);
    }
}
