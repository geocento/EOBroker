package com.geocento.webapps.eobroker.common.shared.entities;

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

    @ElementCollection
    List<String> choices;

    public ChoiceFormElement() {
    }

    public List<String> getChoices() {
        return choices;
    }

    public void setChoices(List<String> choices) {
        this.choices = choices;
    }
}
