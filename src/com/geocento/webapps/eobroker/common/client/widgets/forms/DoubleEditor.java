package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.DoubleFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import gwt.material.design.client.ui.MaterialDoubleBox;

/**
 * Created by thomas on 23/06/2016.
 */
public class DoubleEditor extends ElementEditor<DoubleFormElement> {

    private final MaterialDoubleBox doubleBox;

    public DoubleEditor() {
        doubleBox = new MaterialDoubleBox();
        add(doubleBox);
    }

    @Override
    public void setFormElement(DoubleFormElement doubleFormElement) {
        super.setFormElement(doubleFormElement);
        // TODO - add extra stuff here
        if(doubleFormElement.getMin() != null) {
            doubleBox.setMin(doubleFormElement.getMin() + "");
        }
        if(doubleFormElement.getMax() != null) {
            doubleBox.setMax(doubleFormElement.getMax() + "");
        }
    }

    @Override
    public FormElementValue getFormElementValue() throws Exception {
        Double value = doubleBox.getValue();
        if(formElement.getMin() != null && value < formElement.getMin()) {
            throw new Exception("Minimum value is " + formElement.getMin());
        }
        if(formElement.getMax() != null && value > formElement.getMax()) {
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

    @Override
    public void setChangeListener(final ChangeListener changeListener) {
        doubleBox.addValueChangeHandler(new ValueChangeHandler<Double>() {
            @Override
            public void onValueChange(ValueChangeEvent<Double> event) {
                changeListener.hasChanged();
            }
        });
    }

}
