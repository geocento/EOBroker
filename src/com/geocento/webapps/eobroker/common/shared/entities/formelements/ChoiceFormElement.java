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

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }
}
