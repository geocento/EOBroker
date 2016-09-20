package com.geocento.webapps.eobroker.supplier.shared.dtos;

import java.util.List;

/**
 * Created by thomas on 16/09/2016.
 */
public class BaseRequestDTO {

    String id;
    UserDTO customer;
    List<MessageDTO> messages;

    public BaseRequestDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public UserDTO getCustomer() {
        return customer;
    }

    public void setCustomer(UserDTO customer) {
        this.customer = customer;
    }

    public List<MessageDTO> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageDTO> messages) {
        this.messages = messages;
    }
}
