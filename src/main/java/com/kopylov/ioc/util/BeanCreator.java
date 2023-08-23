package com.kopylov.ioc.util;

import com.kopylov.ioc.entity.Bean;
import com.kopylov.ioc.entity.BeanDefinition;
import com.kopylov.ioc.exception.BeanInstantiationException;
import com.kopylov.ioc.exception.NoSuchBeanException;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class BeanCreator {

    private final List<BeanDefinition> beanDefinitions;

    public BeanCreator(List<BeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }

    public Map<String, Bean> createBeans() {
        Map<String, Bean> beans = fillIdAndClass(beanDefinitions);
        beans = fillProperties(beans, beanDefinitions);
        beans = fillRefProperties(beans, beanDefinitions);
        return beans;
    }

    Map<String, Bean> fillIdAndClass(List<BeanDefinition> beanDefinitions) {
        Map<String, Bean> beans = new HashMap<>();
        for (BeanDefinition beanDefinition : beanDefinitions) {
            if (beanDefinition != null) {
                String id = beanDefinition.getId();
                try {
                    Object object = Class.forName(beanDefinition.getClazz()).getConstructor().newInstance();
                    Bean bean = new Bean(id, object);
                    beans.put(id, bean);
                } catch (Exception e) {
                    log.error("Failed to create Bean from BeanDefinition: {}" + id, e);
                    throw new BeanInstantiationException("Error with create Bean from BeanDefinition", e);
                }
            }
        }
        return beans;
    }

    Map<String, Bean> fillProperties(Map<String, Bean> beans, List<BeanDefinition> beanDefinitions) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            String id = beanDefinition.getId();
            Bean bean = beans.get(id);
            if (bean == null) {
                log.error("Can't find Bean with id: {}" + id);
                throw new IllegalArgumentException("Bean with id '" + id + "' not found.");
            }
            Field[] fields = bean.getValue().getClass().getDeclaredFields();
            Set<Map.Entry<String, String>> properties = beanDefinition.getProperty().entrySet();
            propertyReader(bean, fields, properties);
        }
        return beans;
    }

    Map<String, Bean> fillRefProperties(Map<String, Bean> beans, List<BeanDefinition> beanDefinitions) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            String beanDefinitionId = beanDefinition.getId();
            if (beans.containsKey(beanDefinitionId)) {
                if (!beanDefinition.getRefProperty().isEmpty()) {
                    for (Map.Entry<String, String> refProperty : beanDefinition.getRefProperty().entrySet()) {
                        String beanKey = refProperty.getKey();
                        if (beans.containsKey(beanKey)) {
                            Bean beanWithRefProperty = beans.get(beanDefinitionId);
                            Bean refBean = beans.get(beanKey);
                            setBeanRefProperty(beanKey, beanWithRefProperty, refBean);
                        } else {
                            log.error("No such Bean with key: {} in map" + beanKey);
                            throw new NoSuchBeanException(beanKey);
                        }
                    }
                }
            }
        }
        return beans;
    }

    private void setBeanRefProperty(String beanKey, Bean beanWithRefProperty, Bean refBean) {
        try {
            Field declaredField = beanWithRefProperty.getValue().getClass().getDeclaredField(beanKey);
            declaredField.setAccessible(true);
            declaredField.set(beanWithRefProperty.getValue(), refBean.getValue());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error when trying to add ref properties for Bean with key: {}" + beanKey, e);
            throw new RuntimeException("Error setting field value " + beanKey + ".", e);
        }
    }

    private void propertyReader(Bean bean, Field[] fields, Set<Map.Entry<String, String>> properties) {
        for (Map.Entry<String, String> property : properties) {
            String propertyName = property.getKey();
            String propertyValue = property.getValue();
            String setMethodName = "set" + Character.toUpperCase(propertyName.charAt(0)) + propertyName.substring(1);
            Class<?> fieldType = fieldTypeReader(propertyName, fields);
            try {
                Method setMethod = bean.getValue().getClass().getMethod(setMethodName, fieldType);
                Object convertedField = convertToType(propertyValue, fieldType);
                setMethod.invoke(bean.getValue(), convertedField);
            } catch (NoSuchMethodException e) {
                log.error("No such method:{} for property: {}", setMethodName, propertyName, e);
                throw new IllegalArgumentException("Set method " + setMethodName +
                        " not found for property " + propertyName + ".");
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("Error during property with name: {} setting for Bean: {}" + propertyName, bean.getId(), e);
                throw new RuntimeException("Error setting property " + propertyName +
                        " for bean " + bean.getId() + ".", e);
            }
        }
    }

    private Class<?> fieldTypeReader(String propertyName, Field[] fields) {
        Class<?> fieldType = null;
        for (Field field : fields) {
            if (field.getName().equalsIgnoreCase(propertyName)) {
                fieldType = field.getType();
            }
        }
        return fieldType;
    }

    private Object convertToType(String value, Class<?> fieldType) {
        if (fieldType == String.class) {
            return value;
        }
        if (fieldType == int.class || fieldType == Integer.class) {
            return Integer.parseInt(value);
        } else if (fieldType == double.class || fieldType == Double.class) {
            return Double.parseDouble(value);
        } else if (fieldType == long.class || fieldType == Long.class) {
            return Long.parseLong(value);
        } else if (fieldType == boolean.class || fieldType == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (fieldType == float.class || fieldType == Float.class) {
            return Float.parseFloat(value);
        } else if (fieldType == byte.class || fieldType == Byte.class) {
            return Byte.parseByte(value);
        } else {
            log.error("Unsupported filedType: {}" + fieldType.getName());
            throw new IllegalArgumentException("Unsupported fieldType: " + fieldType.getName());
        }
    }
}
