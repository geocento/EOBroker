package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.Place;

public class PlaceHistoryHelper {

	private static AppPlaceHistoryMapper placeHistoryMapper = GWT.create(AppPlaceHistoryMapper.class);
	
	public static String convertPlace(Place place) {
		return placeHistoryMapper.getToken(place);
	}

}
