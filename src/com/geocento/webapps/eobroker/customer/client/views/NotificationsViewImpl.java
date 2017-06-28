package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.widgets.NotificationWidget;
import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialRow;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class NotificationsViewImpl extends Composite implements NotificationsView {

    private Presenter presenter;

    interface NotificationsViewUiBinder extends UiBinder<Widget, NotificationsViewImpl> {
    }

    private static NotificationsViewUiBinder ourUiBinder = GWT.create(NotificationsViewUiBinder.class);
    @UiField
    MaterialRow notificationsPanel;

    public NotificationsViewImpl(ClientFactoryImpl clientFactory) {

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayNotifications(List<NotificationDTO> notificationDTOs) {
        notificationsPanel.clear();
        if(notificationDTOs == null || notificationDTOs.size() == 0) {
            this.notificationsPanel.add(new MaterialLabel("No notifications..."));
            return;
        }
        MaterialRow materialRow = new MaterialRow();
        this.notificationsPanel.add(materialRow);
        for(NotificationDTO notificationDTO : notificationDTOs) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
            materialColumn.add(new NotificationWidget(notificationDTO));
            materialRow.add(materialColumn);
        }

    }

    @Override
    public Widget asWidget() {
        return this;
    }

}