package com.geocento.webapps.eobroker.common.client.utils.geojson.geometry;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.geojson.geometry.Geometry;

import java.util.List;

@JsonTypeName("Polygon")
public class Polygon extends Geometry {

	public List<List<double[]>> coordinates;

	public Polygon() {
		super("Polygon");
	}

}
