package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLabel;

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
    MaterialButton action;
    @UiField
    MaterialLabel pitch;
    @UiField
    MaterialLabel title;

    public DataAccessWidget(DatasetAccess datasetAccess, boolean isFree) {

        initWidget(ourUiBinder.createAndBindUi(this));

        // TODO - change uri if free or commercial
        title.setText(datasetAccess.getTitle());
        pitch.setText(datasetAccess.getPitch());
        IconType iconType = IconType.HELP_OUTLINE;
        IconType actionIconType = IconType.HELP_OUTLINE;
        if(isFree) {
            if(datasetAccess instanceof DatasetAccessFile) {
                iconType = IconType.ARCHIVE;
                actionIconType = IconType.GET_APP;
            } else if(datasetAccess instanceof DatasetAccessAPP) {
                iconType = IconType.WEB;
                actionIconType = IconType.OPEN_IN_BROWSER;
            } else if(datasetAccess instanceof DatasetAccessOGC) {
                iconType = IconType.MAP;
                actionIconType = IconType.LAYERS;
            } else if(datasetAccess instanceof DatasetAccessAPI) {
                iconType = IconType.CLOUD_CIRCLE;
                actionIconType = IconType.CONTENT_COPY;
            }
        } else {
            if(datasetAccess instanceof DatasetAccessFile) {
                iconType = IconType.ARCHIVE;
                actionIconType = IconType.INFO;
            } else if(datasetAccess instanceof DatasetAccessAPP) {
                iconType = IconType.WEB;
                actionIconType = IconType.INFO;
            } else if(datasetAccess instanceof DatasetAccessOGC) {
                iconType = IconType.MAP;
                actionIconType = IconType.INFO;
            } else if(datasetAccess instanceof DatasetAccessAPI) {
                iconType = IconType.CLOUD_CIRCLE;
                actionIconType = IconType.INFO;
            }
        }
        Color color = isFree ? Color.GREEN : Color.BLUE;
        image.setBackgroundColor(color);
        action.setText("");
        action.setIconType(actionIconType);
        action.setBackgroundColor(color);
        image.setIconType(iconType);
    }

    public HasClickHandlers getAction() {
        return action;
    }
}