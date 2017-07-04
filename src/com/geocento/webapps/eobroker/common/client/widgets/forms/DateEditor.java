package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.client.utils.DateUtil;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.DateFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import gwt.material.design.client.ui.MaterialDatePicker;

import java.util.Date;

/**
 * Created by thomas on 23/06/2016.
 */
public class DateEditor extends ElementEditor<DateFormElement> {

    private final MaterialDatePicker datePicker;

    public DateEditor() {
        datePicker = new MaterialDatePicker();
        datePicker.setSelectionType(MaterialDatePicker.MaterialDatePickerType.YEAR_MONTH_DAY);
        datePicker.setAutoClose(true);
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
    protected void setPlaceHolder(String placeHolder) {
        datePicker.setPlaceholder(placeHolder);
    }

    @Override
    public FormElementValue getFormElementValue() throws Exception {
        Date value = datePicker.getValue();
        FormElementValue formElementValue = new FormElementValue();
        formElementValue.setValue(DateUtil.displayUTCDate(value));
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

    @Override
    public void resetValue() {
        setValue(null);
    }

    @Override
    public void setChangeListener(final ChangeListener changeListener) {
        datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                changeListener.hasChanged();
            }
        });
    }

}
