package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialTextBox;

/**
 * Created by thomas on 14/03/2017.
 */
public class AwardWidget extends Composite {

    interface AwardWidgetUiBinder extends UiBinder<Widget, AwardWidget> {
    }

    private static AwardWidgetUiBinder ourUiBinder = GWT.create(AwardWidgetUiBinder.class);

    @UiField
    MaterialTextBox value;
    @UiField
    MaterialButton remove;

    public AwardWidget() {

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    public HasText getText() {
        return value;
    }

    public HasClickHandlers getRemove() {
        return remove;
    }
}