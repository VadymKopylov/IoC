package com.kopylov.ioc.reader.dom;

import com.kopylov.ioc.entity.BeanDefinition;
import com.kopylov.ioc.exception.BeanDefinitionReadException;
import com.kopylov.ioc.reader.BeanDefinitionReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
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
            try (InputStream inputStream = getClass().getResourceAsStream(path)) {
                Document document = createDocument(inputStream);
                readImportedResources(beanDefinitions, document);
                beanDefinitions.addAll(documentBeanDefinitionReader(document));
            } catch (ParserConfigurationException e) {
                throw new BeanDefinitionReadException("Error while configuring the XML parser", e);
            } catch (IOException e) {
                throw new BeanDefinitionReadException("I/O error occurred while reading the XML file", e);
            } catch (SAXException e) {
                throw new BeanDefinitionReadException("Error while reading the XML file ", e);
            }
        }
        return beanDefinitions;
    }

    Document createDocument(InputStream inputStream)
            throws ParserConfigurationException, IOException, SAXException {

        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(inputStream);
    }

    List<BeanDefinition> documentBeanDefinitionReader(Document document) {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        NodeList beanList = document.getElementsByTagName("bean");
        for (int i = 0; i < beanList.getLength(); i++) {
            Node bean = beanList.item(i);
            if (bean == null || bean.getNodeType() != Node.ELEMENT_NODE) {
                throw new IllegalArgumentException("Invalid bean node");
            }
            Element beanElement = (Element) bean;
            BeanDefinition beanDefinition = createBeanDefinition(beanElement);
            NodeList beanProperty = beanElement.getElementsByTagName("property");
            for (int j = 0; j < beanProperty.getLength(); j++) {
                Node property = beanProperty.item(j);
                setBeanDefinitionProperty(property, beanDefinition);
            }
            beanDefinitions.add(beanDefinition);
        }
        return beanDefinitions;
    }

    private void readImportedResources(List<BeanDefinition> beanDefinitions, Document document)
            throws IOException, ParserConfigurationException, SAXException {

        NodeList importNodes = document.getElementsByTagName("import");
        for (int i = 0; i < importNodes.getLength(); i++) {
            Node importNode = importNodes.item(i);
            if (importNode.getNodeType() == Node.ELEMENT_NODE) {
                Element importElement = (Element) importNode;
                String resourcePath = importElement.getAttribute("resource");
                if (!resourcePath.startsWith("/")) {
                    try (InputStream importedInputStream = getClass().getResourceAsStream("/context/" + resourcePath)) {
                        Document importedDocument = createDocument(importedInputStream);
                        beanDefinitions.addAll(documentBeanDefinitionReader(importedDocument));
                    }
                } else {
                    try (InputStream importedInputStream = getClass().getResourceAsStream("/context" + resourcePath)) {
                        Document importedDocument = createDocument(importedInputStream);
                        beanDefinitions.addAll(documentBeanDefinitionReader(importedDocument));
                    }
                }
            }
        }
    }

    private BeanDefinition createBeanDefinition(Element beanElement) {
        String id = beanElement.getAttribute("id");
        String clazz = beanElement.getAttribute("class");
        if (id.isEmpty() || clazz.isEmpty()) {
            throw new IllegalArgumentException("Bean attributes 'id' and 'class' must not be empty");
        }
        return new BeanDefinition(id, clazz);
    }

    private void setBeanDefinitionProperty(Node property, BeanDefinition beanDefinition) {
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