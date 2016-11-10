package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveProject;
import com.geocento.webapps.eobroker.supplier.client.places.ProjectPlace;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProjectDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCardAction;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 09/06/2016.
 */
public class ProjectWidget extends Composite {

    interface ProjectUiBinder extends UiBinder<Widget, ProjectWidget> {
    }

    private static ProjectUiBinder ourUiBinder = GWT.create(ProjectUiBinder.class);

    @UiField
    MaterialImage image;
    @UiField
    MaterialCardAction action;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLink edit;
    @UiField
    MaterialLink remove;

    public ProjectWidget(final ProjectDTO projectDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.setUrl(projectDTO.getImageUrl());
        title.setText(projectDTO.getName());
        edit.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Supplier.clientFactory.getPlaceController().goTo(new ProjectPlace(ProjectPlace.TOKENS.id.toString() + "=" + projectDTO.getId()));
            }
        });
        remove.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Supplier.clientFactory.getEventBus().fireEvent(new RemoveProject(projectDTO.getId()));
            }
        });
    }

}