package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.*;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 23/06/2016.
 */
public class FormElementEditor<T extends FormElement> extends Composite {

    interface FormElementEditorUiBinder extends UiBinder<Widget, FormElementEditor> {
    }

    private static FormElementEditorUiBinder ourUiBinder = GWT.create(FormElementEditorUiBinder.class);

    @UiField
    MaterialTextBox name;
    @UiField
    MaterialTextArea description;
    @UiField
    MaterialLink save;
    @UiField
    MaterialLink cancel;
    @UiField
    MaterialTextBox formId;
    @UiField
    MaterialCardContent content;
    @UiField
    HTMLPanel additionalFields;

    private T formElement;

    public FormElementEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setFormElement(T formElement) {
        this.formElement = formElement;
        name.setText(formElement.getName());
        description.setText(formElement.getDescription());
        formId.setText(formElement.getFormid());
        additionalFields.clear();
        if(formElement instanceof TextFormElement) {
            TextFormElement textFormElement = (TextFormElement) formElement;
            MaterialIntegerBox minBox = new MaterialIntegerBox();
            minBox.setPlaceholder("Min number of characters");
            minBox.setMin("0");
            minBox.setValue(textFormElement.getMin());
            additionalFields.add(minBox);
            MaterialIntegerBox maxBox = new MaterialIntegerBox();
            maxBox.setPlaceholder("Max number of characters");
            maxBox.setMin("10");
            maxBox.setValue(textFormElement.getMax());
            additionalFields.add(maxBox);
        } else if(formElement instanceof DateFormElement) {
            DateFormElement dateFormElement = (DateFormElement) formElement;
            MaterialDatePicker startDateBox = new MaterialDatePicker();
            startDateBox.setPlaceholder("Min date allowed");
            startDateBox.setValue(dateFormElement.getMinDate());
            additionalFields.add(startDateBox);
            MaterialDatePicker stopDateBox = new MaterialDatePicker();
            stopDateBox.setPlaceholder("Max date allowed");
            stopDateBox.setValue(dateFormElement.getMaxDate());
            additionalFields.add(stopDateBox);
        } else if(formElement instanceof IntegerFormElement) {
            IntegerFormElement integerFormElement = (IntegerFormElement) formElement;
            MaterialIntegerBox minBox = new MaterialIntegerBox();
            minBox.setPlaceholder("Min authorised value");
            minBox.setValue(integerFormElement.getMin());
            additionalFields.add(minBox);
            MaterialIntegerBox maxBox = new MaterialIntegerBox();
            maxBox.setPlaceholder("Max authorised value");
            maxBox.setValue(integerFormElement.getMax());
            additionalFields.add(maxBox);
        } else if(formElement instanceof DoubleFormElement) {
            DoubleFormElement doubleFormElement = (DoubleFormElement) formElement;
            MaterialDoubleBox minBox = new MaterialDoubleBox();
            minBox.setPlaceholder("Min authorised value");
            minBox.setValue(doubleFormElement.getMin());
            additionalFields.add(minBox);
            MaterialDoubleBox maxBox = new MaterialDoubleBox();
            maxBox.setPlaceholder("Max authorised value");
            maxBox.setValue(doubleFormElement.getMax());
            additionalFields.add(maxBox);
        } else if(formElement instanceof ChoiceFormElement) {
            ChoiceFormElement choiceFormElement = (ChoiceFormElement) formElement;
            MaterialCheckBox multipleBox = new MaterialCheckBox();
            multipleBox.setText("Multiple selection allowed");
            multipleBox.setValue(choiceFormElement.isMultiple());
            additionalFields.add(multipleBox);
            MaterialTextArea choiceBox = new MaterialTextArea();
            choiceBox.setPlaceholder("Comma separated list of values");
            choiceBox.setValue(StringUtils.join(choiceFormElement.getChoices(), ","));
            additionalFields.add(choiceBox);
        }
    }

    public FormElement getFormElement() {
        return formElement;
    }

    public void updateFormElement(FormElement formElement) {
        formElement.setName(name.getText());
        formElement.setDescription(description.getText());
        formElement.setFormid(formId.getText());
        if(formElement instanceof TextFormElement) {
            TextFormElement textFormElement = (TextFormElement) formElement;
            textFormElement.setMin(((MaterialIntegerBox) additionalFields.getWidget(0)).getValue());
            textFormElement.setMax(((MaterialIntegerBox) additionalFields.getWidget(1)).getValue());
        } else if(formElement instanceof ChoiceFormElement) {
            ChoiceFormElement choiceFormElement = (ChoiceFormElement) formElement;
            choiceFormElement.setMultiple(((MaterialCheckBox) additionalFields.getWidget(0)).getValue());
            choiceFormElement.setChoices(ListUtil.toList(((MaterialTextArea) additionalFields.getWidget(1)).getValue().split(",")));
        } else if(formElement instanceof DateFormElement) {
            DateFormElement dateFormElement = (DateFormElement) formElement;
            dateFormElement.setMinDate(((MaterialDatePicker) additionalFields.getWidget(0)).getValue());
            dateFormElement.setMaxDate(((MaterialDatePicker) additionalFields.getWidget(1)).getValue());
        } else if(formElement instanceof IntegerFormElement) {
            IntegerFormElement integerFormElement = (IntegerFormElement) formElement;
            integerFormElement.setMin(((MaterialIntegerBox) additionalFields.getWidget(0)).getValue());
            integerFormElement.setMax(((MaterialIntegerBox) additionalFields.getWidget(1)).getValue());
        } else if(formElement instanceof DoubleFormElement) {
            DoubleFormElement doubleFormElement = (DoubleFormElement) formElement;
            doubleFormElement.setMin(((MaterialDoubleBox) additionalFields.getWidget(0)).getValue());
            doubleFormElement.setMax(((MaterialDoubleBox) additionalFields.getWidget(1)).getValue());
        }
    }

    public HasClickHandlers getSave() {
        return save;
    }

    public HasClickHandlers getCancel() {
        return cancel;
    }

}