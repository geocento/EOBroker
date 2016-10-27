package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.CSWBriefRecord;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 09/06/2016.
 */
public class DatasetWidget extends Composite {

    interface DatasetWidgetUiBinder extends UiBinder<Widget, DatasetWidget> {
    }

    private static DatasetWidgetUiBinder ourUiBinder = GWT.create(DatasetWidgetUiBinder.class);

    @UiField
    MaterialImage image;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLink information;
    @UiField
    MaterialLink type;

    public DatasetWidget(CSWBriefRecord cswBriefRecord) {
        initWidget(ourUiBinder.createAndBindUi(this));
        Extent extent = cswBriefRecord.getExtent();
        String extentString =
                extent.getNorth() + "," + extent.getWest() + "|" +
                extent.getNorth() + "," + (extent.getWest() - extent.getEast()) / 2.0 + "|" +
                extent.getNorth() + "," + extent.getEast() + "|" +
                extent.getSouth() + "," + extent.getEast() + "|" +
                extent.getSouth() + "," + (extent.getWest() - extent.getEast()) / 2.0 + "|" +
                extent.getSouth() + "," + extent.getWest() + "|" +
                extent.getNorth() + "," + extent.getWest();
        image.setUrl("http://maps.google.com/maps/api/staticmap?center=0.000,0.000&zoom=0&path=color:red|weight:4|" + extentString + "&size=300x200");
        description.setText(cswBriefRecord.getTitle());
        type.setText(cswBriefRecord.getType().toString().toUpperCase());
        information.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + cswBriefRecord.getId())));
    }
}