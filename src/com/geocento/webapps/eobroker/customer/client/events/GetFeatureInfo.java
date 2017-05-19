package com.geocento.webapps.eobroker.customer.client.events;

import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.PointJSNI;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 19/05/2017.
 */
public class GetFeatureInfo extends GwtEvent<GetFeatureInfoHandler> {

    public static Type<GetFeatureInfoHandler> TYPE = new Type<GetFeatureInfoHandler>();

    private PointJSNI mapPoint;
    private Extent extent;
    private int width;
    private int height;
    private int[] point;

    public GetFeatureInfo() {
    }

    public void setMapPoint(PointJSNI mapPoint) {
        this.mapPoint = mapPoint;
    }

    public PointJSNI getMapPoint() {
        return mapPoint;
    }

    public Type<GetFeatureInfoHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(GetFeatureInfoHandler handler) {
        handler.onGetFeatureInfo(this);
    }

    public Extent getExtent() {
        return extent;
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int[] getPoint() {
        return point;
    }

    public void setPoint(int[] point) {
        this.point = point;
    }

}
