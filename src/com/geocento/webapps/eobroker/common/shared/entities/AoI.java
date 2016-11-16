package com.geocento.webapps.eobroker.common.shared.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.geocento.webapps.eobroker.common.server.Utils.GeometryConverter;

import javax.persistence.*;

/**
 * Created by thomas on 03/06/2016.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
public class AoI {

    @Id
    @GeneratedValue
    Long id;

    @Column
    String name;

    @Convert(converter = GeometryConverter.class)
    String geometry;

    @JsonIgnore
    @ManyToOne
    User user;

    public AoI() {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }
}
