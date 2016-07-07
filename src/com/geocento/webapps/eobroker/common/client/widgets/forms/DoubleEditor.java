package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.DoubleFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import gwt.material.design.client.ui.MaterialNumberBox;

/**
 * Created by thomas on 23/06/2016.
 */
public class DoubleEditor extends ElementEditor<DoubleFormElement> {

    private final MaterialNumberBox<Double> doubleBox;

    public DoubleEditor() {
        doubleBox = new MaterialNumberBox<>();
        add(doubleBox);
    }

    @Override
    public void setFormElement(DoubleFormElement doubleFormElement) {
        super.setFormElement(doubleFormElement);
        // TODO - add extra stuff here
        doubleBox.setMin(doubleFormElement.getMin() + "");
        doubleBox.setMax(doubleFormElement.getMax() + "");
    }

    @Override
    public FormElementValue getFormElementValue() throws Exception {
        Double value = doubleBox.getValue();
        if(value < formElement.getMin()) {
            throw new Exception("Minimum value is " + formElement.getMin());
        }
        if(value > formElement.getMax()) {
            throw new Exception("Maximum value is " + formElement.getMax());
        }
        FormElementValue formElementValue = new FormElementValue();
        formElementValue.setValue(value + "");
        formElementValue.setName(formElement.getName());
        formElementValue.setFormid(formElement.getFormid());
        return formElementValue;
    }

    @Override
    public void setFormElementValue(String value) {
        setValue(Double.valueOf(value));
    }

    public void setValue(Double value) {
        doubleBox.setValue(value);
    }

}
