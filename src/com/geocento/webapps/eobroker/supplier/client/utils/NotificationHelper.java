package com.geocento.webapps.eobroker.supplier.client.utils;

import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;
import com.geocento.webapps.eobroker.common.shared.entities.requests.RequestDTO;
import com.geocento.webapps.eobroker.supplier.client.places.ConversationPlace;
import com.geocento.webapps.eobroker.supplier.client.places.EOBrokerPlace;
import com.geocento.webapps.eobroker.supplier.client.places.OrderPlace;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierNotificationDTO;
import com.google.gwt.place.shared.Place;

/**
 * Created by thomas on 29/05/2017.
 */
public class NotificationHelper {

    public static Place convertToPlace(SupplierNotificationDTO supplierNotificationDTO) {
        EOBrokerPlace place = null;
        switch(supplierNotificationDTO.getType()) {
            case MESSAGE:
                place = new ConversationPlace(ConversationPlace.TOKENS.id.toString() + "=" + supplierNotificationDTO.getLinkId());
                break;
            case OTSPRODUCTREQUEST:
            case PRODUCTREQUEST:
            case IMAGESERVICEREQUEST:
            case IMAGEREQUEST:
                place = new OrderPlace(
                        supplierNotificationDTO.getLinkId(),
                        supplierNotificationDTO.getType() == SupplierNotification.TYPE.IMAGEREQUEST ? RequestDTO.TYPE.image :
                                supplierNotificationDTO.getType() == SupplierNotification.TYPE.IMAGESERVICEREQUEST ? RequestDTO.TYPE.imageservice :
                                        supplierNotificationDTO.getType() == SupplierNotification.TYPE.PRODUCTREQUEST ? RequestDTO.TYPE.product :
                                                supplierNotificationDTO.getType() == SupplierNotification.TYPE.OTSPRODUCTREQUEST ? RequestDTO.TYPE.otsproduct :
                                                        null
                );
                break;
        }
        return place;
    }

}
