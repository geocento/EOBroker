package com.geocento.webapps.eobroker.common.server.Utils;

import com.geocento.webapps.eobroker.common.server.websockets.NotificationSocket;
import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.AdminNotification;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.Notification;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;

import javax.persistence.EntityManager;
import java.util.Date;

/**
 * Created by thomas on 06/07/2016.
 */
public class NotificationHelper {

    public static void notifyCustomer(EntityManager em, User user, Notification.TYPE type, String message, String linkId) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setMessage(message);
        notification.setLinkId(linkId);
        notification.setCreationDate(new Date());
        em.persist(notification);
        NotificationSocket.sendMessage(user.getUsername(), "New notification");
    }

    public static void notifySupplier(EntityManager em, Company company, SupplierNotification.TYPE type, String message, String linkId) {
        SupplierNotification supplierNotification = new SupplierNotification();
        supplierNotification.setCompany(company);
        supplierNotification.setType(type);
        supplierNotification.setMessage(message);
        supplierNotification.setLinkId(linkId);
        em.persist(supplierNotification);
    }

    public static void notifyAdmin(EntityManager em, AdminNotification.TYPE type, String message, String linkId) {
        AdminNotification adminNotification = new AdminNotification();
        adminNotification.setType(type);
        adminNotification.setMessage(message);
        adminNotification.setLinkId(linkId);
        em.persist(adminNotification);
    }

}
