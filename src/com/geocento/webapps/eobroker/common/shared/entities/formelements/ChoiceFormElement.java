package com.geocento.webapps.eobroker.common.shared.entities.formelements;

import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import java.util.List;

/**
 * Created by thomas on 23/06/2016.
 */
@Entity
@DiscriminatorValue("CHOICE")
public class ChoiceFormElement extends FormElement {

    boolean multiple;
    boolean hasNone;

    @ElementCollection
    List<String> choices;

    public ChoiceFormElement() {
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public boolean isHasNone() {
        return hasNone;
    }

    public void setHasNone(boolean hasNone) {
        this.hasNone = hasNone;
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }
}
