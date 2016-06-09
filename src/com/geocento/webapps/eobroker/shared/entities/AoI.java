package com.geocento.webapps.eobroker.shared.entities;

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

    public AoI() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
