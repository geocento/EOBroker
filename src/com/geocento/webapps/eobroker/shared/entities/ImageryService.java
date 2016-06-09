package com.geocento.webapps.eobroker.shared.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * Created by thomas on 06/06/2016.
 */
@Entity(name = "imageryservice")
public class ImageryService {

    @Id
    Long id;

    @Column(length = 1000, unique = true)
    String name;

    @Column(length = 1000)
    String iconURL;

    @Column(length = 1000)
    String description;

    @OneToMany
    Company company;

}
