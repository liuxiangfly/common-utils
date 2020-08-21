package com.lxm.netty.client.exception;

public class ClientException extends Exception {

    public ClientException(String errorMsg) {
        super(errorMsg);
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }
}
