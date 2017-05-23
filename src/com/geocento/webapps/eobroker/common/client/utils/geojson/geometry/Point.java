package com.geocento.webapps.eobroker.common.client.utils.geojson.geometry;

import org.geojson.geometry.Geometry;

public class Point extends Geometry {

	private double[] coordinates;

	public Point() {
		super("Point");
	}

}
