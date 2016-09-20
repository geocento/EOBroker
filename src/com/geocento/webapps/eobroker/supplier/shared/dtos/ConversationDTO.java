package com.geocento.webapps.eobroker.supplier.shared.dtos;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class ConversationDTO {

    String id;
    UserDTO user;
    String topic;
    List<MessageDTO> messages;
    Date creationDate;

    public ConversationDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
