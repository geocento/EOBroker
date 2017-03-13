package com.geocento.webapps.eobroker.common.shared.entities.formelements;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by thomas on 23/06/2016.
 */
@Entity
@DiscriminatorValue("BOOLEAN")
public class BooleanFormElement extends FormElement {

    Boolean value;

    public BooleanFormElement() {
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }
}
