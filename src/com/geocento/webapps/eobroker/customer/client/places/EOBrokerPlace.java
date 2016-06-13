package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.Place;

public class EOBrokerPlace extends Place {

    protected String token;

    public EOBrokerPlace() {
	}

    public EOBrokerPlace(String token) {
        this.token = token;
    }

    public String getToken() {
		return token;
	}

}
