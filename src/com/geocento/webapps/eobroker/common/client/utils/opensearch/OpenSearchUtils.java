package com.geocento.webapps.eobroker.common.client.utils.opensearch;

import com.geocento.webapps.eobroker.common.client.utils.XMLUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 07/10/2016.
 */
public class OpenSearchUtils {

    static private DateTimeFormat fmt = DateTimeFormat.getFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

    static List<String> supportedFormats = ListUtil.toListArg("application/atom+xml", "application/json");

    static private List<String> reservedParameters = ListUtil.toList(
            new String[]{"start", "end", "searchTerms", "count", "startIndex", "startPage", "language"});

    private static NumberFormat format = NumberFormat.getFormat("#.##");

    public static void getRecords(int start, int startPage, OpenSearchDescription openSearchDescription, String wktGeometry, Date startDate, Date stopDate, String query, AsyncCallback<SearchResponse> callback) throws Exception {
        // find the suitable URL
        Url url = selectSuitableUrl(openSearchDescription.getUrl());
        if(url == null) {
            throw new Exception("No supported formats available in url templates");
        }
        String selectedFormat = url.getType();
        String datasetURL = url.getTemplate();
        // build request
        String requestURL;
        // get basic URL
        requestURL = datasetURL.split("\\?")[0];
        List<Parameter> supportedParameters = url.getParameters();
        HashMap<String, String> requestedParameters = new HashMap<String, String>();
        Parameter searchParameter = getSupportedParameterType(supportedParameters, "", "searchTerms");
        if(searchParameter != null) {
            requestedParameters.put(searchParameter.getName(), query);
        }
        Parameter startParameter = getSupportedParameterType(supportedParameters, "time", "start");
        if(startParameter != null) {
            requestedParameters.put(startParameter.getName(), fmt.format(startDate));
        }
        Parameter stopParameter = getSupportedParameterType(supportedParameters, "time", "end");
        if(stopParameter != null) {
            requestedParameters.put(stopParameter.getName(), fmt.format(stopDate));
        }
        Parameter geometryParameter = getSupportedParameterType(supportedParameters, "", "geometry");
        if(geometryParameter != null) {
            requestedParameters.put(geometryParameter.getName(), wktGeometry);
        } else {
            geometryParameter = getSupportedParameterType(supportedParameters, "", "bbox");
            if(geometryParameter != null) {
                Extent extent = AoIUtil.getExtent(AoIUtil.fromWKT(wktGeometry));
                // from http://www.opensearch.org/Specifications/OpenSearch/Extensions/Geo/1.0/Draft_2
                // geo:box ~ west, south, east, north
                requestedParameters.put(geometryParameter.getName(), extent.getWest() + "," + extent.getSouth() + "," + extent.getEast() + "," + extent.getNorth());
            }
        }
        // finally add the start and limit
        Parameter startIndexParameter = getSupportedParameterType(supportedParameters, "", "startIndex");
        if(startIndexParameter == null) {
            throw new Exception("Missing start index parameter in URL");
        }
        requestedParameters.put(startIndexParameter.getName(), (url.getIndexOffset() + start) + "");
        Parameter startPageParameter = getSupportedParameterType(supportedParameters, "", "startPage");
        if(startPageParameter == null) {
            throw new Exception("Missing count parameter in URL");
        }
        requestedParameters.put(startPageParameter.getName(), (url.getPageOffset() + startPage) + "");

        List<String> requestedValues = new ArrayList<String>();
        for(String parameterName : requestedParameters.keySet()) {
            requestedValues.add(parameterName + "=" + requestedParameters.get(parameterName));
        }
        requestURL += "?" + URL.encodeQueryString(com.geocento.webapps.eobroker.common.client.utils.StringUtils.join(requestedValues, "&"));
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, maybeProxyRequest(requestURL));
        builder.setHeader("Content-Type", selectedFormat);
        try {
            Request owsRequest = builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                    callback.onFailure(new Throwable("Couldn't retrieve Data"));
                }

