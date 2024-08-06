package com.emna.micro_service2.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class XmlUtils {

    public static Map<String, String> extractAttributes(String xmlContent) {
        Map<String, String> attributes = new HashMap<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlContent.getBytes()));

            // Commencez à partir de l'élément racine
            Element rootElement = document.getDocumentElement();
            traverseNodes(rootElement, attributes, "");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return attributes;
    }

    private static void traverseNodes(Node node, Map<String, String> attributes, String parentPath) {
        String currentPath = parentPath.isEmpty() ? node.getNodeName() : parentPath + "." + node.getNodeName();

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);

                if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                    traverseNodes(childNode, attributes, currentPath);
                } else if (childNode.getNodeType() == Node.TEXT_NODE) {
                    String textContent = childNode.getTextContent().trim();
                    if (!textContent.isEmpty()) {
                        attributes.put(currentPath, textContent);
                    }
                }
            }
        }
    }

    public static String convertXmlToJson(String xmlContent) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        JsonNode node = xmlMapper.readTree(xmlContent.getBytes());
        ObjectMapper jsonMapper = new ObjectMapper();
        return jsonMapper.writeValueAsString(node);
    }

    public static boolean isValidXml(String xmlContent) {
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.readTree(xmlContent.getBytes());
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
