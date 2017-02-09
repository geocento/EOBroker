package com.geocento.webapps.eobroker.common.server.Utils;

import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class WMSCapabilities {

    public static class WMSLayer {
        String name;
        String description;
        String baseUrl;
        // WMS or WFS type of layer
        String layerName;
        String version;
        Extent bounds;
        String credits;
        // layer styles supported by the server
        ArrayList<LayerStyle> styles;
        // SRS to use for the call
        List<String> supportedSRS;

        public WMSLayer() {
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getLayerName() {
            return layerName;
        }

        public void setLayerName(String layerName) {
            this.layerName = layerName;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public Extent getBounds() {
            return bounds;
        }

        public void setBounds(Extent bounds) {
            this.bounds = bounds;
        }

        public String getCredits() {
            return credits;
        }

        public void setCredits(String credits) {
            this.credits = credits;
        }

        public ArrayList<LayerStyle> getStyles() {
            return styles;
        }

        public void setStyles(ArrayList<LayerStyle> styles) {
            this.styles = styles;
        }

        public List<String> getSupportedSRS() {
            return supportedSRS;
        }

        public void setSupportedSRS(List<String> supportedSRS) {
            this.supportedSRS = supportedSRS;
        }
    }

    public class LayerStyle {

        private String name;

        public LayerStyle() {
        }

        public LayerStyle(String name) {
            this.name = name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

    }

	static public List<String> supportedSRSs = ListUtil.toList(new String[]{"EPSG:3857", "EPSG:102100", "EPSG:900913"}); //, "CRS:84", "EPSG:4326"});
	
	private String title;
	private String description;
	private String baseAddress;
	private String version;
	private ArrayList<WMSLayer> layersList;
	private String name;
	
	public WMSCapabilities() {
	}

	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getBaseAddress() {
		return baseAddress;
	}


	public void setBaseAddress(String baseAddress) {
		this.baseAddress = baseAddress;
	}


	public String getVersion() {
		return version;
	}


	public void setVersion(String version) {
		this.version = version;
	}


	public ArrayList<WMSLayer> getLayersList() {
		return layersList;
	}


	public void setLayersList(ArrayList<WMSLayer> layersList) {
		this.layersList = layersList;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

    public void extractWMSXMLResources(String url) throws Exception {
		// parse XML response
/*
		Document document = XMLUtil.getDocument(stripInvalidXmlCharacters(response));
*/
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder domBuilder = dbf.newDocumentBuilder();
        URL resURL = new URL(url);
        Document document = domBuilder.parse(resURL.openStream());
		// get top element
		Element topElement = document.getDocumentElement();
		if(topElement.getNodeName().equalsIgnoreCase("ows:ExceptionReport")) {
			throw new Exception("Error querying service");
		}
		String topName = topElement.getNodeName();
		if(topName.contentEquals("WMS_Capabilities") || topElement.getNodeName().contentEquals("WMT_MS_Capabilities")) {
			version = topElement.getAttribute("version");
			Node serviceNode = XMLUtil.getUniqueNode(topElement, "Service");
			title = XMLUtil.getUniqueValue(serviceNode, "Title");
			name = XMLUtil.getUniqueValue(serviceNode, "Name");
			description = XMLUtil.getUniqueValue(serviceNode, "Abstract");
			Node capabilityNode = XMLUtil.getUniqueNode(topElement, "Capability");
			Node layerNodes = XMLUtil.getUniqueNode((Element) capabilityNode, "Layer");
			layersList = new ArrayList<WMSLayer>();
			if(layerNodes != null) {
				parseXMLLayer(layerNodes, null);
			}
			return;
		}
		throw new Exception("Unsupported protocol");
	}

	private void parseXMLLayer(Node layerNode, WMSLayer parentLayer) throws Exception {
		WMSLayer layer = new WMSLayer();
        layer.setBaseUrl(baseAddress);
        layer.setVersion(version);
        layer.setName(XMLUtil.getUniqueValue(layerNode, "Title"));
        layer.setLayerName(XMLUtil.getUniqueValue(layerNode, "Name"));
        layer.setDescription(XMLUtil.getUniqueValue(layerNode, "Abstract"));
        Node attributionNode = XMLUtil.getUniqueNode((Element) layerNode, "Attribution");
        if(attributionNode != null) {
            layer.setCredits(XMLUtil.getUniqueValue(attributionNode, "Title"));
        }
		// go for EX_GeographicBoundingBox values first
		if(XMLUtil.getUniqueNode((Element) layerNode, "EX_GeographicBoundingBox") != null) {
			try {
				Node boundBox = XMLUtil.getUniqueNode((Element) layerNode, "EX_GeographicBoundingBox");
                Extent extent = new Extent();
                extent.setWest(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "westBoundLongitude")));
                extent.setSouth(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "southBoundLatitude")));
                extent.setEast(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "eastBoundLongitude")));
                extent.setNorth(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "northBoundLatitude")));
				layer.setBounds(extent);
			} catch(Exception e) {
			}
		} else if(XMLUtil.getUniqueNode((Element) layerNode, "LatLonBoundingBox") != null) {
				try {
					Element boundBox = (Element) XMLUtil.getUniqueNode((Element) layerNode, "LatLonBoundingBox");
                    Extent extent = new Extent();
                    extent.setWest(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "minx")));
                    extent.setSouth(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "miny")));
                    extent.setEast(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "maxx")));
                    extent.setNorth(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "maxy")));
                    layer.setBounds(extent);
				} catch(Exception e) {
				}
		} else if(XMLUtil.getUniqueNode((Element) layerNode, "BoundingBox") != null) {
			try {
				// TODO - check the CRS/SRS
				Element boundBox = (Element) XMLUtil.getUniqueNode((Element) layerNode, "BoundingBox");
                Extent extent = new Extent();
                extent.setWest(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "minx")));
                extent.setSouth(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "miny")));
                extent.setEast(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "maxx")));
                extent.setNorth(Double.parseDouble(XMLUtil.getUniqueValue(boundBox, "maxy")));
                layer.setBounds(extent);
			} catch(Exception e) {
			}
		}
		
		// assign an SRS
		List<String> listSRSs = XMLUtil.getNodesValue(layerNode, version.startsWith("1.3") ? "CRS" : "SRS");
        layer.setSupportedSRS(listSRSs);
		// check for styles
		List<Node> styleNodes = XMLUtil.getNodes(layerNode, "Style");
		ArrayList<LayerStyle> styles = new ArrayList<LayerStyle>();
		if(styleNodes != null) {
			for(Node styleNode : styleNodes) {
				final String name = XMLUtil.getUniqueValue(styleNode, "Name");
				if(ListUtil.findIndex(styles, new ListUtil.CheckValue<LayerStyle>() {

					@Override
					public boolean isValue(LayerStyle value) {
						return name.contentEquals(value.getName());
					}
				}) == -1) {
					styles.add(new LayerStyle(name));
				}
			}
		}
        layer.setStyles(styles);
        // sometimes attributes need to be fetched from the parent layer
        if(parentLayer != null) {
            if(layer.getBounds() == null) {
                layer.setBounds(parentLayer.getBounds());
            }
            if(layer.getDescription() == null) {
                layer.setDescription(parentLayer.getDescription());
            }
            if(parentLayer.getStyles() != null) {
                // it is OK to do shallow copy as styles are immutable
                for (LayerStyle style : parentLayer.getStyles()) {
                    layer.getStyles().add(style);
                }
            }
            if(layer.getSupportedSRS() == null) {
                layer.setSupportedSRS(parentLayer.getSupportedSRS());
            }
        }

		// parse layers
		List<Node> childrenNodes = XMLUtil.getNodes(layerNode, "Layer");
		if(childrenNodes != null && childrenNodes.size() > 0) {
			for(Node childNode : childrenNodes) {
				parseXMLLayer(childNode, layer);
			}
		} else {
			// no more children layer so we can add it to the list
			layersList.add(layer);
		}
	}

    public static String stripInvalidXmlCharacters(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (XMLChar.isValid(c)) {
                sb.append(c);
            }
        }

        return sb.toString();
    }


}
