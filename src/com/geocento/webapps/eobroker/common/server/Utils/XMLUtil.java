package com.geocento.webapps.eobroker.common.server.Utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class XMLUtil {
	
	public static SimpleDateFormat timeFormat = getTimeFormatUTC();
	
	static SimpleDateFormat getTimeFormatUTC() {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		fmt.setTimeZone(TimeZone.getTimeZone("GMT"));
		return fmt;
	}
	
	public static Document getDocument(String xmlResponse) throws ParserConfigurationException, SAXException, IOException {
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		
		Document doc = db.parse(new StringBufferInputStream(xmlResponse));
		doc.getDocumentElement().normalize();
		System.out.println("Root element " + doc.getDocumentElement().getNodeName());
		
		return doc;
	}
	
	public static String getUniqueValue(Node node, String tagName) {
		NodeList nodeList = ((Element) node).getElementsByTagName(tagName);
		return (nodeList != null && nodeList.getLength() > 0 && nodeList.item(0).getFirstChild() != null) ? nodeList.item(0).getFirstChild().getTextContent() : "";
	}
	
	public static String createQuery(String queryString, String... values) {
		String query = new String(queryString);
		int index = 1;
		for(String value : values) {
			query = query.replace("%" + index, value);
			index++;
		}
		return query;
	}

	public static Node getUniqueNode(Element node, String tagName) {
		NodeList nodeList = ((Element) node).getElementsByTagName(tagName);
		return (nodeList != null && nodeList.getLength() > 0) ? nodeList.item(0) : null;
	}

	public static List<Node> getNodes(Node node, String tagName) {
		List<Node> nodes = new ArrayList<Node>();
		NodeList nodeList = ((Element) node).getElementsByTagName(tagName);
		for (int index = 0; index < nodeList.getLength(); index++) {
		    nodes.add(nodeList.item(index));
		}
		return nodes;
	}

    public static String innerXml(Node xmlNode) {
        String xmlContent = "";
        NodeList nodeList = xmlNode.getChildNodes();
        for(int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            xmlContent += toXml(node);
        }
        return xmlContent;
    }

    public static String toXml(Node node) {
        // you may prefer to use single instances of Transformer, and
        // StringWriter rather than create each time. That would be up to your
        // judgement and whether your app is single threaded etc
        StreamResult xmlOutput = new StreamResult(new StringWriter());
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(node), xmlOutput);
        } catch(Exception e) {

        }
        return xmlOutput.getWriter().toString();
    }

	public static String addValue(String name, String value) {
		return "<" + name + ">" + value + "</" + name + ">";
	}


    public static List<String> getNodesValue(Node node, String tagName) {
        List<String> nodes = new ArrayList<String>();
        NodeList nodeList = ((Element) node).getElementsByTagName(tagName);
        for (int index = 0; index < nodeList.getLength(); index++) {
            nodes.add(nodeList.item(index).getFirstChild().getTextContent());
        }
        return nodes;
    }

}

