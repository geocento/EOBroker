package com.geocento.webapps.eobroker.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import gwt.material.design.client.ui.MaterialValueBox;

/**
 * Created by thomas on 03/06/2016.
 */
public class SuggestionBox extends Composite {
    interface SuggestionBoxUiBinder extends UiBinder<MaterialValueBox, SuggestionBox> {
    }

    private static SuggestionBoxUiBinder ourUiBinder = GWT.create(SuggestionBoxUiBinder.class);

    public SuggestionBox() {

        initWidget(ourUiBinder.createAndBindUi(this));

    }
}