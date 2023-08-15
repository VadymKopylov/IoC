package com.kopylov.ioc.exception;

public class BeanInstantiationException extends RuntimeException {

    public BeanInstantiationException(String message) {
        super(message);
    }

    public BeanInstantiationException(String message, Throwable e) {
        super(message, e);
    }
}
