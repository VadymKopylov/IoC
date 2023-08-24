package com.kopylov.ioc.reader.stax;

import com.kopylov.ioc.entity.BeanDefinition;
import com.kopylov.ioc.exception.BeanDefinitionReadException;
import com.kopylov.ioc.reader.dom.XmlBeanDefinitionReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlBeanDefinitionStaxReaderITest {

    private final XmlBeanDefinitionStaxReader xmlBeanDefinitionStaxReader =
            new XmlBeanDefinitionStaxReader("/context/context-with-import.xml");

    @Test
    void testReadBeanDefinitionReturnNotEmptyList() {
        List<BeanDefinition> actualBeanDefinitions = xmlBeanDefinitionStaxReader.readBeanDefinition();

        assertFalse(actualBeanDefinitions.isEmpty());
        assertEquals(3, actualBeanDefinitions.size());
    }

    @Test
    void testReadBeanDefinitionReturnCorrectBeanDefinitionIdAndClass() {
        List<BeanDefinition> actualBeanDefinitions = xmlBeanDefinitionStaxReader.readBeanDefinition();

        BeanDefinition firstBeanDefinition = actualBeanDefinitions.get(0);
        BeanDefinition secondBeanDefinition = actualBeanDefinitions.get(1);
        BeanDefinition thirdBeanDefinition = actualBeanDefinitions.get(2);

        assertEquals("paymentService", firstBeanDefinition.getId());
        assertEquals("userService",secondBeanDefinition.getId());
        assertEquals("mailService",thirdBeanDefinition.getId());

        assertEquals("com.kopylov.ioc.entity.PaymentService",firstBeanDefinition.getClazz());
        assertEquals("com.kopylov.ioc.entity.UserService",secondBeanDefinition.getClazz());
        assertEquals("com.kopylov.ioc.entity.MailService",thirdBeanDefinition.getClazz());
    }

    @Test
    void testReadBeanDefinitionReturnCorrectBeanDefinitionProperty(){
        List<BeanDefinition> actualBeanDefinitions = xmlBeanDefinitionStaxReader.readBeanDefinition();

        BeanDefinition firstBeanDefinition = actualBeanDefinitions.get(0);
        BeanDefinition secondBeanDefinition = actualBeanDefinitions.get(1);
        BeanDefinition thirdBeanDefinition = actualBeanDefinitions.get(2);

        assertEquals("visa",firstBeanDefinition.getProperty().get("paymentType"));
        assertEquals("Vadym",secondBeanDefinition.getProperty().get("user"));
        assertEquals("POP3",thirdBeanDefinition.getProperty().get("protocol"));
        assertEquals("3000",thirdBeanDefinition.getProperty().get("port"));
    }

    @Test
    void testReadBeanDefinitionReturnCorrectBeanDefinitionRefProperty(){
        List<BeanDefinition> actualBeanDefinitions = xmlBeanDefinitionStaxReader.readBeanDefinition();

        BeanDefinition firstBeanDefinition = actualBeanDefinitions.get(0);
        BeanDefinition secondBeanDefinition = actualBeanDefinitions.get(1);

        assertEquals("mailService",firstBeanDefinition.getRefProperty().get("mailService"));
        assertEquals("mailService",secondBeanDefinition.getRefProperty().get("mailService"));
    }

    @Test
    void testThrowExceptionWhenXmlWrittenWrong(){
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader("/context/wrong-context.xml");
        Assertions.assertThrows(BeanDefinitionReadException.class, xmlBeanDefinitionReader::readBeanDefinition);
    }
}