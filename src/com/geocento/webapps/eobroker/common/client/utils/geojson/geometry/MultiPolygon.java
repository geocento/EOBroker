package com.geocento.webapps.eobroker.common.client.utils.geojson.geometry;

import org.geojson.geometry.Geometry;

import java.util.List;

public class MultiPolygon extends Geometry {

	public List<List<List<double[]>>> coordinates;

	public MultiPolygon() {
		super("MultiPolygon");
	}

}
