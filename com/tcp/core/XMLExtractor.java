package com.tcp.core;
/**
class: XMLExtractor
Purpose: used to extract NODES from xml config file.
Notes:
Author: Tim Lane
Date: 25/03/2014
**/

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import org.apache.log4j.Logger;

 public class XMLExtractor {
   
     
  StubLog stubLog = new StubLog();
  String logMsg = "";
  private Logger logger;
  
    public static final String XML_TRUE = "TRUE";
    public static final String XML_FALSE = "FALSE";

    private Element xmlRootElement;

    public XMLExtractor(InputStream inStream)
    {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setValidating(false);
        try
        {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document xmlDoc = docBuilder.parse(inStream);
            xmlRootElement = xmlDoc.getDocumentElement();
        } catch (Exception e) {
          logMsg = "XMLExtractor: error processing XML file " + inStream + " : " + e;
          stubLog.addMessage(logMsg, logger,"ERROR");
        }
    }

    public XMLExtractor(Element inElement)
    {
        xmlRootElement = inElement;
    }

    public Element getElement(String elementName)
    {
        NodeList nodes = xmlRootElement.getElementsByTagName(elementName);
        if (nodes.getLength() == 0)
            return null;
        else
            return (Element)nodes.item(0);
    }

    public NodeList getNodeList(String elementName)
    {
        return xmlRootElement.getElementsByTagName(elementName);
    }

    public Element getXmlRootElement() {
        return xmlRootElement;
    }

    public void setXmlRootElement(Element xmlRootElement) {
        this.xmlRootElement = xmlRootElement;
    }

    public static boolean booleanFromString(String boolValue)
    {
        if (boolValue.equals(XML_TRUE))
            return true;
        else
            return false;
    }    
}
