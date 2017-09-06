package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.BooleanFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import gwt.material.design.client.ui.MaterialSwitch;

/**
 * Created by thomas on 23/06/2016.
 */
public class BooleanEditor extends ElementEditor<BooleanFormElement> {

    private final MaterialSwitch materialSwitch;

    public BooleanEditor() {
        materialSwitch = new MaterialSwitch();
        add(materialSwitch);
    }

    @Override
    public void setFormElement(BooleanFormElement booleanFormElement) {
        super.setFormElement(booleanFormElement);
    }

    @Override
    protected void setPlaceHolder(String placeHolder) {
        materialSwitch.setHelperText(placeHolder);
    }

    @Override
    public FormElementValue getFormElementValue() throws Exception {
        Boolean value = materialSwitch.getValue();
        FormElementValue formElementValue = new FormElementValue();
        formElementValue.setValue(value + "");
        formElementValue.setName(formElement.getName());
        formElementValue.setFormid(formElement.getFormid());
        return formElementValue;
    }

    @Override
    public void setFormElementValue(String value) {
        setValue(value == null ? null : Boolean.valueOf(value));
    }

    @Override
    public void resetValue() {
        setValue(null);
    }

    public void setValue(Boolean value) {
        materialSwitch.setValue(value);
    }

    @Override
    public void setChangeListener(final ChangeListener changeListener) {
        materialSwitch.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                changeListener.hasChanged();
            }
        });
    }

}
