package com.geocento.webapps.eobroker.admin.shared.dtos;

/**
 * Created by thomas on 30/05/2017.
 */
public class AdminWebSocketMessage {

    static public enum TYPE {notification, conversationMessage, requestMessage};

    TYPE type;

    // list of possible actual values
    NotificationDTO notificationDTO;

    public AdminWebSocketMessage() {
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public NotificationDTO getNotificationDTO() {
        return notificationDTO;
    }

    public void setNotificationDTO(NotificationDTO notificationDTO) {
        this.notificationDTO = notificationDTO;
    }

}
