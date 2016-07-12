package com.geocento.webapps.eobroker.common.server.Utils;

import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;

import javax.persistence.EntityManager;

/**
 * Created by thomas on 06/07/2016.
 */
public class NotificationHelper {

    public static void notifySupplier(EntityManager em, Company company, SupplierNotification.TYPE type, String message, String linkId) {
        SupplierNotification supplierNotification = new SupplierNotification();
        supplierNotification.setCompany(company);
        supplierNotification.setType(type);
        supplierNotification.setMessage(message);
        supplierNotification.setLinkId(linkId);
        em.persist(supplierNotification);
    }
}
