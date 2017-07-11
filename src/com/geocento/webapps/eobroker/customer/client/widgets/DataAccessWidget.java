package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

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
    @UiField
    MaterialBadge format;
    @UiField
    MaterialTooltip tooltip;

    public DataAccessWidget(DatasetAccess datasetAccess, boolean isFree) {

        initWidget(ourUiBinder.createAndBindUi(this));

        // TODO - change uri if free or commercial
        title.setText(datasetAccess.getTitle());
        pitch.setText(datasetAccess.getPitch());
        IconType iconType = IconType.HELP_OUTLINE;
        IconType actionIconType = IconType.HELP_OUTLINE;
        String tooltip = null;
        String format = null;
        if(datasetAccess instanceof DatasetAccessFile) {
            iconType = IconType.ARCHIVE;
            actionIconType = IconType.INFO;
            tooltip = "Download file" +
                    (datasetAccess.getSize() == null ? "" : " size is " + Utils.displayFileSize(datasetAccess.getSize()));
        } else if(datasetAccess instanceof DatasetAccessAPP) {
            iconType = IconType.WEB;
            actionIconType = IconType.INFO;
            tooltip = "Open web application in new browser window";
        } else if(datasetAccess instanceof DatasetAccessOGC) {
            iconType = IconType.MAP;
            actionIconType = IconType.INFO;
            tooltip = "View data in EO Broker map visualisation page";
        } else if(datasetAccess instanceof DatasetAccessAPI) {
            iconType = IconType.CLOUD_CIRCLE;
            actionIconType = IconType.INFO;
            tooltip = "Open API help page in new browser window";
        } else if(datasetAccess instanceof DatasetAccessKML) {
            iconType = IconType.MAP;
            format = "KML";
        }
        Color color = isFree ? Color.GREEN : Color.BLUE;
        image.setBackgroundColor(color);
        action.setText("");
        action.setIconType(IconType.EXIT_TO_APP);
        action.setBackgroundColor(color);
        action.setVisible(false);
        image.setIconType(iconType);
        boolean hasFormat = format != null;
        this.format.setVisible(hasFormat);
        if(hasFormat) {
            this.format.setText(format);
            this.format.setTextColor(Color.WHITE);
            this.format.setBackgroundColor(color);
        }
    }

    public HasClickHandlers getAction() {
        return image;
    }
}