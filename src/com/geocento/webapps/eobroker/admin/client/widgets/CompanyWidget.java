package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.events.RemoveCompany;
import com.geocento.webapps.eobroker.admin.client.places.CompanyPlace;
import com.geocento.webapps.eobroker.admin.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

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
    @UiField
    MaterialLink edit;
    @UiField
    MaterialLink remove;

    public CompanyWidget(final CompanyDTO companyDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.setUrl(companyDTO.getIconURL() == null || companyDTO.getIconURL().length() == 0 ? "./images/noImage.png" : companyDTO.getIconURL());
        title.setText(companyDTO.getName());
        description.setText(companyDTO.getDescription());
        edit.setHref("#" + PlaceHistoryHelper.convertPlace(new CompanyPlace(companyDTO.getId())));
        remove.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(Window.confirm("Are you sure you want to remove this company?")) {
                    Admin.clientFactory.getEventBus().fireEvent(new RemoveCompany(companyDTO));
                }
            }
        });
    }
}