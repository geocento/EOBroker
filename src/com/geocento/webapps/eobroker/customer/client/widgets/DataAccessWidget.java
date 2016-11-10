package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.AccessType;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialIcon;
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
    MaterialIcon image;
    @UiField
    MaterialLink action;
    @UiField
    MaterialLabel pitch;

    public DataAccessWidget(DatasetAccess datasetAccess) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.setIconType(datasetAccess.getAccessType() == AccessType.download ? IconType.ATTACH_FILE : IconType.ACCESS_ALARM);
        pitch.setText(datasetAccess.getPitch());
        action.setIconType(datasetAccess.getAccessType() == AccessType.download ? IconType.FILE_DOWNLOAD : IconType.ACCESS_ALARM);
        action.setHref(datasetAccess.getUrl());
    }

}