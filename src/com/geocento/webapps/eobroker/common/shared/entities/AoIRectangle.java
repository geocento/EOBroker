package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;

/**
 * Created by thomas on 03/06/2016.
 */
@Entity
@DiscriminatorValue("R")
public class AoIRectangle extends AoI {

    @Embedded
    Extent extent;

    public AoIRectangle() {
    }

}
