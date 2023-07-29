package com.kopylov.ioc.exception;

public class BeanDefinitionReadException extends RuntimeException {

    public BeanDefinitionReadException(String message, Throwable e) {
        super(message,e);
    }
}
