package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.places.SearchPagePlace;
import com.geocento.webapps.eobroker.customer.shared.ChallengeDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCardTitle;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 09/06/2016.
 */
public class ChallengeWidget extends Composite {

    interface ChallengeWidgetUiBinder extends UiBinder<Widget, ChallengeWidget> {
    }

    private static ChallengeWidgetUiBinder ourUiBinder = GWT.create(ChallengeWidgetUiBinder.class);

    @UiField
    MaterialImageLoading image;
    @UiField
    MaterialCardTitle title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLink view;
    @UiField
    MaterialLink products;
    @UiField
    MaterialLabel descriptionFull;

    public ChallengeWidget(final ChallengeDTO challengeDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.setImageUrl(challengeDTO.getImageUrl());
        title.setText(challengeDTO.getName());
        description.setText(challengeDTO.getShortDescription());

        view.setHref("#" + PlaceHistoryHelper.convertPlace(
                new FullViewPlace(Utils.generateTokens(FullViewPlace.TOKENS.challengeid.toString(), challengeDTO.getId() + ""))));

        boolean hasProducts = challengeDTO.getNumberProducts() > 0;
        products.setVisible(hasProducts);
        if(hasProducts) {
            products.setText(challengeDTO.getNumberProducts() + "");
            products.setTooltip(challengeDTO.getNumberProducts() + " products helping tackle this challenge, click to view them");
            products.setHref("#" + PlaceHistoryHelper.convertPlace(
                    new SearchPagePlace(Utils.generateTokens(SearchPagePlace.TOKENS.challengeId.toString(), challengeDTO.getId() + ""))));
        }

        description.setText("'" + challengeDTO.getName() + "' " + challengeDTO.getShortDescription());
    }
}