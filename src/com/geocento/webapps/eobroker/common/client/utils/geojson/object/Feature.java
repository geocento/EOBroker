package com.geocento.webapps.eobroker.common.client.utils.geojson.object;

import com.geocento.webapps.eobroker.common.client.utils.geojson.geometry.Geometry;

import java.util.Map;

public class Feature {

	public String type;
	public Map<String, Object> properties;
	public Geometry geometry;

	public Feature() {
	}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }
}
