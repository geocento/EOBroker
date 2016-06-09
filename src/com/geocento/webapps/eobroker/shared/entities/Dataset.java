package com.geocento.webapps.eobroker.shared.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity(name = "dataset")
public class Dataset {

    @Id
    Long id;

    @Column(length = 1000, unique = true)
    String name;

    @Column(length = 1000)
    String iconURL;

    @Column(length = 1000)
    String description;


}
