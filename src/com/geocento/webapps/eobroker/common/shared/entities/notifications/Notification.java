package com.geocento.webapps.eobroker.common.shared.entities.notifications;

import com.geocento.webapps.eobroker.common.shared.entities.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class Notification {

    static public enum TYPE {MESSAGE, IMAGEREQUEST, IMAGESERVICEREQUEST, PRODUCTREQUEST, REQUESTMESSAGE, ORDER};

    @Id
    @GeneratedValue
    Long id;

    @Enumerated(EnumType.STRING)
    TYPE type;

    @Column(length = 1000)
    String message;

    @ManyToOne
    User user;

    @Column(length = 100)
    String linkId;

    @Temporal(TemporalType.TIMESTAMP)
    Date creationDate;

    boolean sent;
    boolean viewed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String text) {
        this.message = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isSent() {
        return sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean isViewed() {
        return viewed;
    }

    public void setViewed(boolean viewed) {
        this.viewed = viewed;
    }
}
