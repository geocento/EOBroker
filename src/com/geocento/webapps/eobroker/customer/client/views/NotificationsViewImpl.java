package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
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

    }

    @Override
    public Widget asWidget() {
        return this;
    }

}