package com.kopylov.ioc.reader.dom;

import com.kopylov.ioc.entity.BeanDefinition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class XmlBeanDefinitionReaderTest {

    private final String XML_CONTEXT =
            "<beans>\n" +
            "    <bean id=\"paymentService\" class=\"com.kopylov.ioc.entity.PaymentService\">\n" +
            "        <property name=\"mailService\" ref=\"mailService\"/>\n" +
            "        <property name=\"paymentType\" value=\"visa\"/>\n" +
            "    </bean>\n" +
            "\n" +
            "    <bean id=\"userService\" class=\"com.kopylov.ioc.entity.UserService\">\n" +
            "        <property name=\"mailService\" ref=\"mailService\"/>\n" +
            "        <property name=\"user\" value=\"Vadym\"/>\n" +
            "    </bean>\n" +
            "\n" +
            "    <bean id=\"mailService\" class=\"com.kopylov.ioc.entity.MailService\">\n" +
            "        <property name=\"protocol\" value=\"POP3\"/>\n" +
            "        <property name=\"port\" value=\"3000\"/>\n" +
            "    </bean>\n" +
            "</beans>";

    private final XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(XML_CONTEXT);
    private List<BeanDefinition> beanDefinitions;

    @BeforeEach
    void setUp() throws ParserConfigurationException, IOException, SAXException {
        beanDefinitions = xmlBeanDefinitionReader.documentBeanDefinitionReader
                (xmlBeanDefinitionReader.createDocument(new ByteArrayInputStream(XML_CONTEXT.getBytes())));
    }

    @Test
    void testInputStreamBeanDefinitionReaderReturnsNotEmptyList() {
        assertNotEquals(0, beanDefinitions.size());
    }

    @Test
    void testInputStreamBeanDefinitionReaderReturnsCorrectQuantityBeanDefinitions() {
        assertEquals(3, beanDefinitions.size());
    }

    @Test
    void testInputStreamBeanDefinitionReaderReturnsCorrectBeanDefinitionsIdAndClass() {
        assertEquals("paymentService",beanDefinitions.get(0).getId());
        assertEquals("userService",beanDefinitions.get(1).getId());
        assertEquals("mailService",beanDefinitions.get(2).getId());

        assertEquals("com.kopylov.ioc.entity.PaymentService",beanDefinitions.get(0).getClazz());
        assertEquals("com.kopylov.ioc.entity.UserService",beanDefinitions.get(1).getClazz());
        assertEquals("com.kopylov.ioc.entity.MailService",beanDefinitions.get(2).getClazz());
    }

    @Test
    void testInputStreamBeanDefinitionReaderReturnsCorrectBeanDefinitionProperties() {
        Map<String, String> paymentServiceProperty = beanDefinitions.get(0).getProperty();
        Map<String, String> userServiceProperty = beanDefinitions.get(1).getProperty();
        Map<String, String> mailServiceProperty = beanDefinitions.get(2).getProperty();

        assertEquals(1,paymentServiceProperty.size());
        assertEquals(1,userServiceProperty.size());
        assertEquals(2,mailServiceProperty.size());

        assertTrue(paymentServiceProperty.containsKey("paymentType"));
        assertTrue(userServiceProperty.containsKey("user"));
        assertTrue(mailServiceProperty.containsKey("protocol"));
        assertTrue(mailServiceProperty.containsKey("port"));

        assertEquals("visa",paymentServiceProperty.get("paymentType"));
        assertEquals("Vadym",userServiceProperty.get("user"));
        assertEquals("POP3",mailServiceProperty.get("protocol"));
        assertEquals("3000",mailServiceProperty.get("port"));
    }

    @Test
    void testInputStreamBeanDefinitionReaderReturnsCorrectBeanDefinitionRefProperties(){
        Map<String, String> paymentServiceProperty = beanDefinitions.get(0).getRefProperty();
        Map<String, String> userServiceProperty = beanDefinitions.get(1).getRefProperty();
        Map<String, String> mailServiceProperty = beanDefinitions.get(2).getRefProperty();

        assertTrue(paymentServiceProperty.containsKey("mailService"));
        assertTrue(userServiceProperty.containsKey("mailService"));
        assertTrue(mailServiceProperty.isEmpty());

        assertEquals(paymentServiceProperty.get("mailService"), "mailService");
        assertEquals(userServiceProperty.get("mailService"),"mailService");
    }

}
