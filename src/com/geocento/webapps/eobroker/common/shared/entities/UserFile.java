package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;

/**
 * Created by thomas on 11/07/2017.
 */
@Entity
public class UserFile {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 100)
    String name;

    // the file location on the server
    @Column(length = 1000)
    String uri;

    @ManyToOne
    User user;

    public UserFile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