                public void onResponseReceived(Request request, Response response) {
                    try {
                        if (200 == response.getStatusCode()) {
                            // parse response
                            SearchResponse searchResponse = parseSearchResponse(response.getText(), selectedFormat);
                            // do not forget to subtract the offsets
                            searchResponse.setStart(searchResponse.getStart() - url.getIndexOffset());
                            callback.onSuccess(searchResponse);
                        } else {
                            throw new Exception("Error querying the requested server: " + response.getStatusText());
                        }
                    } catch(Exception e) {
                        callback.onFailure(new Throwable(e.getMessage()));
                    }
                }

            });
        } catch (RequestException e) {
            callback.onFailure(new Throwable("Couldn't retrieve Data"));
        }
    }

    private static Url selectSuitableUrl(List<Url> urls) {
        Url url = null;
        for(String preferredFormat : supportedFormats) {
            url = ListUtil.findValue(urls, new ListUtil.CheckValue<Url>() {
                @Override
                public boolean isValue(Url value) {
                    return preferredFormat.equalsIgnoreCase(value.getType().toLowerCase());
                }
            });
            if(url != null) {
                break;
            }
        }
        return url;
    }

    private static SearchResponse parseSearchResponse(String response, String selectedFormat) throws Exception {
        switch (selectedFormat) {
            case "application/json": {
                return parseGeojsonResults(response);
            }
            case "application/atom+xml": {
                return parseAtomXMLResults(response);
            }
        }
        return null;
    }

    public static Parameter getSupportedParameterType(List<Parameter> supportedParameters, String namespace, String type) {
        return ListUtil.findValue(supportedParameters, new ListUtil.CheckValue<Parameter>() {
            @Override
            public boolean isValue(Parameter value) {
                return value.getType().equalsIgnoreCase(type);
            }
        });
    }

    public static void getDescription(String requestURL, AsyncCallback<OpenSearchDescription> callback) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, maybeProxyRequest(requestURL));
        try {
            builder.sendRequest(null, new RequestCallback() {

                public void onError(Request request, Throwable exception) {
                    callback.onFailure(new Throwable("Couldn't retrieve Data"));
                }

                public void onResponseReceived(Request request, Response response) {
                    //Window.alert(response.getStatusCode() + " " + response.getStatusText());
                    try {
                        if (200 <= response.getStatusCode() && response.getStatusCode() < 300) {
                            // parse response
                            OpenSearchDescription openSearchDescription = new OpenSearchDescription();
                            Document document = XMLParser.parse(response.getText());
                            Element element = document.getDocumentElement();
                            openSearchDescription.setShortName(getElementValue(element, "ShortName"));
                            openSearchDescription.setDescription(getElementValue(element, "Description"));
                            NodeList urlNodes = element.getElementsByTagName("Url");
                            List<Url> urls = new ArrayList<Url>();
                            for(int index = 0; index < urlNodes.getLength(); index++) {
                                Node urlNode = urlNodes.item(index);
                                Url url = parseUrlNode(urlNode);
                                // only add the supported ones
                                if(supportedFormats.contains(url.getType())) {
                                    urls.add(url);
                                }
                            }
                            openSearchDescription.setUrl(urls);
                            callback.onSuccess(openSearchDescription);
                        } else {
                            throw new Exception("Error querying the requested server: " + response.getStatusText());
                        }
                    } catch(Exception e) {
                        callback.onFailure(new Throwable(e.getMessage()));
                    }
                }

            });
        } catch (RequestException e) {
            callback.onFailure(new Throwable("Couldn't retrieve Data"));
        }
    }

    private static Url parseUrlNode(Node urlNode) {
        Url url = new Url();
        url.setTemplate(((Element) urlNode).getAttribute("template"));
        url.setType(((Element) urlNode).getAttribute("type"));
        String pageOffset = ((Element) urlNode).getAttribute("pageOffset");
        url.setPageOffset(pageOffset == null ? 1 : Integer.parseInt(pageOffset));
        String indexOffset = ((Element) urlNode).getAttribute("indexOffset");
        url.setIndexOffset(indexOffset == null ? 1 : Integer.parseInt(indexOffset));
        url.setParameters(getSupportedParameters(url.getTemplate()));
        // look for extra information on parameters
        NodeList parameterNodes = ((Element) urlNode).getElementsByTagName("Parameter");
        if(parameterNodes != null && parameterNodes.getLength() > 0) {
            for(int parameterIndex = 0; parameterIndex < parameterNodes.getLength(); parameterIndex++) {
                Node parameterNode = parameterNodes.item(parameterIndex);
                String parameterName = ((Element) parameterNode).getAttribute("name");
                // look for matching parameter
                Parameter parameter = ListUtil.findValue(url.getParameters(), new ListUtil.CheckValue<Parameter>() {
                    @Override
                    public boolean isValue(Parameter value) {
                        return value.getName().contentEquals(parameterName);
                    }
                });
                if(parameter != null) {
                    // look for additional information
                    String title = ((Element) parameterNode).getAttribute("title");
                    parameter.setTitle(title);
                    String pattern = ((Element) parameterNode).getAttribute("pattern");
                    if(pattern != null) {
                        parameter.setFieldType("string");
                        parameter.setPattern(pattern);
                    }
                    NodeList optionNodes = ((Element) urlNode).getElementsByTagName("Option");
                    if (optionNodes != null && optionNodes.getLength() > 0) {
                        //String optionName =
                    }
                }
            }
        }
        return url;
    }

    public static List<Parameter> getSupportedParameters(String urlTemplate) {
        List<Parameter> parameters = new ArrayList<Parameter>();
        String queryString = urlTemplate.substring(urlTemplate.indexOf("?") + 1);
        for(String parameterDescription : URL.decodeQueryString(queryString).split("&")) {
            String parameterName = parameterDescription.split("=")[0];
            String parameterType = parameterDescription.split("=")[1];
            // remove brackets
            parameterType = parameterType.substring(1, parameterType.length() - 1);
            Parameter parameter = new Parameter();
            parameter.setName(parameterName);
            parameter.setOptional(parameterType.endsWith("?"));
            if(parameter.isOptional()) {
                parameterType = parameterType.substring(0, parameterType.length() - 1);
            }
            if(parameterType.contains(":")) {
                parameter.setNamespace(parameterType.split(":")[0]);
                parameter.setType(parameterType.split(":")[1]);
            } else {
                parameter.setType(parameterType);
            }
            parameter.setReserved(reservedParameters.contains(parameterType));
            parameters.add(parameter);
        }
        return parameters;
    }

    private static String maybeProxyRequest(String requestURL) {
        String hostPageUrl = Window.Location.getHostName();
        boolean sameDomain = requestURL.startsWith(hostPageUrl) || requestURL.contains("//" + hostPageUrl);
        // call the getCapabilities via a proxy if the domain is different
        if (!sameDomain) {
            return GWT.getHostPageBaseURL() + "proxy?url=" + URL.encode(requestURL);
        }
        return requestURL;
    }

    private static String getElementValue(Element element, String shortName) {
        if(element == null) {
            return null;
        }
        NodeList elements = element.getElementsByTagName(shortName);
        if(elements == null || elements.getLength() == 0) {
            return null;
        }
        return XMLUtil.getNodeValue(elements.item(0));
    }

    static public SearchResponse parseGeojsonResults(String geojson) {
        SearchResponse searchResponse = new SearchResponse();
        JSONObject jsonObject = JSONParser.parseLenient(geojson).isObject();
        if(jsonObject.get("type").isString().stringValue().equalsIgnoreCase("FeatureCollection")) {
            JSONObject responsepropertiesJSON = jsonObject.get("properties").isObject();
            searchResponse.setTotalRecords((int) responsepropertiesJSON.get("totalResults").isNumber().doubleValue());
            searchResponse.setStart((int) responsepropertiesJSON.get("startIndex").isNumber().doubleValue());
            searchResponse.setLimit((int) responsepropertiesJSON.get("itemsPerPage").isNumber().doubleValue());
            List<Record> records = new ArrayList<Record>();
            JSONArray featuresJSON = jsonObject.get("features").isArray();
            for(int index = 0; index < featuresJSON.size(); index++) {
                JSONObject featureJSON = featuresJSON.get(index).isObject();
                Record record = new Record();
                try {
                    if (featureJSON.get("type").isString().stringValue().equalsIgnoreCase("Feature")) {
                        if(featureJSON.containsKey("id")) {
                            record.setId(featureJSON.get("id").toString());
                        }
                        record.setTitle("Record " + record.getId());
                        JSONObject geometryJSON = featureJSON.get("geometry").isObject();
                        JSONArray coordinates = geometryJSON.get("coordinates").isArray();
                        switch (geometryJSON.get("type").isString().stringValue()) {
                            case "Point":
                                record.setGeometryWKT("POINT(" + convertCoordinate(coordinates) + ")");
                                break;
                            case "Polygon":
                                record.setGeometryWKT("POLYGON((" + convertCoordinates(coordinates.get(0).isArray()) + "))");
                                break;
                            case "MultiPolygon":
                                record.setGeometryWKT("MULTIPOLYGON(" + convertMultipleCoordinates(coordinates.isArray()) + ")");
                                break;
                            case "LineString":
                                record.setGeometryWKT("LINESTRING(" + convertCoordinates(coordinates.get(0).isArray()) + ")");
                                break;
                            // TODO - implement the rest
                            // skip record if no geometry available?
                            default:
                        }
                        JSONObject propertiesJSON = featureJSON.get("properties").isObject();
                        HashMap<String, String> properties = new HashMap<String, String>();
                        for (String propertyName : propertiesJSON.keySet()) {
                            JSONValue property = propertiesJSON.get(propertyName);
                            if(property.isString() != null) {
                                properties.put(propertyName, property.isString().stringValue());
                            }
                        }
                        record.setProperties(properties);
                        String htmlContent = "";
                        for(String propertyName : properties.keySet()) {
                            htmlContent += addProperty(propertyName, properties.get(propertyName));
                        }
                        record.setContent(htmlContent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                records.add(record);
            }
            searchResponse.setRecords(records);
        }
        return searchResponse;
    }

    private static SearchResponse parseAtomXMLResults(String response) throws Exception {
        SearchResponse searchResponse = new SearchResponse();
        Document document = XMLParser.parse(response);
        Element feedNode = document.getDocumentElement();
        String totalResults = XMLUtil.getUniqueNodeValue(feedNode, "totalResults");
        if(totalResults != null) {
            searchResponse.setTotalRecords(Integer.parseInt(totalResults));
        }
        String start = XMLUtil.getUniqueNodeValue(feedNode, "startIndex");
        if(start != null) {
            searchResponse.setStart(Integer.parseInt(start));
        }
        String limit = XMLUtil.getUniqueNodeValue(feedNode, "itemsPerPage");
        if(limit != null) {
            searchResponse.setLimit(Integer.parseInt(limit));
        }
        List<Record> records = new ArrayList<Record>();
        List<Node> entryNodes = XMLUtil.getNodes(feedNode, "entry");
        for(Node entryNode : entryNodes) {
            Record record = new Record();
            record.setTitle(XMLUtil.getUniqueNodeValue(entryNode, "title"));
            record.setId(XMLUtil.getUniqueNodeValue(entryNode, "id"));
            // look for geometry
            Node whereNode = XMLUtil.getUniqueNode(entryNode, "where");
            boolean gmlStyle = whereNode != null;
            if(gmlStyle) {
                Node geometry = XMLUtil.getUniqueNode(whereNode, "Polygon");
                if (geometry != null) {
                    NodeList posNodes = ((Element) geometry).getElementsByTagName("posList");
                    record.setGeometryWKT("POLYGON((" + parseGeorssCoordinates(XMLUtil.getNodeValue(posNodes.item(0))) + "))");
                } else {
                    geometry = XMLUtil.getUniqueNode(whereNode, "LineString");
                    if (geometry != null) {
                        record.setGeometryWKT("LINESTRING(" + parseGeorssCoordinates(XMLUtil.getNodeValue(((Element) geometry).getElementsByTagName("postList").item(0))) + ")");
                    } else {
                        geometry = XMLUtil.getUniqueNode(whereNode, "Point");
                        if (geometry != null) {
                            record.setGeometryWKT("POINT(" + parseGeorssCoordinates(XMLUtil.getNodeValue(((Element) geometry).getElementsByTagName("pos").item(0))) + ")");
                        }
                    }
                }
            } else {
                String geometry = XMLUtil.getUniqueNodeValue(entryNode, "polygon");
                if (geometry != null) {
                    record.setGeometryWKT("POLYGON((" + parseGeorssCoordinates(geometry) + "))");
                } else {
                    geometry = XMLUtil.getUniqueNodeValue(entryNode, "point");
                    if (geometry != null) {
                        record.setGeometryWKT("POINT(" + parseGeorssCoordinates(geometry) + ")");
                    } else {
                        geometry = XMLUtil.getUniqueNodeValue(entryNode, "line");
                        if (geometry != null) {
                            record.setGeometryWKT("LINESTRING(" + parseGeorssCoordinates(geometry) + ")");
                        }
                    }
                }
            }
            record.setContent(XMLUtil.getUniqueNodeValue(entryNode, "content"));
            records.add(record);
        }
        searchResponse.setRecords(records);
        return searchResponse;
    }

    private static String parseGeorssCoordinates(String coordinates) {
        List<String> wktCoordinates = new ArrayList<String>();
        String[] coordinateValues = coordinates.split("\\s+");
        for(int index = 0; index < coordinateValues.length; index += 2) {
            wktCoordinates.add(coordinateValues[index + 1] + " " + coordinateValues[index]);
        }
        return StringUtils.join(wktCoordinates, ",");
    }

    private static String convertMultipleCoordinates(JSONArray coordinates) {
        String wktCoordinates = "";
        for(int index = 0; index < coordinates.size(); index++) {
            JSONArray polygon = coordinates.get(index).isArray();
            wktCoordinates += "((" + convertCoordinates(polygon.get(0).isArray()) + ")),";
        }
        return wktCoordinates.substring(0, wktCoordinates.length() - 1);
    }

    private static String convertCoordinates(JSONArray coordinates) {
        String wktCoordinates = "";
        for(int index = 0; index < coordinates.size(); index++) {
            wktCoordinates += convertCoordinate(coordinates.get(index).isArray()) + ",";
        }
        return wktCoordinates.substring(0, wktCoordinates.length() - 1);
    }

    private static String convertCoordinate(JSONArray coordinates) {
        return coordinates.get(0).isNumber() + " " + coordinates.get(1).isNumber();
    }

    private static String formatString(String value) {
        return value == null ?  "NA" : value;
    }

    private static String formatNumber(Double value, String unitValue) {
        return value == null || value == -1 ?  "NA" : (format.format(value) + " " + unitValue);
    }

    private static String addProperty(String name, Object value) {
        return "<div style='padding: 5px; white-space: nowrap; text-overflow: ellipsis;'><b>" + name + ": </b>" + (value == null ? "NA" : value.toString()) + "</div>";
    }

}
