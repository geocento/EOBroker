package com.geocento.webapps.eobroker.server.imageapi;

import com.geocento.webapps.eobroker.shared.imageapi.ImageProductDTO;
import com.geocento.webapps.eobroker.shared.imageapi.SearchRequest;
import com.geocento.webapps.eobroker.shared.imageapi.SearchResponse;
import com.google.gson.*;
import org.apache.log4j.Logger;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 04/03/2016.
 */
public class EIAPIUtil {

    static private Logger logger = Logger.getLogger(EIAPIUtil.class);

    static GsonBuilder builder = new GsonBuilder();
    static {
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
    }

    private static String token = "";

    static public List<ImageProductDTO> queryProducts(SearchRequest searchRequest) throws Exception {
        String response = sendAPIRequest("/search/catalogue", new Gson().toJson(searchRequest));
        return builder.create().fromJson(response, SearchResponse.class).getImageProductDTOs();
    }

    private static String sendAPIRequest(String endPoint, String payload) throws Exception {

        logger.debug("Calling EI API with:");
        logger.debug("End point = " + endPoint);
        logger.debug("Payload = " + payload);

        String response = "";

        HttpURLConnection connection = null;
        InputStream connectionIstream = null;

        // add some debug log information
        try {
            //Send request
            URL targetURL = new URL("192.168.2.100:8888" + endPoint);
            connection = (HttpURLConnection) targetURL.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Token " + token);
            connection.setRequestProperty("Accept", "application/json");
            //connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", payload.length() + "");
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(payload);
            wr.flush();
            wr.close();
            //Get Response
            connectionIstream = connection.getInputStream();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("Error submitting query to API");
            }
            int bufferSize = 4*4*1024;//same buffer size as in Jetty utils (2*8192)
            byte[] buffer = new byte[bufferSize];
            int read = 0;
            while ((read = connectionIstream.read(buffer)) != -1) {
                response += new String(buffer, 0, read);
            }
        } catch (Exception e){
            throw e;
        } finally {
            if(connectionIstream != null) { connectionIstream.close(); }
        }
        return response;
    }

}
