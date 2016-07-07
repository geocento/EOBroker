package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.ChoiceFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import gwt.material.design.client.ui.MaterialListBox;

/**
 * Created by thomas on 23/06/2016.
 */
public class ChoiceEditor extends ElementEditor<ChoiceFormElement> {

    private final MaterialListBox listBox;

    public ChoiceEditor() {
        listBox = new MaterialListBox();
        add(listBox);
    }

    @Override
    public void setFormElement(ChoiceFormElement choiceFormElement) {
        super.setFormElement(choiceFormElement);
        listBox.setMultipleSelect(choiceFormElement.isMultiple());
        for(String value : choiceFormElement.getChoices()) {
            listBox.addItem(value);
        }
    }

    @Override
    public FormElementValue getFormElementValue() throws Exception {
        String[] values = listBox.getItemsSelected();
        if(values.length == 0) {
            throw new Exception("At least one selection is required");
        }
        FormElementValue formElementValue = new FormElementValue();
        formElementValue.setValue(StringUtils.join(values, ","));
        formElementValue.setName(formElement.getName());
        formElementValue.setFormid(formElement.getFormid());
        return formElementValue;
    }

    @Override
    public void setFormElementValue(String value) {
        setValue(value.split(","));
    }

    public void setValue(String[] values) {
        // unselect all first
        for(String value : listBox.getItemsSelected()) {
            listBox.setValueSelected(value, false);
        }
        // now select the selected values
        for(String value : values) {
            listBox.setValueSelected(value, true);
        }
    }

}
