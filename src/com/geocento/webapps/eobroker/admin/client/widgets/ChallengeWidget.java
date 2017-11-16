package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.events.RemoveChallenge;
import com.geocento.webapps.eobroker.admin.client.places.ChallengePlace;
import com.geocento.webapps.eobroker.admin.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.admin.shared.dtos.ChallengeDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
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
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLink edit;
    @UiField
    MaterialLink remove;

    public ChallengeWidget(final ChallengeDTO challengeDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.setImageUrl(challengeDTO.getImageUrl());
        title.setText(challengeDTO.getName());
        description.setText(challengeDTO.getDescription());
        edit.setHref("#" + PlaceHistoryHelper.convertPlace(
                new ChallengePlace(Utils.generateTokens(ChallengePlace.TOKENS.id.toString(), challengeDTO.getId() + ""))));
        remove.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (Window.confirm("Are you sure you want to remove this challenge?")) {
                    Admin.clientFactory.getEventBus().fireEvent(new RemoveChallenge(challengeDTO));
                }
            }
        });
    }
}