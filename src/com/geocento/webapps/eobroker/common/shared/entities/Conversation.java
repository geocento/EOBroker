package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity
public class Conversation {

    @Id
    String id;

    @ManyToOne
    User customer;

    @ManyToOne
    Company company;

    @Column(length = 1000)
    String topic;

    @OneToMany
    List<Message> messages;

    @Temporal(TemporalType.TIMESTAMP)
    Date creationDate;

    public Conversation() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
