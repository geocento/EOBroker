package com.geocento.webapps.eobroker.customer.client.events;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 14/06/2017.
 */
public class WCSRequest extends GwtEvent<WCSRequestHandler> {

    public static Type<WCSRequestHandler> TYPE = new Type<WCSRequestHandler>();

    private final AoIDTO aoi;

    public WCSRequest(AoIDTO aoi) {
        this.aoi = aoi;
    }

    public AoIDTO getAoi() {
        return aoi;
    }

    public Type<WCSRequestHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(WCSRequestHandler handler) {
        handler.onWCSRequest(this);
    }
}
