package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by thomas on 29/06/2016.
 */
public interface LogOutHandler extends EventHandler {
    void onLogOut(LogOut event);
}
