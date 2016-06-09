package com.geocento.webapps.eobroker.client.services;

import com.google.gwt.core.client.GWT;

/**
 * Created by thomas on 03/06/2016.
 */
public class ServicesUtil {

    public static SearchService searchService = GWT.create(SearchService.class);

}
