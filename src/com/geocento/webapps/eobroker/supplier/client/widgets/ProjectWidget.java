package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveProject;
import com.geocento.webapps.eobroker.supplier.client.places.ProjectPlace;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProjectDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCardAction;
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
    MaterialCardAction action;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLink edit;
    @UiField
    MaterialLink remove;
    @UiField
    MaterialImageLoading imagePanel;

    public ProjectWidget(final ProjectDTO projectDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));

        imagePanel.setImageUrl(projectDTO.getImageUrl());
        imagePanel.addClickHandler(event -> Supplier.clientFactory.getPlaceController().goTo(new ProjectPlace(ProjectPlace.TOKENS.id.toString() + "=" + projectDTO.getId())));

        title.setText(projectDTO.getName());

        edit.addClickHandler(event -> Supplier.clientFactory.getPlaceController().goTo(new ProjectPlace(ProjectPlace.TOKENS.id.toString() + "=" + projectDTO.getId())));
        remove.addClickHandler(event -> Supplier.clientFactory.getEventBus().fireEvent(new RemoveProject(projectDTO.getId())));
    }

}