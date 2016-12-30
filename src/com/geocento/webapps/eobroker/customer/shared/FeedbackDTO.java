package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
public class FeedbackDTO {

    String id;
    String topic;
    List<MessageDTO> messages;
    Date creationDate;

    public FeedbackDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
