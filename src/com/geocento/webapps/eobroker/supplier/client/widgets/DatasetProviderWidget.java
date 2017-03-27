package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveDataset;
import com.geocento.webapps.eobroker.supplier.client.places.DatasetProviderPlace;
import com.geocento.webapps.eobroker.supplier.shared.dtos.DatasetProviderDTO;
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
public class DatasetProviderWidget extends Composite {

    interface DatasetProviderUiBinder extends UiBinder<Widget, DatasetProviderWidget> {
    }

    private static DatasetProviderUiBinder ourUiBinder = GWT.create(DatasetProviderUiBinder.class);

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

    public DatasetProviderWidget(final DatasetProviderDTO datasetProviderDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));

        imagePanel.setImageUrl(datasetProviderDTO.getIconURL());

        title.setText(datasetProviderDTO.getName());
        edit.addClickHandler(event -> Supplier.clientFactory.getPlaceController().goTo(new DatasetProviderPlace(DatasetProviderPlace.TOKENS.id.toString() + "=" + datasetProviderDTO.getId())));
        remove.addClickHandler(event -> Supplier.clientFactory.getEventBus().fireEvent(new RemoveDataset(datasetProviderDTO.getId())));
    }

}