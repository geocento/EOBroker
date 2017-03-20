package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.NOTIFICATION_DELAY;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.*;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialLabel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 14/03/2017.
 */
public class FormCreator extends Composite {

    interface FormEditorUiBinder extends UiBinder<HTMLPanel, FormCreator> {
    }

    private static FormEditorUiBinder ourUiBinder = GWT.create(FormEditorUiBinder.class);

    public static interface Style extends CssResource {

        String editor();

        String title();
    }

    @UiField
    Style style;

    @UiField
    HTMLPanel panel;

    public FormCreator() {
        initWidget(ourUiBinder.createAndBindUi(this));

    }

    public void addTitle(String title) {
        MaterialLabel materialLabel = new MaterialLabel(title);
        materialLabel.addStyleName(style.title());
        panel.add(materialLabel);
    }

    public void addTextEditor(String value, String name, String description, int min, int max) {
        TextFormElement textFormElement = new TextFormElement();
        textFormElement.setName(name);
        textFormElement.setDescription(description);
        textFormElement.setMin(min);
        textFormElement.setMax(max);
        ElementEditor editor = createEditor(textFormElement);
        // TODO - check this is OK
        if(value != null) {
            editor.setFormElementValue(value.toString());
        }
    }

    public void addIntegerEditor(Integer value, String name, String description) {
        IntegerFormElement integerFormElement = new IntegerFormElement();
        integerFormElement.setName(name);
        integerFormElement.setDescription(description);
        ElementEditor editor = createEditor(integerFormElement);
        // TODO - check this is OK
        if(value != null) {
            editor.setFormElementValue(value.toString());
        }
    }

    public void addBooleanEditor(Boolean value, String name, String description) {
        BooleanFormElement booleanFormElement = new BooleanFormElement();
        booleanFormElement.setName(name);
        booleanFormElement.setDescription(description);
        ElementEditor editor = createEditor(booleanFormElement);
        // TODO - check this is OK
        if(value != null) {
            editor.setFormElementValue(value.toString());
        }
    }

    public void addChoiceEnumEditor(String[] values, String value, String name, String description) {
        ChoiceFormElement choiceFormElement = new ChoiceFormElement();
        choiceFormElement.setName(name);
        choiceFormElement.setDescription(description);
        choiceFormElement.setChoices(ListUtil.toList(values));
        ElementEditor editor = createEditor(choiceFormElement);
        // TODO - check this is OK
        if(value != null) {
            editor.setFormElementValue(value.toString());
        }
    }

    public <T extends Enum<T>> void addChoiceEnumEditor(T[] values, T value, String name, String description) {
        addChoiceEnumEditor(Utils.enumNameToStringArray(NOTIFICATION_DELAY.values()),
                value == null ? null : value.toString(), name, description);
    }

    private ElementEditor createEditor(FormElement formElement) {
        ElementEditor editor = FormHelper.createEditor(formElement);
        editor.addStyleName(style.editor());
        panel.add(editor);
        return editor;
    }

    public List<FormElementValue> getFormElementValues() throws Exception {
        List<FormElementValue> formElementValues = new ArrayList<FormElementValue>();
        for(int index = 0; index < panel.getWidgetCount(); index++) {
            Widget widget = panel.getWidget(index);
            if(widget instanceof ElementEditor) {
                formElementValues.add(((ElementEditor) widget).getFormElementValue());
            }
        }
        return formElementValues;
    }

    public void clear() {
        panel.clear();
    }

}