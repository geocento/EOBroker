package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.shared.entities.TextFormElement;
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

    public void setText(String text) {
        textBox.setText(text);
    }

}
