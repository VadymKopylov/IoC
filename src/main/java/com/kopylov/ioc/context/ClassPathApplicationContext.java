package com.kopylov.ioc.context;

import com.kopylov.ioc.entity.Bean;
import com.kopylov.ioc.entity.BeanDefinition;
import com.kopylov.ioc.exception.BeanInstantiationException;
import com.kopylov.ioc.exception.NoSuchBeanException;
import com.kopylov.ioc.exception.NoUniqueBeanException;
import com.kopylov.ioc.reader.dom.XmlBeanDefinitionReader;
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

    public ClassPathApplicationContext() {
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        validateClass(clazz);
        if(!hasBeanWithClass(clazz)){
            throw new NoSuchBeanException(clazz.getName());
        }
        for (Bean bean : beans.values()) {
            if (clazz.isInstance(bean.getValue())) {
                return clazz.cast(bean.getValue());
            }
        }
        return null;
    }

    @Override
    public <T> T getBean(String id, Class<T> clazz) {
        validateId(id);
        validateClass(clazz);
        Bean bean = beans.get(id);
        if (bean != null) {
            if (clazz.isInstance(bean.getValue())) {
                return clazz.cast(bean.getValue());
            }
        }
        return null;
    }

    @Override
    public Object getBean(String id) {
        validateId(id);
        if(!beans.containsKey(id)){
            throw new NoSuchBeanException(id);
        }
        return beans.get(id).getValue();
    }

    public List<String> getBeanNames() {
        return new ArrayList<>(beans.keySet());
    }

    void setBeans(Map<String, Bean> beans) {
        this.beans = beans;
    }

    private void validateId(String id) {
        if (id == null || id.isEmpty()) {
            throw new BeanInstantiationException("Bean id must not be null or empty. Id: " + id);
        }
    }

    private void validateClass(Class<?> clazz) {
        if (clazz == null) {
            throw new BeanInstantiationException("Bean class must not be null.");
        }
        long classDuplicatesCount = beans.values().stream()
                .filter(bean -> clazz.isInstance(bean.getValue()))
                .count();
        if (classDuplicatesCount > 1) {
            throw new NoUniqueBeanException("No unique bean : " + clazz.getName());
        }
    }
    private boolean hasBeanWithClass(Class<?> beanClass) {
        for (Bean bean : beans.values()) {
            if (bean.getValue().getClass().equals(beanClass)) {
                return true;
            }
        }
        return false;
    }
}
