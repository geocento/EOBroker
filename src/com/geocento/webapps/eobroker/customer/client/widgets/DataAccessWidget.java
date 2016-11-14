package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.places.VisualisationPlace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialAnchorButton;
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
    MaterialAnchorButton action;
    @UiField
    MaterialLabel pitch;

    public DataAccessWidget(DatasetAccess datasetAccess, boolean isFree) {
        initWidget(ourUiBinder.createAndBindUi(this));
        pitch.setText(datasetAccess.getPitch());
        IconType iconType = IconType.HELP_OUTLINE;
        String text = "Unknown";
        String url = "";
        if(isFree) {
            switch (datasetAccess.getAccessType()) {
                case download:
                    iconType = IconType.GET_APP;
                    text = "Download File";
                    url = datasetAccess.getUri();
                    break;
                case webapplication:
                    iconType = IconType.WEB;
                    text = "Open URL";
                    url = datasetAccess.getUri();
                    break;
                case wms:
                case wfs:
                case wcs:
                    iconType = IconType.LAYERS;
                    text = "View in map";
                    url = "#" + PlaceHistoryHelper.convertPlace(new VisualisationPlace(
                            Utils.generateTokens(VisualisationPlace.TOKENS.uri.toString(), URL.encodeQueryString(datasetAccess.getUri()))));
                    break;
                case api:
                    iconType = IconType.CLOUD_CIRCLE;
                    text = "Copy URL";
                    break;
            }
        }
        action.setText(text);
        image.setIconType(iconType);
        action.setHref(url);
    }

}