package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.DateFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import gwt.material.design.client.ui.MaterialDatePicker;

import java.util.Date;

/**
 * Created by thomas on 23/06/2016.
 */
public class DateEditor extends ElementEditor<DateFormElement> {

    private final MaterialDatePicker datePicker;

    public DateEditor() {
        datePicker = new MaterialDatePicker();
        add(datePicker);
    }

    @Override
    public void setFormElement(DateFormElement dateFormElement) {
        super.setFormElement(dateFormElement);
        // TODO - add extra stuff here
        datePicker.setDateMin(dateFormElement.getMinDate());
        datePicker.setDateMax(dateFormElement.getMaxDate());
    }

    @Override
    public FormElementValue getFormElementValue() throws Exception {
        Date value = datePicker.getValue();
        FormElementValue formElementValue = new FormElementValue();
        formElementValue.setValue(value.getTime() + "");
        formElementValue.setName(formElement.getName());
        formElementValue.setFormid(formElement.getFormid());
        return formElementValue;
    }

    @Override
    public void setFormElementValue(String value) {
        setValue(new Date(Long.valueOf(value)));
    }

    public void setValue(Date value) {
        datePicker.setValue(value);
    }

}
