package com.geocento.webapps.eobroker.common.client.utils.opensearch;

import com.geocento.webapps.eobroker.common.client.utils.JSON;
import com.geocento.webapps.eobroker.common.client.utils.geojson.object.Feature;
import com.geocento.webapps.eobroker.common.client.utils.geojson.object.FeatureCollection;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.google.gwt.http.client.*;
import com.google.gwt.i18n.client.DateTimeFormat;
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

    static private DateTimeFormat fmt = DateTimeFormat.getFormat("YYYY-MM-DDTHH:mm:ssZ");

    public static HashMap<String, String> getSupportedParameters(String urlTemplate) {
        HashMap<String, String> parameters = new HashMap<String, String>();
        for(String parameterDescription : URL.decodeQueryString(urlTemplate).split("&")) {
            String parameterName = parameterDescription.split("=")[0];
            String parameterType = parameterDescription.split("=")[1];
            // remove brackets
            parameterType = parameterType.substring(1, parameterType.length() - 1);
            parameters.put(parameterName, parameterType);
        }
        return parameters;
    }

    public static void getRecords(OpenSearchDescription openSearchDescription, String wktGeometry, Date startDate, Date stopDate, String query, AsyncCallback<List<Feature>> callback) throws Exception {
        // find the suitable URL
        List<String> preferredFormats = ListUtil.toListArg("application/json", "application/rss+xml");
        Url url = ListUtil.findValue(openSearchDescription.getUrl(), new ListUtil.CheckValue<Url>() {
            @Override
            public boolean isValue(Url value) {
                return preferredFormats.contains(value.getType().toLowerCase());
            }
        });
        if(url == null) {
            throw new Exception("No supported formats available in url templates");
        }
        String selectedFormat = url.getType();
        String datasetURL = url.getTemplate();
        // build request
        String requestURL;
        // get basic URL
        requestURL = URL.decodePathSegment(datasetURL);
        HashMap<String, String> supportedParameters = getSupportedParameters(datasetURL);
        HashMap<String, String> requestedParameters = new HashMap<String, String>();
        String startParameter = getSupportedParameterType(supportedParameters, "time:start");
        if(startParameter != null) {
            requestedParameters.put(startParameter, fmt.format(startDate));
        }
        String stopParameter = getSupportedParameterType(supportedParameters, "time:end");
        if(stopParameter != null) {
            requestedParameters.put(stopParameter, fmt.format(stopDate));
        }
        if(supportedParameters.containsKey("geometry")) {
            requestedParameters.put("geometry", wktGeometry);
        } else if(supportedParameters.containsKey("bbox")) {
            Extent extent = AoIUtil.getExtent(AoIUtil.fromWKT(wktGeometry));
            // from http://www.opensearch.org/Specifications/OpenSearch/Extensions/Geo/1.0/Draft_2
            // geo:box ~ west, south, east, north
            requestedParameters.put("bbox", extent.getWest() + "," + extent.getSouth() + "," + extent.getEast() + "," + extent.getNorth());
        }
        // finally add the format

        List<String> requestedValues = new ArrayList<String>();
        for(String parameterName : requestedParameters.keySet()) {
            requestedValues.add(parameterName + "=" + requestedParameters.get(parameterName));
        }
        requestURL += "?" + URL.encodeQueryString(com.geocento.webapps.eobroker.common.client.utils.StringUtils.join(requestedValues, "&"));
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, maybeProxyRequest(requestURL));
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

    private static List<Feature> parseSearchResponse(String text, String selectedFormat) {
        switch (selectedFormat) {
            case "application/json":
                FeatureCollection featureCollection = (FeatureCollection) JSON.parse(text);
                for(Feature feature : featureCollection.features) {
                    Window.alert(feature.type + " " + feature.geometry.type);
                }
        }
        return null;
    }

    public static String getSupportedParameterType(HashMap<String, String> supportedParameters, String namespace) {
        for(String parameterName : supportedParameters.keySet()) {
            if(supportedParameters.get(parameterName).equalsIgnoreCase(namespace)) {
                return parameterName;
            }
        }
        return null;
    }

    public static void getDescription(String requestURL, AsyncCallback<OpenSearchDescription> callback) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, maybeProxyRequest(requestURL));
        try {
            builder.sendRequest(null, new RequestCallback() {

                public void onError(Request request, Throwable exception) {
                    callback.onFailure(new Throwable("Couldn't retrieve Data"));
                }

                public void onResponseReceived(Request request, Response response) {
                    Window.alert(response.getStatusCode() + " " + response.getStatusText());
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
                                Url url = new Url();
                                url.setTemplate(((Element) urlNode).getAttribute("template"));
                                url.setType(((Element) urlNode).getAttribute("type"));
                                urls.add(url);
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

    private static String maybeProxyRequest(String requestURL) {
        String hostPageUrl = Window.Location.getHostName();
        boolean sameDomain = requestURL.startsWith(hostPageUrl) || requestURL.contains("//" + hostPageUrl);
        // call the getCapabilities via a proxy if the domain is different
        if (!sameDomain) {
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


}
