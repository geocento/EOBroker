package com.geocento.webapps.eobroker.common.shared.entities.formelements;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by thomas on 23/06/2016.
 */
@Entity
@DiscriminatorValue("DOUBLE")
public class DoubleFormElement extends FormElement {

    Double min;
    Double max;

    public DoubleFormElement() {
    }

    public Double getMin() {
        return min;
    }

    public void setMin(Double min) {
        this.min = min;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }
}
