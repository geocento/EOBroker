package com.geocento.webapps.eobroker.supplier.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 07/10/2016.
 */
public interface RemoveDatasetHandler extends EventHandler {
    void onRemoveDataset(RemoveDataset event);
}
