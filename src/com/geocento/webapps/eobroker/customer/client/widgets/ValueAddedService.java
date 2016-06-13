package com.geocento.webapps.eobroker.customer.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * Created by thomas on 27/05/2016.
 */
public class ValueAddedService extends Composite {

    interface ValueAddedServiceUiBinder extends UiBinder<HTMLPanel, ValueAddedService> {
    }

    private static ValueAddedServiceUiBinder ourUiBinder = GWT.create(ValueAddedServiceUiBinder.class);

    public ValueAddedService() {
        initWidget(ourUiBinder.createAndBindUi(this));

    }
}