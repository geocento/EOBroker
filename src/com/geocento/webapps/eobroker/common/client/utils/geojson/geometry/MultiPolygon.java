package com.geocento.webapps.eobroker.common.client.utils.geojson.geometry;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.geojson.geometry.Geometry;
import org.geojson.geometry.Polygon;

import java.util.ArrayList;
import java.util.List;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public class MultiPolygon extends Geometry {

	public List<List<List<double[]>>> coordinates;

	public MultiPolygon() {
		super(org.geojson.geometry.MultiPolygon.class.getSimpleName());
	}

	public MultiPolygon(List<Polygon> coordinates) {
		this();
		if (coordinates != null) {
			this.coordinates = new ArrayList<>();

			for (Polygon coordinate : coordinates) {
				this.coordinates.add(coordinate.getCoordinates());
			}
		}

	}

	public List<List<List<double[]>>> getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(List<List<List<double[]>>> coordinates) {
		this.coordinates = coordinates;
	}

}
