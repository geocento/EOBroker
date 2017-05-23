package com.geocento.webapps.eobroker.common.client.utils.geojson.geometry;

import org.geojson.geometry.Geometry;

import java.util.List;

public class GeometryCollection extends Geometry {

	public List<Geometry> geometries;

	public GeometryCollection() {
        super("geometryCollection");
	}

}
