package com.geocento.webapps.eobroker.common.client.utils.geojson.object;

import com.geocento.webapps.eobroker.common.client.utils.geojson.geometry.Geometry;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import java.util.Map;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public class Feature {

	public String type;
	public Map<String, Object> properties;
	public Geometry geometry;

	public Feature() {
	}

}
