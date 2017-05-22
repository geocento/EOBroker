package com.geocento.webapps.eobroker.common.client.utils.geojson.object;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

import java.util.List;

@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public class FeatureCollection {

	public String type;
    public List<Feature> features;

    public FeatureCollection() {
    }
}
