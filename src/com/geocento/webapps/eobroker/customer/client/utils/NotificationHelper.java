package com.geocento.webapps.eobroker.customer.client.utils;

import com.geocento.webapps.eobroker.customer.client.places.*;
import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;

/**
 * Created by thomas on 30/05/2017.
 */
public class NotificationHelper {

    public static EOBrokerPlace createNotificationPlace(NotificationDTO notificationDTO) {
        EOBrokerPlace place = null;
        switch(notificationDTO.getType()) {
            case MESSAGE:
                place = new ConversationPlace(ConversationPlace.TOKENS.conversationid.toString() + "=" + notificationDTO.getLinkId());
                break;
            case PRODUCTREQUEST:
                place = new ProductResponsePlace(ProductResponsePlace.TOKENS.id.toString() + "=" + notificationDTO.getLinkId());
                break;
            case IMAGESERVICEREQUEST:
                place = new ImageryResponsePlace(ImageryResponsePlace.TOKENS.id.toString() + "=" + notificationDTO.getLinkId());
                break;
            case IMAGEREQUEST:
                place = new ImagesResponsePlace(ImageryResponsePlace.TOKENS.id.toString() + "=" + notificationDTO.getLinkId());
                break;
        }
        return place;
    }

}
