package com.geocento.webapps.eobroker.customer.client.events;

import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 30/12/2016.
 */
public class ChangeStatus extends GwtEvent<ChangeStatusHandler> {

    public static Type<ChangeStatusHandler> TYPE = new Type<ChangeStatusHandler>();

    private final Request.STATUS status;

    public ChangeStatus(Request.STATUS status) {
        this.status = status;
    }

    public Request.STATUS getStatus() {
        return status;
    }

    public Type<ChangeStatusHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ChangeStatusHandler handler) {
        handler.onChangeStatus(this);
    }
}
