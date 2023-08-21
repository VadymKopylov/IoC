package com.kopylov.ioc.reader.stax;

import com.kopylov.ioc.entity.BeanDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XmlBeanDefinitionStaxReaderITest {

    private final XmlBeanDefinitionStaxReader xmlBeanDefinitionStaxReader =
            new XmlBeanDefinitionStaxReader("/context-with-import.xml");


    @Test
    void testReadBeanDefinitionReturnCorrectData() {
        List<BeanDefinition> actualBeanDefinitions = xmlBeanDefinitionStaxReader.readBeanDefinition();
        BeanDefinition beanDefinition1 = actualBeanDefinitions.get(0);
        BeanDefinition beanDefinition2 = actualBeanDefinitions.get(1);
        BeanDefinition beanDefinition3 = actualBeanDefinitions.get(2);

        assertEquals("paymentService", beanDefinition1.getId());
        assertEquals("userService", beanDefinition2.getId());
        assertEquals("mailService", beanDefinition3.getId());
    }
}