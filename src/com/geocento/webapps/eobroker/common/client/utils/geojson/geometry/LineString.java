package com.geocento.webapps.eobroker.common.client.utils.geojson.geometry;

import org.geojson.geometry.Geometry;

import java.util.List;

public class LineString extends Geometry {

	public List<double[]> coordinates;

	public LineString() {
		super("LineString");
	}

}
