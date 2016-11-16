package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by thomas on 03/06/2016.
 */
@Entity
@DiscriminatorValue("P")
public class AoIPolygon extends AoI {

    public AoIPolygon() {
    }

}
