package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.utils.NotificationHelper;
import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import gwt.material.design.addins.client.bubble.MaterialBubble;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 29/05/2017.
 */
public class NotificationWidget extends MaterialPanel {

    interface ConversationWidgetUiBinder extends UiBinder<MaterialBubble, NotificationWidget> {
    }

    private static ConversationWidgetUiBinder ourUiBinder = GWT.create(ConversationWidgetUiBinder.class);

    @UiField
    MaterialLabel comment;
    @UiField
    MaterialLink title;

    public NotificationWidget(NotificationDTO notificationDTO) {

        add(ourUiBinder.createAndBindUi(this));

        title.setText("Notification on " + (notificationDTO.getCreationDate() == null ? "undefined" : DateUtils.formatDateTime(notificationDTO.getCreationDate())));
        comment.setText(notificationDTO.getMessage());

        title.setHref("#" + PlaceHistoryHelper.convertPlace(NotificationHelper.createNotificationPlace(notificationDTO)));
    }

}