package com.kopylov.ioc.context;

import java.util.List;

public interface ApplicationContext {

    <T> T getBean(Class<T> clazz);

    <T> T getBean(String name, Class<T> clazz);

    Object getBean(String name);

    List<String> getBeanNames();
}
