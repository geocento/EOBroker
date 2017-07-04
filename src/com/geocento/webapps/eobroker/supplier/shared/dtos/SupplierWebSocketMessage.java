package com.geocento.webapps.eobroker.supplier.shared.dtos;

/**
 * Created by thomas on 30/05/2017.
 */
public class SupplierWebSocketMessage {

    private String destination;

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    static public enum TYPE {notification, conversationMessage, logout, requestMessage};

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
