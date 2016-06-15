package com.geocento.webapps.eobroker.supplier.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 15/06/2016.
 */
public interface RemoveServiceHandler extends EventHandler {
    void onRemoveService(RemoveService event);
}
