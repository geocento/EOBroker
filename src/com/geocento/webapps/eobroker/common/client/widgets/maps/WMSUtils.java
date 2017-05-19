package com.geocento.webapps.eobroker.common.client.widgets.maps;

import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Created by thomas on 18/05/2017.
 */
public class WMSUtils {

    public static void getFeatureInfo(String baseUrl, String layers, String version, String styles, Extent bbox, int width, int height, int x, int y, final AsyncCallback<String> callback) throws Exception {
        boolean version13 = version.startsWith("1.3");
        callOWSServer(baseUrl, "WMS", "GetFeatureInfo",
                        "version=" + version +
                        "&layers=" + layers +
                        "&styles=" + styles +
                        (version13 ? "&crs=" : "&srs=") + "EPSG:4326" +
                        "&bbox=" + toWMSBBox(bbox) +
                        "&width=" + width +
                        "&height=" + height +
                        "&query_layers=" + layers +
                        "&" + (version13 ? "i" : "x") + "=" + x +
                        "&" + (version13 ? "j" : "y") + "=" + y +
                        "&info_format=text/html"
                , new AsyncCallback<String>() {

                    @Override
                    public void onSuccess(String response) {
                        callback.onSuccess(response);
/*
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
*/
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        callback.onFailure(throwable);
                    }
                });
    }

    static public void callOWSServer(String baseAddress, String service, String request, String parameters, final AsyncCallback<String> callBack) {
        String hostPageUrl = Window.Location.getHostName();
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

    private static String toWMSBBox(Extent bbox) throws Exception {
        return bbox.getWest() + "," + bbox.getSouth() + "," + bbox.getEast() + "," + bbox.getNorth();
    }

}
