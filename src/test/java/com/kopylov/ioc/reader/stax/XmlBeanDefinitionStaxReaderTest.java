package com.kopylov.ioc.reader.stax;

import com.kopylov.ioc.entity.BeanDefinition;
import org.junit.jupiter.api.Test;

import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlBeanDefinitionStaxReaderTest {

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

    private final XmlBeanDefinitionStaxReader xmlBeanDefinitionStaxReader = new XmlBeanDefinitionStaxReader(XML_CONTEXT);

    @Test
    void testReadBeanDefinition() throws XMLStreamException, IOException {
        List<BeanDefinition> beanDefinitionList =
                xmlBeanDefinitionStaxReader.inputStreamBeanDefinitionReader(new ByteArrayInputStream(XML_CONTEXT.getBytes()));

        assertEquals("paymentService", beanDefinitionList.get(0).getId());
        assertEquals("userService", beanDefinitionList.get(1).getId());
        assertEquals("mailService", beanDefinitionList.get(2).getId());

        assertEquals("com.kopylov.ioc.entity.PaymentService", beanDefinitionList.get(0).getClazz());
        assertEquals("com.kopylov.ioc.entity.UserService", beanDefinitionList.get(1).getClazz());
        assertEquals("com.kopylov.ioc.entity.MailService", beanDefinitionList.get(2).getClazz());

        assertTrue(beanDefinitionList.get(0).getProperty().containsKey("paymentType"));
        assertTrue(beanDefinitionList.get(0).getRefProperty().containsKey("mailService"));
        assertTrue(beanDefinitionList.get(1).getProperty().containsKey("user"));
        assertTrue(beanDefinitionList.get(1).getRefProperty().containsKey("mailService"));
        assertTrue(beanDefinitionList.get(2).getProperty().containsKey("protocol"));
        assertTrue(beanDefinitionList.get(2).getProperty().containsKey("port"));

        assertTrue(beanDefinitionList.get(0).getProperty().containsValue("visa"));
        assertTrue(beanDefinitionList.get(0).getRefProperty().containsValue("mailService"));
        assertTrue(beanDefinitionList.get(1).getProperty().containsValue("Vadym"));
        assertTrue(beanDefinitionList.get(1).getRefProperty().containsValue("mailService"));
        assertTrue(beanDefinitionList.get(2).getProperty().containsValue("POP3"));
        assertTrue(beanDefinitionList.get(2).getProperty().containsValue("3000"));
    }
}