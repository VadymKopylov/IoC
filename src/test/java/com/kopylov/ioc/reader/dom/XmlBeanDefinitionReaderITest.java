package com.kopylov.ioc.reader.dom;

import com.kopylov.ioc.entity.BeanDefinition;
import com.kopylov.ioc.exception.BeanDefinitionReadException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class XmlBeanDefinitionReaderITest {

    private final String context = "/context/context-with-import.xml";
    private final String wrongContext = "/context/wrong-context.xml";
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    @Test
    void testReadBeanDefinitionReturnNotEmptyList() {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(context);
        List<BeanDefinition> actualBeanDefinitions = xmlBeanDefinitionReader.readBeanDefinition();

        assertFalse(actualBeanDefinitions.isEmpty());
        assertEquals(3, actualBeanDefinitions.size());
    }

    @Test
    void testReadBeanDefinitionReturnCorrectBeanDefinitionIdAndClass() {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(context);
        List<BeanDefinition> actualBeanDefinitions = xmlBeanDefinitionReader.readBeanDefinition();

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
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(context);
        List<BeanDefinition> actualBeanDefinitions = xmlBeanDefinitionReader.readBeanDefinition();

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
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(context);
        List<BeanDefinition> actualBeanDefinitions = xmlBeanDefinitionReader.readBeanDefinition();

        BeanDefinition firstBeanDefinition = actualBeanDefinitions.get(0);
        BeanDefinition secondBeanDefinition = actualBeanDefinitions.get(1);
        BeanDefinition thirdBeanDefinition = actualBeanDefinitions.get(2);

        assertEquals("mailService",firstBeanDefinition.getRefProperty().get("mailService"));
        assertEquals("mailService",secondBeanDefinition.getRefProperty().get("mailService"));
        assertNull(thirdBeanDefinition.getRefProperty());

    }

    @Test
    void testThrowExceptionWhenXmlWrittenWrong(){
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(wrongContext);
        Assertions.assertThrows(BeanDefinitionReadException.class, xmlBeanDefinitionReader::readBeanDefinition);
    }

    //correct handling and testing of exceptions that can only occur if you change the configuration??
    @Test
    void testReadBeanDefinitionThrowConfigurationExceptionWhenDocumentBuilder() {
        Assertions.assertThrows(ParserConfigurationException.class, () ->
                factory.setFeature("http://apache.org/xml/features/nonexistent-feature", true));
    }
}