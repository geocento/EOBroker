package com.geocento.webapps.eobroker.customer.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 05/09/2016.
 */
public class FeasibilityHeader extends Composite {

    interface FeasibilityHeaderUiBinder extends UiBinder<HTMLPanel, FeasibilityHeader> {
    }

    private static FeasibilityHeaderUiBinder ourUiBinder = GWT.create(FeasibilityHeaderUiBinder.class);

    @UiField
    MaterialLink indicator;
    @UiField
    MaterialLink header;

    public FeasibilityHeader() {

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    public void setHeaderText(String text) {
        header.setText(text);
    }

    public void setHeaderIcon(IconType iconType) {
        header.setIconType(iconType);
    }

    public void setIndicatorText(String text) {
        indicator.setText(text);
    }

    public void setIndicatorColor(Color color) {
        indicator.setTextColor(color);
    }

    public com.google.gwt.event.shared.HandlerRegistration addClickHandler(ClickHandler clickHandler) {
        return indicator.addClickHandler(clickHandler);
    }

}