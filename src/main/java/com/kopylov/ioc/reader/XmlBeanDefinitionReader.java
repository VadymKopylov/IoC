package com.kopylov.ioc.reader;

import com.kopylov.ioc.entity.BeanDefinition;
import com.kopylov.ioc.exception.BeanDefinitionReadException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XmlBeanDefinitionReader implements BeanDefinitionReader {

    private final String[] paths;
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    public XmlBeanDefinitionReader(String... paths) {
        this.paths = paths;
    }

    @Override
    public List<BeanDefinition> readBeanDefinition() {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        for (String path : paths) {
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(path);

                NodeList beanList = document.getElementsByTagName("bean");

                for (int i = 0; i < beanList.getLength(); i++) {
                    Node bean = beanList.item(i);
                    if (bean.getNodeType() == Node.ELEMENT_NODE) {
                        Element beanElement = (Element) bean;
                        String id = beanElement.getAttribute("id");
                        String clazz = beanElement.getAttribute("class");
                        BeanDefinition beanDefinition = new BeanDefinition(id, clazz);

                        NodeList propertyList = beanElement.getChildNodes();
                        for (int j = 0; j < propertyList.getLength(); j++) {
                            Node property = propertyList.item(j);
                            if (property.getNodeType() == Node.ELEMENT_NODE) {
                                Element propertyElement = (Element) property;
                                String propertyName = propertyElement.getAttribute("name");
                                String propertyValue = propertyElement.getAttribute("value");
                                String propertyRef = propertyElement.getAttribute("ref");
                                if (!propertyValue.isEmpty()) {
                                    beanDefinition.getProperty().put(propertyName, propertyValue);
                                } else if (!propertyRef.isEmpty()) {
                                    beanDefinition.getRefProperty().put(propertyName, propertyRef);
                                }
                            }
                        }
                        beanDefinitions.add(beanDefinition);
                    }
                }
            } catch (ParserConfigurationException e) {
                throw new BeanDefinitionReadException("Error while configuring the XML parser", e);
            } catch (SAXException e) {
                throw new BeanDefinitionReadException("Error while parsing the XML", e);
            } catch (IOException e) {
                throw new BeanDefinitionReadException("I/O error occurred while reading the XML file", e);
            }
        }
        return beanDefinitions;
    }
}