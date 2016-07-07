package com.geocento.webapps.eobroker.common.shared.entities.notifications;

import javax.persistence.*;

/**
 * Created by thomas on 06/07/2016.
 */
@Entity
public class Notification {

    static public enum TYPE {MESSAGE, ORDER};

    @Id
    @GeneratedValue
    Long id;

    @Enumerated(EnumType.STRING)
    TYPE type;

    @Column(length = 1000)
    String text;

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
