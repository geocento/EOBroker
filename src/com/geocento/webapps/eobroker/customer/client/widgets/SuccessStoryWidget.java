package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.places.SuccessStoryPlace;
import com.geocento.webapps.eobroker.customer.shared.SuccessStoryDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 20/03/2017.
 */
public class SuccessStoryWidget extends Composite {

    interface SuccessStoryWidgetUiBinder extends UiBinder<Widget, SuccessStoryWidget> {
    }

    private static SuccessStoryWidgetUiBinder ourUiBinder = GWT.create(SuccessStoryWidgetUiBinder.class);

    @UiField
    MaterialCard panel;
    @UiField
    MaterialImageLoading image;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLabel date;
    @UiField
    MaterialLink info;
    @UiField
    MaterialLabel descriptionFull;

    static DateTimeFormat fmt = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH);

    public SuccessStoryWidget(final SuccessStoryDTO successStoryDTO) {

        initWidget(ourUiBinder.createAndBindUi(this));

        image.setImageUrl(successStoryDTO.getImageUrl());
        title.setText(successStoryDTO.getName());
        description.setText(successStoryDTO.getDescription());
        descriptionFull.setText("Story from " + successStoryDTO.getCompany().getName() + " with customer " + successStoryDTO.getCustomer().getName() + ". " +
                successStoryDTO.getDescription());
        date.setText(successStoryDTO.getDate() == null ? "" : "from " + fmt.format(successStoryDTO.getDate()));

        info.setHref("#" + PlaceHistoryHelper.convertPlace(new SuccessStoryPlace(Utils.generateTokens(SuccessStoryPlace.TOKENS.id.toString(), successStoryDTO.getId().toString()))));
    }

}