package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;

/**
 * Created by thomas on 11/07/2017.
 */
@Entity
public class CustomerSettings {

    @Id
    @GeneratedValue
    Long id;

    public CustomerSettings() {
    }

}
