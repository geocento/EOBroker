package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.TextFormElement;
import gwt.material.design.client.ui.MaterialTextBox;

/**
 * Created by thomas on 23/06/2016.
 */
public class TextEditor extends ElementEditor<TextFormElement> {

    private final MaterialTextBox textBox;

    public TextEditor() {
        textBox = new MaterialTextBox();
        add(textBox);
    }

    @Override
    public void setFormElement(TextFormElement textFormElement) {
        super.setFormElement(textFormElement);
        // TODO - add extra stuff here
        textBox.setLength(textFormElement.getMax());
    }

    @Override
    public FormElementValue getFormElementValue() throws Exception {
        String value = textBox.getText();
        if(value.length() < formElement.getMin()) {
            throw new Exception("Minimum " + formElement.getMin() + " characters");
        }
        if(value.length() > formElement.getMax()) {
            throw new Exception("Maximum " + formElement.getMax() + " characters");
        }
        FormElementValue formElementValue = new FormElementValue();
        formElementValue.setValue(value);
        formElementValue.setName(formElement.getName());
        formElementValue.setFormid(formElement.getFormid());
        return formElementValue;
    }

    @Override
    public void setFormElementValue(String value) {
        setText(value);
    }

    public void setText(String text) {
        textBox.setText(text);
    }

}
