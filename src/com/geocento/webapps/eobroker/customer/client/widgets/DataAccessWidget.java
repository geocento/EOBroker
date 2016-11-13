package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.AccessType;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialAnchorButton;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 08/11/2016.
 */
public class DataAccessWidget extends Composite {

    interface DataAccessWidgetUiBinder extends UiBinder<Widget, DataAccessWidget> {
    }

    private static DataAccessWidgetUiBinder ourUiBinder = GWT.create(DataAccessWidgetUiBinder.class);
    @UiField
    MaterialLink image;
    @UiField
    MaterialAnchorButton action;
    @UiField
    MaterialLabel pitch;

    public DataAccessWidget(DatasetAccess datasetAccess, boolean isFree) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.setIconType(datasetAccess.getAccessType() == AccessType.download ? IconType.ATTACH_FILE : IconType.ACCESS_ALARM);
        pitch.setText(datasetAccess.getPitch());
        String text = "Unknown";
        if(isFree) {
            switch (datasetAccess.getAccessType()) {
                case download:
                    text = "Download File";
                    break;
                case webapplication:
                    text = "Open URL";
                    break;
                case wms:
                case wfs:
                case wcs:
                    text = "View in map";
                    break;
                case api:
                    text = "Copy URL";
                    break;
            }
        }
        action.setText(text);
        action.setHref(datasetAccess.getUri());
    }

}