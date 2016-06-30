package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.FormElement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialTextArea;
import gwt.material.design.client.ui.MaterialTextBox;

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

    private T formElement;

    public FormElementEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));

    }

    public void setFormElement(T formElement) {
        this.formElement = formElement;
        name.setText(formElement.getName());
        description.setText(formElement.getDescription());
        formId.setText(formElement.getFormid());
    }

    public FormElement getFormElement() {
        return formElement;
    }

    public void updateFormElement(FormElement formElement) {
        formElement.setName(name.getText());
        formElement.setDescription(description.getText());
    }

    public HasClickHandlers getSave() {
        return save;
    }

    public HasClickHandlers getCancel() {
        return cancel;
    }

}