package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by thomas on 08/11/2016.
 */
public class AoIWidget extends Composite {

    interface AoIWidgetUiBinder extends UiBinder<Widget, AoIWidget> {
    }

    private static AoIWidgetUiBinder ourUiBinder = GWT.create(AoIWidgetUiBinder.class);

    @UiField
    MaterialIcon image;
    @UiField
    MaterialButton action;
    @UiField
    MaterialLabel name;

    public AoIWidget(AoIDTO aoIDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        name.setText(aoIDTO.getName());
        IconType iconType = IconType.HELP_OUTLINE;
        image.setIconType(iconType);
    }

}