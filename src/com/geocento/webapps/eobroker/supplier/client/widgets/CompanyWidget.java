package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by thomas on 09/06/2016.
 */
public class CompanyWidget extends Composite {

    interface CompanyWidgetUiBinder extends UiBinder<Widget, CompanyWidget> {
    }

    private static CompanyWidgetUiBinder ourUiBinder = GWT.create(CompanyWidgetUiBinder.class);

    @UiField
    MaterialImage image;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;

    public CompanyWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setCompany(CompanyDTO companyDTO) {
        image.setUrl(companyDTO.getIconURL());
        title.setText(companyDTO.getName());
        description.setText(companyDTO.getDescription());
    }
}