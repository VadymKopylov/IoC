package com.kopylov.ioc.reader.dom;

import com.kopylov.ioc.entity.BeanDefinition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class XmlBeanDefinitionReaderITest {

    private final String path = "/context.xml";
    private final String pathToEmailContext = "/email-context.xml";
    private final XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(path, pathToEmailContext);
    private final List<BeanDefinition> actualBeanDefinitions = xmlBeanDefinitionReader.readBeanDefinition();
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    @Test
    void testGetNodeListReturnNotEmptyList() throws ParserConfigurationException, IOException, SAXException {
        NodeList actualNodeList = xmlBeanDefinitionReader.getNodeList(getClass().getResourceAsStream(pathToEmailContext));
        assertNotNull(actualNodeList);
    }

    @Test
    void testGetNodeListHaveAtLeastOneNode() throws ParserConfigurationException, IOException, SAXException {
        NodeList actualNodeList = xmlBeanDefinitionReader.getNodeList(getClass().getResourceAsStream(pathToEmailContext));
        assertTrue(actualNodeList.getLength() > 0);
    }

    @Test
    void testGetNodeListThrowIOExceptionWhenInvalidPathSpecified() {
        String wrongPath = "wrong/path/to/xml.xml";
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                xmlBeanDefinitionReader.getNodeList(getClass().getResourceAsStream(wrongPath)));
    }

    @Test
    void testGetNodeListThrowSAXExceptionWhenBeanElementUnclosed() {
        String wrongXml = "/wrong-context.xml";
        Assertions.assertThrows(SAXException.class, () ->
                xmlBeanDefinitionReader.getNodeList(getClass().getResourceAsStream(wrongXml)));
    }

    //correct handling and testing of exceptions that can only occur if you change the configuration??
    @Test
    void testGetNodeListThrowParserConfigurationExceptionWhenDocumentBuilder() {
        Assertions.assertThrows(ParserConfigurationException.class, () ->
                factory.setFeature("http://apache.org/xml/features/nonexistent-feature", true));
    }

    @Test
    void testXmlBeanDefinitionReaderReadXmlFileThenReturnNotEmptyList() {
        assertNotEquals(0, actualBeanDefinitions.size());
    }

    @Test
    void testXmlBeanDefinitionReaderReadXmlFileThenReturnCorrectIdAndClass() {
        BeanDefinition beanDefinition = actualBeanDefinitions.get(0);

        assertEquals("paymentService", beanDefinition.getId());
        assertEquals("com.kopylov.ioc.entity.PaymentService", beanDefinition.getClazz());
    }

    @Test
    void testFillPropertyReturnsNotNullPropertyMap() {
        BeanDefinition beanDefinition = actualBeanDefinitions.get(0);

        assertNotNull(beanDefinition.getProperty());
    }

    @Test
    void testFillPropertyReturnsCorrectProperty() {
        BeanDefinition beanDefinition = actualBeanDefinitions.get(0);
        Map<String, String> property = beanDefinition.getProperty();
        Map<String, String> refProperty = beanDefinition.getRefProperty();

        assertEquals("visa", property.get("paymentType"));
        assertEquals("mailService", refProperty.get("mailService"));
    }

    @Test
    void testFillPropertyReturnsCorrectPropertyFromEmailContext() {
        BeanDefinition beanDefinition = actualBeanDefinitions.get(2);
        Map<String, String> property = beanDefinition.getProperty();

        assertEquals("POP3", property.get("protocol"));
        assertEquals("3000", property.get("port"));
    }
}