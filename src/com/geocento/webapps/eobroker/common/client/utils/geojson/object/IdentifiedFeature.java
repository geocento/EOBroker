package com.geocento.webapps.eobroker.common.client.utils.geojson.object;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import org.geojson.object.Feature;

/**
 * Make it possible to construct features that have an "id" identifier. From
 * http://geojson.org/geojson-spec.html: If a feature has a commonly used
 * identifier, that identifier should be included as a member of the feature
 * object with the name "id".
 */
@JsType(isNative=true, namespace= JsPackage.GLOBAL, name="Object")
public class IdentifiedFeature extends Feature {

	public String id;

	public IdentifiedFeature() {
	}

}
