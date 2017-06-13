package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 08/11/2016.
 */
public class StyleWidget extends Composite {

    interface StyleWidgetUiBinder extends UiBinder<Widget, StyleWidget> {
    }

    private static StyleWidgetUiBinder ourUiBinder = GWT.create(StyleWidgetUiBinder.class);

    @UiField
    MaterialButton select;
    @UiField
    MaterialLabel name;
    @UiField
    MaterialButton edit;
    @UiField
    MaterialPanel editPanel;
    @UiField
    MaterialPanel selectPanel;
    @UiField
    MaterialTextBox editName;
    @UiField
    MaterialButton delete;

    private String style;

    public StyleWidget(String style) {
        this.style = style;
        initWidget(ourUiBinder.createAndBindUi(this));
        updateName();
    }

    public String getStyle() {
        return style;
    }

    private void updateName() {
        String name = style == null || style.length() == 0 ? "No name" : style;
        boolean ownWorkspace = name.contains(":");
        this.name.setText(ownWorkspace ? name.substring(name.indexOf(":") + 1) : name);
        delete.setVisible(ownWorkspace);
        if(!ownWorkspace) {
            selectPanel.setBackgroundColor(Color.AMBER_LIGHTEN_5);
        }
        editName.setText(name);
    }

    public HasClickHandlers getSelect() {
        return select;
    }

    public void setEditing(boolean editing) {
        selectPanel.setVisible(!editing);
        editPanel.setVisible(editing);
    }

    @UiHandler("edit")
    void edit(ClickEvent clickEvent) {
        setEditing(true);
    }

    @UiHandler("validate")
    void validate(ClickEvent clickEvent) {
        String name = editName.getText();
        if(name.length() == 0 || name.contains(":")) {
            MaterialToast.fireToast("Please provide a valid name, only letters are allowed", Color.RED.getCssName());
            return;
        }
        // update style
        style = name;
        setEditing(false);
        // TODO - for now style name is not editable
        MaterialToast.fireToast("Cannot change style name...");
    }

    public HasClickHandlers getDelete() {
        return delete;
    }

}