package com.geocento.webapps.eobroker.common.client.utils.geojson.geometry;

import org.geojson.geometry.Geometry;

import java.util.List;

public class MultiPoint extends Geometry {

	public List<double[]> coordinates;

	public MultiPoint() {
		super("MultiPoint");
	}

}
