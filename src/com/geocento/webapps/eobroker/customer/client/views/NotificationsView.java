package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface NotificationsView extends IsWidget {

    void setPresenter(Presenter presenter);

    void displayNotifications(List<NotificationDTO> notificationDTOs);

    public interface Presenter {
    }

}
