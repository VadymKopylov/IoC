package com.kopylov.ioc.exception;

public class NoSuchBeanException extends RuntimeException {

    public NoSuchBeanException(String beanKey) {
        super("Bean with key '" + beanKey + "' not found.");
    }
}

