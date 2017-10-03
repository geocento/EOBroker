package com.geocento.webapps.eobroker.customer.client.widgets.maps;

import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.WCSRequest;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;

/**
 * Created by thomas on 17/11/2016.
 */
public class VisualisationMapContainer extends MapContainer {

    private MaterialButton wcsSelectButton;

    public VisualisationMapContainer() {
        // add select and save buttons
        // add save buttom
        wcsSelectButton = new MaterialButton();
        wcsSelectButton.setBackgroundColor(Color.BLUE);
        wcsSelectButton.setIconType(IconType.CROP_SQUARE);
        addButton(wcsSelectButton, "Select a region to download");
        wcsSelectButton.addClickHandler(event -> {
            startDrawing("rectangle");
        });
        wcsSelectButton.setVisible(false);

        super.setPresenter(aoi -> {
            clearAoI();
            // get the WCS back
            Customer.clientFactory.getEventBus().fireEvent(new WCSRequest(aoi));
        });
    }

    public void setWCSEnabled(boolean enable) {
        wcsSelectButton.setVisible(enable);
    }

}
