package com.geocento.webapps.eobroker.supplier.shared.dtos;

import com.github.nmorel.gwtjackson.client.ObjectMapper;

/**
 * Created by thomas on 30/05/2017.
 */
public class SupplierWebSocketMessage {

    public static interface WebSocketMessageMapper extends ObjectMapper<SupplierWebSocketMessage> {};

    static public enum TYPE {notification, message};

    TYPE type;

    // list of possible actual values
    SupplierNotificationDTO notificationDTO;
    MessageDTO messageDTO;

    public SupplierWebSocketMessage() {
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public SupplierNotificationDTO getNotificationDTO() {
        return notificationDTO;
    }

    public void setNotificationDTO(SupplierNotificationDTO notificationDTO) {
        this.notificationDTO = notificationDTO;
    }

    public MessageDTO getMessageDTO() {
        return messageDTO;
    }

    public void setMessageDTO(MessageDTO messageDTO) {
        this.messageDTO = messageDTO;
    }
}
