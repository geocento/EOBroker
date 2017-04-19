package com.geocento.webapps.eobroker.common.shared.entities.subscriptions;

import com.geocento.webapps.eobroker.common.shared.entities.User;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by thomas on 19/04/2017.
 */
@Entity
public class FollowingEvent {

    @GeneratedValue
    @Id
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    Event event;

    @Temporal(TemporalType.TIMESTAMP)
    Date creationDate;

    public FollowingEvent() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
