package com.geocento.webapps.eobroker.common.client.widgets.maps;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.google.gwt.http.client.*;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 18/05/2017.
 */
public class WMSUtils {

    public static void getFeatureInfo(String baseUrl, String layers, String version, String styles, String srs, Extent bbox, int width, int height, int x, int y, final AsyncCallback<List<Feature>> callback) throws Exception {
        callOWSServer(baseUrl, "WMS", "GetFeatureInfo",
                "version=" + version +
                        "&layers=" + layers +
                        "&styles=" + styles +
                        (version.startsWith("1.3") ? "&crs=" : "&srs=") + srs +
                        "&bbox=" + toWMSBBox(bbox) +
                        "&width=" + width +
                        "&height=" + height +
                        "&query_layers=" + layers +
                        "&x=" + x +
                        "&y=" + y +
                        "&info_format=application/json"
                , new AsyncCallback<String>() {

                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONValue jsonResponse = JSONParser.parseLenient(response);
                            if(jsonResponse.isObject() != null && jsonResponse.isObject().containsKey("features")) {
                                callback.onSuccess(convertToFeatures(jsonResponse.isObject().get("features").isArray()));
                            } else {
                                throw new Exception("Problem parsing response");
                            }
                        } catch (Exception e) {
                            onFailure(e);
                        }
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onFailure(throwable);
                    }
                });
    }

    static public void callOWSServer(String baseAddress, String service, String request, String parameters, final AsyncCallback<String> callBack) {
        String hostPageUrl = Window.Location.getHostName();
        boolean sameDomain = baseAddress.startsWith(hostPageUrl) || baseAddress.contains("//" + hostPageUrl);
        // call the getCapabilities via a proxy if the domain is different
        if(!sameDomain) {
            proxyService.proxyOWSRequest(baseAddress, service, request, parameters, callBack);
        } else {
            RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, baseAddress + "service=" + service + "&request=" + request + "&" + parameters);
            try {
                Request owsRequest = builder.sendRequest(null, new RequestCallback() {
                    public void onError(Request request, Throwable exception) {
                        callBack.onFailure(new Throwable("Couldn't retrieve Data"));
                    }

                    public void onResponseReceived(Request request, Response response) {
                        try {
                            if (200 == response.getStatusCode()) {
                                callBack.onSuccess(response.getText());
                            } else {
                                throw new Exception("Error querying the requested server: " + response.getStatusText());
                            }
                        } catch(Exception e) {
                            callBack.onFailure(new Throwable(e.getMessage()));
                        }
                    }

                });
            } catch (RequestException e) {
                callBack.onFailure(new Throwable("Couldn't retrieve Data"));
            }
        }
    }

    static private List<Feature> convertToFeatures(JSONArray featuresArray) {
        List<Feature> features = new ArrayList<Feature>();
        for (int index = 0; index < featuresArray.size(); index++) {
            JSONObject feature = featuresArray.get(index).isObject();
            if (feature != null && feature.get("type") != null && feature.get("type").isString() != null && feature.get("type").isString().stringValue().equalsIgnoreCase("Feature")) {
                String name = feature.get("id").toString();
                String wktGeometry = "";
                JSONObject geometry = feature.get("geometry").isObject();
                String geometryType = geometry.get("type").isString().stringValue().toLowerCase();
                // we don't support geometry collections for features
                if(geometryType.contentEquals("geometrycollection")) {
                    continue;
                }
                JSONArray coordinates = geometry.get("coordinates").isArray();
                if(geometryType.startsWith("multi")) {
                    // only take the first array in array of arrays
                    coordinates = coordinates.get(0).isArray();
                    geometryType = geometryType.replace("multi", "");
                }
                // convert coordinates from geojson to wkt
                String coordinatesString = geometryType.equalsIgnoreCase("polygon") ? coordinates.get(0).toString() : coordinates.toString();
                String wktCoordinates = coordinatesString.replace("\"", "").replace(",", " ").replace("[", "").replace("]", ",");
                wktCoordinates = StringUtils.stripEnd(wktCoordinates, ",");
                switch (geometryType) {
                    case "polygon":
                        wktGeometry = "POLYGON((" + wktCoordinates + "))";
                        break;
                    case "point":
                        wktGeometry = "POINT(" + wktCoordinates + ")";
                        break;
                    case "linestring":
                        wktGeometry = "LINESTRING(" + wktCoordinates + ")";
                        break;
                    // unsupported geometry type
                    default:
                        continue;
                }
                HashMap<String, String> propertiesMap = new HashMap<String, String>();
                JSONObject properties = feature.get("properties") != null && feature.get("properties").isObject() != null ? feature.get("properties").isObject() : null;
                for (String propertyName : properties.keySet()) {
                    propertiesMap.put(propertyName, properties.get(propertyName).toString());
                }
                features.add(new Feature(name, wktGeometry, propertiesMap));
            }
        }
        return features;
    }

}
