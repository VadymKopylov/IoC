package com.kopylov.ioc.reader;

import com.kopylov.ioc.entity.BeanDefinition;

import java.util.List;

public interface BeanDefinitionReader {

    List<BeanDefinition> readBeanDefinition();
}
