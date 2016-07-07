package com.geocento.webapps.eobroker.common.shared.entities.formelements;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by thomas on 23/06/2016.
 */
@Entity
@DiscriminatorValue("TEXT")
public class TextFormElement extends FormElement {

    int min;
    int max;

    public TextFormElement() {
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
