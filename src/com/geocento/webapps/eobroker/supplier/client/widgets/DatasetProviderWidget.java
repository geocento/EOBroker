package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveDataset;
import com.geocento.webapps.eobroker.supplier.client.places.DatasetProviderPlace;
import com.geocento.webapps.eobroker.supplier.shared.dtos.DatasetProviderDTO;
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
public class DatasetProviderWidget extends Composite {

    interface DatasetProviderUiBinder extends UiBinder<Widget, DatasetProviderWidget> {
    }

    private static DatasetProviderUiBinder ourUiBinder = GWT.create(DatasetProviderUiBinder.class);

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

    public DatasetProviderWidget(final DatasetProviderDTO datasetProviderDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.setUrl(datasetProviderDTO.getIconURL());
        title.setText(datasetProviderDTO.getName());
        edit.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Supplier.clientFactory.getPlaceController().goTo(new DatasetProviderPlace(DatasetProviderPlace.TOKENS.id.toString() + "=" + datasetProviderDTO.getId()));
            }
        });
        remove.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Supplier.clientFactory.getEventBus().fireEvent(new RemoveDataset(datasetProviderDTO.getId()));
            }
        });
    }

}