package com.geocento.webapps.eobroker.shared.entities.dtos;

import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * Created by thomas on 03/06/2016.
 */
@JsonTypeName("circle")
public class AoICircleDTO extends AoIDTO {

    double radius;

    public AoICircleDTO() {
    }

}
