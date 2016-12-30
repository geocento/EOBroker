package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 30/12/2016.
 */
public interface ChangeStatusHandler extends EventHandler {
    void onChangeStatus(ChangeStatus event);
}
