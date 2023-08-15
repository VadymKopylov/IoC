package com.kopylov.ioc.reader.stax;

import com.kopylov.ioc.entity.BeanDefinition;
import com.kopylov.ioc.exception.BeanDefinitionReadException;
import com.kopylov.ioc.reader.BeanDefinitionReader;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlBeanDefinitionStaxReader implements BeanDefinitionReader {

    private final String[] paths;
    private final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

    public XmlBeanDefinitionStaxReader(String... paths) {
        this.paths = paths;
    }

    @Override
    public List<BeanDefinition> readBeanDefinition() {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        for (String path : paths) {
            try (InputStream inputStream = getClass().getResourceAsStream(path)) {
                beanDefinitions.addAll(inputStreamBeanDefinitionReader(inputStream));
            } catch (IOException e) {
                throw new BeanDefinitionReadException("I/O error occurred while reading the XML file", e);
            } catch (XMLStreamException e) {
                throw new BeanDefinitionReadException("Error while parsing the XML", e);
            }
        }
        return beanDefinitions;
    }

    List<BeanDefinition> inputStreamBeanDefinitionReader(InputStream inputStream) throws XMLStreamException {
        List<BeanDefinition> beanDefinitions = new ArrayList<>();
        BeanDefinition beanDefinition = null;
        XMLEventReader reader = xmlInputFactory.createXMLEventReader(inputStream);
        while (reader.hasNext()) {
            XMLEvent xmlEvent = reader.nextEvent();
            if (xmlEvent.isStartElement()) {
                StartElement startElement = xmlEvent.asStartElement();
                if (startElement.getName().getLocalPart().equals("bean")) {
                    beanDefinition = new BeanDefinition();
                    Attribute idAttribute = startElement.getAttributeByName(new QName("id"));
                    Attribute classAttribute = startElement.getAttributeByName(new QName("class"));
                    if (idAttribute != null && classAttribute != null) {
                        beanDefinition.setId(idAttribute.getValue());
                        beanDefinition.setClazz(classAttribute.getValue());
                    }
                } else if (startElement.getName().getLocalPart().equals("property") && beanDefinition != null) {
                    Attribute propertyName = startElement.getAttributeByName(new QName("name"));
                    Attribute propertyRef = startElement.getAttributeByName(new QName("ref"));
                    Attribute propertyValue = startElement.getAttributeByName(new QName("value"));
                    if (propertyName != null && propertyRef != null) {
                        if (beanDefinition.getRefProperty() == null) {
                            Map<String, String> refPropertyMap = new HashMap<>();
                            refPropertyMap.put(propertyName.getValue(), propertyRef.getValue());
                            beanDefinition.setRefProperty(refPropertyMap);
                        } else {
                            beanDefinition.getRefProperty().put(propertyName.getValue(), propertyRef.getValue());
                        }
                    } else if (propertyName != null && propertyValue != null) {
                        if (beanDefinition.getProperty() == null) {
                            Map<String, String> propertyMap = new HashMap<>();
                            propertyMap.put(propertyName.getValue(), propertyValue.getValue());
                            beanDefinition.setProperty(propertyMap);
                        } else {
                            beanDefinition.getProperty().put(propertyName.getValue(), propertyValue.getValue());
                        }
                    }
                }
            }
            if (xmlEvent.isEndElement()) {
                EndElement endElement = xmlEvent.asEndElement();
                if (endElement.getName().getLocalPart().equals("bean")) {
                    beanDefinitions.add(beanDefinition);
                    beanDefinition = null;
                }
            }
        }
        return beanDefinitions;
    }
}
