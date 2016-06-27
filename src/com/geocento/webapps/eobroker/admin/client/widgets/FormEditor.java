package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.forms.ElementEditor;
import com.geocento.webapps.eobroker.common.client.widgets.forms.TextEditor;
import com.geocento.webapps.eobroker.common.shared.entities.AoIFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.DateFormElement;
import com.geocento.webapps.eobroker.common.shared.entities.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.TextFormElement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialListBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 23/06/2016.
 */
public class FormEditor extends Composite {

    interface FormEditorUiBinder extends UiBinder<HTMLPanel, FormEditor> {
    }

    private static FormEditorUiBinder ourUiBinder = GWT.create(FormEditorUiBinder.class);

    private static enum TYPE {aoi, text, date};

    @UiField
    MaterialListBox selectElement;
    @UiField
    MaterialButton addElement;
    @UiField
    FlowPanel container;
    @UiField
    MaterialLabel comment;

    public FormEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));

        selectElement.addItem("Area of Interest", TYPE.aoi.toString());
        selectElement.addItem("Text", TYPE.text.toString());
        selectElement.addItem("Date", TYPE.date.toString());
    }

    public void setElements(List<FormElement> formElements) {
        container.clear();
        if(formElements != null) {
            for (FormElement formElement : formElements) {
                addFormElement(formElement);
            }
        }
        updateMessage();
    }

    private void updateMessage() {
        if(getElements().size() > 0) {
            comment.setVisible(false);
        } else {
            comment.setVisible(true);
            comment.setText("No elements in this form");
        }
    }

    private void addFormElement(final FormElement formElement) {
        ElementEditor editor = createEditor(formElement);
        container.add(editor);
        updateMessage();
    }

    private ElementEditor createEditor(final FormElement formElement) {
        if(formElement instanceof AoIFormElement) {

        } else if(formElement instanceof TextFormElement) {
            TextEditor textEditor = new TextEditor();
            textEditor.setFormElement((TextFormElement) formElement);
            textEditor.addAction(IconType.EDIT, new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    edit(formElement);
                }
            });
            return textEditor;
        }
        return null;
    }

    private void edit(final FormElement formElement) {
        // clean up first
        closeEditor();
        // find the form element widget
        for(int index = 0; index < container.getWidgetCount(); index++) {
            Widget widget = container.getWidget(index);
            if(widget instanceof ElementEditor && formElement == ((ElementEditor) widget).getFormElement()) {
                container.remove(widget);
                final FormElementEditor formElementEditor = new FormElementEditor();
                formElementEditor.setFormElement(formElement);
                formElementEditor.getSave().addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        formElementEditor.updateFormElement(formElement);
                        closeEditor();
                    }
                });
                formElementEditor.getCancel().addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        closeEditor();
                    }
                });
                container.insert(formElementEditor, index);
            }
        }
    }

    private void closeEditor() {
        for(int index = 0; index < container.getWidgetCount(); index++) {
            Widget widget = container.getWidget(index);
            if(widget instanceof FormElementEditor) {
                container.remove(widget);
                container.insert(createEditor(((FormElementEditor) widget).getFormElement()), index);
            }
        }
    }

    @UiHandler("addElement")
    void addElement(ClickEvent clickEvent) {
        TYPE elementType = TYPE.valueOf(selectElement.getSelectedValue());
        addFormElement(createElement(elementType));
    }

    private FormElement createElement(TYPE elementType) {
        switch(elementType) {
            case aoi:
                return new AoIFormElement();
            case text:
                return new TextFormElement();
            case date:
                return new DateFormElement();
        }
        return null;
    }

    public List<FormElement> getElements() {
        List<FormElement> elements = new ArrayList<FormElement>();
        for(int index = 0; index < container.getWidgetCount(); index++) {
            if(container.getWidget(index) instanceof ElementEditor) {
                elements.add(((ElementEditor) container.getWidget(index)).getFormElement());
            }
        }
        return elements;
    }

}