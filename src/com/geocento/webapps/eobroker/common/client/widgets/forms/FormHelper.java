package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.shared.entities.AoIFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.TextFormElement;

/**
 * Created by thomas on 29/06/2016.
 */
public class FormHelper {

    static public ElementEditor createEditor(final FormElement formElement) {
        if(formElement instanceof AoIFormElement) {

        } else if(formElement instanceof TextFormElement) {
            TextEditor textEditor = new TextEditor();
            textEditor.setFormElement((TextFormElement) formElement);
            return textEditor;
        }
        return null;
    }

}
