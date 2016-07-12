package com.geocento.webapps.eobroker.supplier.client.utils;

import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.events.SupplierNotifications;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierNotificationDTO;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 07/07/2016.
 */
public class NotificationMonitoring {

    static private List<SupplierNotificationDTO> supplierNotifications = new ArrayList<>();

    public static void start(final ClientFactory clientFactory) {
        try {
            REST.withCallback(new MethodCallback<List<SupplierNotificationDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<SupplierNotificationDTO> supplierNotifications) {
                    NotificationMonitoring.supplierNotifications.addAll(supplierNotifications);
                    clientFactory.getEventBus().fireEvent(new SupplierNotifications(supplierNotifications));
                }
            }).call(ServicesUtil.assetsService).getNotifications();
        } catch (Exception e) {

        }
    }

    public static List<SupplierNotificationDTO> getNotifications() {
        return supplierNotifications;
    }

}
