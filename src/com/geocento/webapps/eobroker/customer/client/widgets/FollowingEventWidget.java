package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.customer.shared.FollowingEventDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by thomas on 08/11/2016.
 */
public class FollowingEventWidget extends Composite {

    interface FollowingEventWidgetUiBinder extends UiBinder<Widget, FollowingEventWidget> {
    }

    private static FollowingEventWidgetUiBinder ourUiBinder = GWT.create(FollowingEventWidgetUiBinder.class);

    @UiField
    MaterialImage image;
    @UiField
    MaterialButton action;
    @UiField
    MaterialLabel message;
    @UiField
    MaterialLabel creationDate;

    public FollowingEventWidget(FollowingEventDTO followingEventDTO) {

        initWidget(ourUiBinder.createAndBindUi(this));

        image.setUrl(followingEventDTO.getCompanyDTO().getIconURL());
        message.setText(followingEventDTO.getMessage());
        creationDate.setText(DateUtils.dateFormat.format(followingEventDTO.getCreationDate()));
    }

    public HasClickHandlers getAction() {
        return action;
    }
}