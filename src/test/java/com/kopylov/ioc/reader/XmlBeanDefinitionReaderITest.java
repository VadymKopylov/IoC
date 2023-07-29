package com.kopylov.ioc.reader;

import com.kopylov.ioc.entity.BeanDefinition;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XmlBeanDefinitionReaderITest {

    private final String path = "src/test/resources/context.xml";
    private final String pathToEmailContext = "src/test/resources/email-context.xml";
    private final XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(path,pathToEmailContext);
    private final List<BeanDefinition> actualBeanDefinitions = xmlBeanDefinitionReader.readBeanDefinition();

    @Test
    void testXmlBeanDefinitionReaderReadXmlFileThenReturnNotEmptyList() {
        assertNotEquals(0, actualBeanDefinitions.size());
    }

    @Test
    void testXmlBeanDefinitionReaderReadXmlFileThenReturnCorrectIdAndClass() {
        BeanDefinition beanDefinition = actualBeanDefinitions.get(0);

        assertEquals("paymentService", beanDefinition.getId());
        assertEquals("com.kopylov.ioc.service.PaymentService", beanDefinition.getClazz());
    }

    @Test
    void testFillPropertyReturnsNotNullPropertyMap(){
        BeanDefinition beanDefinition = actualBeanDefinitions.get(0);

        assertNotNull(beanDefinition.getProperty());
    }

    @Test
    void testFillPropertyReturnsCorrectProperty(){
        BeanDefinition beanDefinition = actualBeanDefinitions.get(0);
        Map<String, String> property = beanDefinition.getProperty();
        Map<String, String> refProperty = beanDefinition.getRefProperty();

        assertEquals("visa",property.get("paymentType"));
        assertEquals("mailService",refProperty.get("mailService"));
    }

    @Test
    void testFillPropertyReturnsCorrectPropertyFromEmailContext(){
        BeanDefinition beanDefinition = actualBeanDefinitions.get(2);
        Map<String, String> property = beanDefinition.getProperty();

        assertEquals("POP3",property.get("protocol"));
        assertEquals("3000",property.get("port"));
    }
}