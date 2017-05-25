package com.geocento.webapps.eobroker.common.client.utils;

import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 24/05/2017.
 */
public class XMLUtil {

    public static Node getUniqueNode(Node node, String name) {

        if(node == null) {
            return null;
        }
        NodeList nodeList = node.getChildNodes();
        if(nodeList == null) {
            return null;
        }
        boolean withPrefix = name.contains(":");
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    (withPrefix ? child.getNodeName().contentEquals(name) :
                            (":" + child.getNodeName()).endsWith(":" + name))) {
                return child;
            }
        }

        return null;
    }

    public static List<Node> getNodes(Node node, String name) {
        if(node == null) {
            return null;
        }

        List<Node> nodes = new ArrayList<Node>();

        boolean withPrefix = name.contains(":");
        for (Node child = node.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                    (withPrefix ? child.getNodeName().contentEquals(name) :
                            (":" + child.getNodeName()).endsWith(":" + name))) {
                nodes.add(child);
            }
        }
        return nodes;
    }

    public static List<String> getNodesValue(Node node, String name) {
        List<String> values = new ArrayList<String>();

        if(node != null) {
            NodeList nodeList = node.getChildNodes();
            for(int index = 0; index < nodeList.getLength(); index++) {
                Node uniqueNode = nodeList.item(index);
                if(uniqueNode.getNodeName().equalsIgnoreCase(name)) {
                    if(uniqueNode.getFirstChild() instanceof Text) {
                        values.add(((Text) uniqueNode.getFirstChild()).getData());
                    } else {
                        values.add("Undefined");
                    }
                }
            }
        }

        return values;
    }

    public static String getUniqueNodeValue(Node node, String name) {
        return getNodeValue(getUniqueNode(node, name));
    }

    public static String getNodeValue(Node node) {
        if(node != null &&node.getFirstChild() instanceof Text) {
            return ((Text) node.getFirstChild()).getData();
        }
        return null;
    }

}
