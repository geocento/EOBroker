package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
import com.github.nmorel.gwtjackson.client.ObjectMapper;

/**
 * Created by thomas on 30/05/2017.
 */
public class WebSocketMessage {

    public static interface WebSocketMessageMapper extends ObjectMapper<WebSocketMessage> {};

    static public enum TYPE {notification, conversationMessage, requestMessage};

    TYPE type;

    // list of possible actual values
    NotificationDTO notificationDTO;
    MessageDTO messageDTO;

    String destination;

    public WebSocketMessage() {
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

    public MessageDTO getMessageDTO() {
        return messageDTO;
    }

    public void setMessageDTO(MessageDTO messageDTO) {
        this.messageDTO = messageDTO;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

}
