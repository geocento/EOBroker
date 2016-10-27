package com.geocento.webapps.eobroker.customer.client.utils;

import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.CSWBriefRecord;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.CSWGetRecordsResponse;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.CSWRecordType;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

/**
 * Created by thomas on 10/10/2016.
 */
public class CSWUtils {

    static String requestRecords =
            "<csw:GetRecords xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:ogc=\"http://www.opengis.net/ogc\" service=\"CSW\" version=\"2.0.2\" resultType=\"results\" startPosition=\"1\" maxRecords=\"5\" outputFormat=\"application/xml\" outputSchema=\"http://www.opengis.net/cat/csw/2.0.2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.opengis.net/cat/csw/2.0.2 http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd\" xmlns:gml=\"http://www.opengis.net/gml\">\n" +
            "  <csw:Query typeNames=\"csw:Record\">\n" +
            "    <csw:ElementSetName>brief</csw:ElementSetName>\n" +
            "    <csw:Constraint version=\"1.1.0\">\n" +
            "       $filter\n" +
            "    </csw:Constraint>\n" +
            "  </csw:Query>\n" +
            "</csw:GetRecords>";

    static String bboxFilter =
            "\t        <ogc:BBOX>\n" +
            "\t          <ogc:PropertyName>ows:BoundingBox</ogc:PropertyName>\n" +
            "\t          <gml:Envelope>\n" +
            "\t            <gml:lowerCorner>$lowercorner</gml:lowerCorner>\n" +
            "\t            <gml:upperCorner>$uppercorner</gml:upperCorner>\n" +
            "\t          </gml:Envelope>\n" +
            "\t        </ogc:BBOX>\n";

    static String islikeFilter = "        <ogc:PropertyIsEqualTo>\n" +
            "          <ogc:PropertyName>csw:AnyText</ogc:PropertyName>\n" +
            "          <ogc:Literal>$value</ogc:Literal>\n" +
            "        </ogc:PropertyIsEqualTo>\n";
/*
                    "       <ogc:PropertyIsLike matchCase=\"false\" wildCard=\"%\" singleChar=\"_\" escapeChar=\"\\\">\n" +
                    "        <ogc:PropertyName>dc:title</ogc:PropertyName>\n" +
                    "        <ogc:Literal>$value%</ogc:Literal>\n" +
                    "       </ogc:PropertyIsLike>\n";
*/

    public static void getRecordsResponse(String uri, String keywords, Extent bounds, final AsyncCallback<CSWGetRecordsResponse> callback) {
        RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, uri);
        try {
            Request cswRequest = builder.sendRequest(getRequestData(keywords, bounds), new RequestCallback() {
                @Override
                public void onResponseReceived(Request request, Response response) {
                    if(response.getStatusCode() >= 400) {
                        callback.onFailure(new Exception("Error querying server"));
                        return;
                    }
                    // parse the response
                    CSWGetRecordsResponse records = new CSWGetRecordsResponse();
                    try {
                        Document document = XMLParser.parse(response.getText());
                        NodeList nodes = document.getElementsByTagName("BriefRecord");
                        for(int index = 0; index < nodes.getLength(); index++) {
                            Node node = nodes.item(index);
                            CSWBriefRecord briefRecord = new CSWBriefRecord();
                            Node childNode = node.getFirstChild();
                            briefRecord.setId(childNode.getNodeValue());
                            childNode = childNode.getNextSibling();
                            briefRecord.setTitle(childNode.getNodeValue());
                            childNode = childNode.getNextSibling();
                            briefRecord.setType(CSWRecordType.valueOf(childNode.getNodeValue()));
                            childNode = childNode.getNextSibling();
                            briefRecord.setDescription(childNode.getNodeValue());
                        }
                        callback.onSuccess(records);
                    } catch (Exception e) {
                        callback.onFailure(new Exception("Error parsing response"));
                        return;
                    }

                }

                @Override
                public void onError(Request request, Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    public static String getRequestData(String keywords, Extent bounds) {
        String filter = "";
        if (keywords != null || bounds != null) {
            filter += "<ogc:Filter>\n";
            boolean multiple = keywords != null && bounds != null;
            if (multiple) {
                filter += "<ogc:And>\n";
            }
            if (keywords != null) {
                filter += islikeFilter.replace("$value", keywords);
            }
            if (bounds != null) {
                filter += bboxFilter
                        .replace("$lowercorner", bounds.getSouth() + " " + bounds.getWest())
                        .replace("$uppercorner", bounds.getNorth() + " " + bounds.getEast());
            }
            if (multiple) {
                filter += "</ogc:And>\n";
            }
            filter += "</ogc:Filter>\n";
        }

        return requestRecords.replace("$filter", filter);
    }

}
