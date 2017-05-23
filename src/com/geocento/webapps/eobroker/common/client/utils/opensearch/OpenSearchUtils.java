package com.geocento.webapps.eobroker.common.client.utils.opensearch;

import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
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

    static List<String> supportedFormats = ListUtil.toListArg("application/json", "application/rss+xml", "application/atom+xml");

    static private List<String> reservedParameters = ListUtil.toList(
            new String[] {"searchTerms", "count", "startIndex", "startPage", "language"});

    public static void getRecords(OpenSearchDescription openSearchDescription, String wktGeometry, Date startDate, Date stopDate, String query, AsyncCallback<List<Record>> callback) throws Exception {
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
        // finally add the format

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
                            callback.onSuccess(parseSearchResponse(response.getText(), selectedFormat));
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

    private static List<Record> parseSearchResponse(String response, String selectedFormat) {
        switch (selectedFormat) {
            case "application/json":
                List<Record> records = parseResults(response);
                return records;
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
        for(String parameterDescription : URL.decodeQueryString(urlTemplate).split("&")) {
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
        if (false) {//!sameDomain) {
            return "/proxy?url=" + URL.encode(requestURL);
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
        return getNodeValue(elements.item(0));
    }

    public static String getNodeValue(Node node) {
        if(node == null) {
            return null;
        }
        if(node.getFirstChild() instanceof Text) {
            return ((Text) node.getFirstChild()).getData();
        } else {
            return "Unknown";
        }
    }

    static public List<Record> parseResults(String geojson) {
        List<Record> records = new ArrayList<Record>();
        JSONObject jsonObject = JSONParser.parseLenient(geojson).isObject();
        if(jsonObject.get("type").isString().stringValue().equalsIgnoreCase("FeatureCollection")) {
            JSONArray featuresJSON = jsonObject.get("features").isArray();
            for(int index = 0; index < featuresJSON.size(); index++) {
                try {
                    JSONObject featureJSON = featuresJSON.get(index).isObject();
                    if (featureJSON.get("type").isString().stringValue().equalsIgnoreCase("Feature")) {
                        Record record = new Record();
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
                            // TODO - implement the rest
                            // skip record if no geometry available?
                            default:
                                continue;
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
                        records.add(record);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return records;
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

}
