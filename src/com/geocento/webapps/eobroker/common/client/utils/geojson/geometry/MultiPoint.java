package com.geocento.webapps.eobroker.common.client.utils.geojson.geometry;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.geojson.geometry.Geometry;

import java.util.List;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public class MultiPoint extends Geometry {

	public List<double[]> coordinates;

	public MultiPoint() {
		super("MultiPoint");
	}

}
