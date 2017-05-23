package com.geocento.webapps.eobroker.common.client.utils.geojson.geometry;

import org.geojson.geometry.Geometry;

import java.util.List;

public class MultiLineString extends Geometry {

	public List<List<double[]>> coordinates;

	public MultiLineString() {
		super("MultiLineString");
	}

}
