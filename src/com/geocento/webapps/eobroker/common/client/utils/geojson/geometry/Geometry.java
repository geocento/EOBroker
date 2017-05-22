package com.geocento.webapps.eobroker.common.client.utils.geojson.geometry;

public abstract class Geometry {

	public String type;

    protected Geometry() {
    }

    public Geometry(String type) {
		this.type = type;
	}

}
