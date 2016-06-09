package com.geocento.webapps.eobroker.shared.entities;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by thomas on 03/06/2016.
 */
@Entity
@DiscriminatorValue("P")
public class AoIPolygon extends AoI {

    @Column(length = 10000)
    String wktRings;

    public AoIPolygon() {
    }

    public String getWktRings() {
        return wktRings;
    }

    public void setWktRings(String wktRings) {
        this.wktRings = wktRings;
    }
}
