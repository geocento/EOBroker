package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by thomas on 08/11/2016.
 */
public class AoIWidget extends Composite {

    interface AoIWidgetUiBinder extends UiBinder<Widget, AoIWidget> {
    }

    private static AoIWidgetUiBinder ourUiBinder = GWT.create(AoIWidgetUiBinder.class);

    @UiField
    MaterialButton select;
    @UiField
    MaterialLabel name;

    public AoIWidget(AoIDTO aoIDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        String name = aoIDTO.getName();
        this.name.setText(name == null || name.length() == 0 ? "No name" : name);
    }

    public HasClickHandlers getSelect() {
        return select;
    }

}