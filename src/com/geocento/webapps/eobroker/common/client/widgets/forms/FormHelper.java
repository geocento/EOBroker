package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.*;

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
        } else if(formElement instanceof DateFormElement) {
            DateEditor dateEditor = new DateEditor();
            dateEditor.setFormElement((DateFormElement) formElement);
            return dateEditor;
        } else if(formElement instanceof ChoiceFormElement) {
            ChoiceEditor choiceEditor = new ChoiceEditor();
            choiceEditor.setFormElement((ChoiceFormElement) formElement);
            return choiceEditor;
        } else if(formElement instanceof IntegerFormElement) {
            IntegerEditor integerEditor = new IntegerEditor();
            integerEditor.setFormElement((IntegerFormElement) formElement);
            return integerEditor;
        } else if(formElement instanceof DoubleFormElement) {
            DoubleEditor doubleEditor = new DoubleEditor();
            doubleEditor.setFormElement((DoubleFormElement) formElement);
            return doubleEditor;
        }
        return null;
    }

}
