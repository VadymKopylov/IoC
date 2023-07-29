package com.kopylov.ioc.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class BeanDefinition {

    private final String id;
    private final String clazz;
    private Map<String, String> property;
    private Map<String, String> refProperty;

    public BeanDefinition(String id, String clazz) {
        this.id = id;
        this.clazz = clazz;
        this.property = new HashMap<>();
        this.refProperty = new HashMap<>();
    }
}
