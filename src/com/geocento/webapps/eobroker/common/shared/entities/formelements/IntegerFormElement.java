package com.geocento.webapps.eobroker.common.shared.entities.formelements;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by thomas on 23/06/2016.
 */
@Entity
@DiscriminatorValue("INTEGER")
public class IntegerFormElement extends FormElement {

    Integer min;
    Integer max;

    public IntegerFormElement() {
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }
}
