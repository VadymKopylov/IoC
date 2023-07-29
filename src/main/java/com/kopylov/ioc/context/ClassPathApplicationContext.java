package com.kopylov.ioc.context;

import com.kopylov.ioc.entity.Bean;
import com.kopylov.ioc.entity.BeanDefinition;
import com.kopylov.ioc.reader.XmlBeanDefinitionReader;
import com.kopylov.ioc.util.BeanCreator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClassPathApplicationContext implements ApplicationContext {

    private Map<String, Bean> beans;

    public ClassPathApplicationContext(String... pathToXml) {
        List<BeanDefinition> beanDefinitions = new XmlBeanDefinitionReader(pathToXml).readBeanDefinition();
        this.beans = new BeanCreator(beanDefinitions).createBeans();
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T getBean(String name, Class<T> clazz) {
        return null;
    }

    @Override
    public Object getBean(String name) {
        return null;
    }

    @Override
    public List<String> getBeans() {
        return new ArrayList<>(beans.keySet());
    }
}
