package com.geocento.webapps.eobroker.common.client.utils.geojson;

import com.geocento.webapps.eobroker.common.client.utils.geojson.object.Feature;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import org.geojson.object.FeatureCollection;

import java.util.ArrayList;

/**
 * Created by thomas on 23/05/2017.
 */
public class GeojsonUtil {

    static public FeatureCollection parseGeojson(String geojson) {
        JSONObject jsonObject = JSONParser.parseLenient(geojson).isObject();
        if(jsonObject.get("type").isString().stringValue().equalsIgnoreCase("FeatureCollection")) {
            FeatureCollection featureCollection = new FeatureCollection();
            JSONArray featuresJSON = jsonObject.get("features").isArray();
            ArrayList<Feature> features = new ArrayList<Feature>();
            for(int index = 0; index < features.size(); index++) {
                JSONObject featureJSON = featuresJSON.get(index).isObject();
                if(featureJSON.get("type").isString().stringValue().equalsIgnoreCase("Feature")) {
                    Feature feature = new Feature();
                    JSONObject geometryJSON = featureJSON.get("geometry").isObject();
/*
                    Geometry geometry = null;
                    switch(geometryJSON.get("type").isString().stringValue()) {
                        case "Point":
                            geometry = new Point();
                            break;
                    }
                    feature.setGeometry(featureJSON.get("name").isString().stringValue());
                    feature.setProperties(featureJSON.get("name").isString().stringValue());
*/
                }
            }
        }
        return null;
    }
}
