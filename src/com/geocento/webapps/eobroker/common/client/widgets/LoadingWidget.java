package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by thomas on 10/10/2016.
 */
public class LoadingWidget extends Composite {

    interface LoadingWidgetUiBinder extends UiBinder<Widget, LoadingWidget> {
    }

    private static LoadingWidgetUiBinder ourUiBinder = GWT.create(LoadingWidgetUiBinder.class);

    @UiField
    MaterialLabel text;

    public LoadingWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
        text.setHeight("100%");
        text.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
    }

    public LoadingWidget(String text) {
        this();
        setText(text);
    }

    public void setText(String text) {
        this.text.setText(text);
    }
}