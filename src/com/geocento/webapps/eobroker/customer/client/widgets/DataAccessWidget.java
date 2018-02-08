package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.google.gwt.core.client.GWT;
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
    MaterialImageLoading image;
    @UiField
    MaterialLabel pitch;
    @UiField
    MaterialCardTitle title;
    @UiField
    MaterialLabel details;
    @UiField
    MaterialPanel actions;
    @UiField
    MaterialIcon imageIcon;
    @UiField
    MaterialLabel descriptionFull;

    private Color color;

    public DataAccessWidget(DatasetAccess datasetAccess, boolean isFree) {

        initWidget(ourUiBinder.createAndBindUi(this));

        // TODO - change uri if free or commercial
        title.setText(StringUtils.isEmpty(datasetAccess.getTitle()) ? "No title" : datasetAccess.getTitle());
        pitch.setText(StringUtils.isEmpty(datasetAccess.getPitch()) ? "No description" : datasetAccess.getPitch());
        IconType iconType = IconType.HELP_OUTLINE;
        color = isFree ? Color.GREEN : Color.BLUE;
        boolean hasImage = !StringUtils.isEmpty(datasetAccess.getImageUrl());
        image.setVisible(hasImage);
        imageIcon.setVisible(!hasImage);
        if(hasImage) {
            image.setImageUrl(datasetAccess.getImageUrl());
        } else {
            imageIcon.setBackgroundColor(color);
            if (datasetAccess instanceof DatasetAccessFile) {
                iconType = IconType.ARCHIVE;
            } else if (datasetAccess instanceof DatasetAccessAPP) {
                iconType = IconType.WEB;
            } else if (datasetAccess instanceof DatasetAccessOGC) {
                iconType = IconType.MAP;
            } else if (datasetAccess instanceof DatasetAccessAPI) {
                iconType = IconType.CLOUD_CIRCLE;
            } else if (datasetAccess instanceof DatasetAccessKML) {
                iconType = IconType.MAP;
            }
            imageIcon.setIconType(iconType);
        }
        descriptionFull.setText(datasetAccess.getTitle() + ": " + datasetAccess.getPitch());
    }

    public MaterialLink addAction(String name, String url, String target, String tooltip) {
        MaterialLink materialLink = new MaterialLink(name);
        if(url != null) {
            materialLink.setHref(url);
            materialLink.setTarget(target);
        }
        materialLink.setTextColor(color);
        materialLink.setMarginRight(10);
        materialLink.setTextColor(color);
        materialLink.setTooltip(tooltip);
        actions.add(materialLink);
        return materialLink;
    }

/*
    public MaterialLink addAction(IconType iconType, String url, String target, String tooltip) {
        MaterialLink materialLink = new MaterialLink("", url);
        materialLink.setBackgroundColor(color);
        materialLink.setIconColor(Color.WHITE);
        materialLink.setIconType(iconType);
        materialLink.setType(ButtonType.FLOATING);
        materialLink.setMarginRight(10);
        materialLink.setTextColor(color);
        materialLink.setTarget(target);
        materialLink.setTooltip(tooltip);
        actions.add(materialLink);
        return materialLink;
    }
*/

    public void setComment(String comment) {
        details.setVisible(true);
        details.setText(comment);
    }

}