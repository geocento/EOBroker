package com.geocento.webapps.eobroker.admin.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 29/06/2016.
 */
public class LogOut extends GwtEvent<LogOutHandler> {
    public static Type<LogOutHandler> TYPE = new Type<LogOutHandler>();

    public Type<LogOutHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(LogOutHandler handler) {
        handler.onLogOut(this);
    }
}
