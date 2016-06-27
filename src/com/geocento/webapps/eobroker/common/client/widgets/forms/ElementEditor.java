package com.geocento.webapps.eobroker.common.client.widgets.forms;

import com.geocento.webapps.eobroker.common.shared.entities.FormElement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialTooltip;

import java.util.Iterator;

/**
 * Created by thomas on 23/06/2016.
 */
public class ElementEditor<T extends FormElement> extends Composite implements HasWidgets {

    interface ElementContainerUiBinder extends UiBinder<HTMLPanel, ElementEditor> {
    }

    private static ElementContainerUiBinder ourUiBinder = GWT.create(ElementContainerUiBinder.class);
    @UiField
    HTMLPanel buttons;
    @UiField
    HTMLPanel container;
    @UiField
    MaterialTooltip information;
    @UiField
    MaterialLabel label;

    protected T formElement;

    public ElementEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setFormElement(T element) {
        this.formElement = element;
        setLabel(element.getName());
        setInformation(element.getDescription());
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public void setInformation(String information) {
        this.information.setText(information);
    }

    public void addAction(IconType edit, ClickHandler clickHandler) {
        MaterialIcon materialIcon = new MaterialIcon(edit);
        materialIcon.addClickHandler(clickHandler);
        buttons.add(materialIcon);
    }

    public T getFormElement() {
        return formElement;
    }

    @Override
    public void add(Widget w) {
        container.add(w);
    }

    @Override
    public void clear() {
        container.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return container.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        return container.remove(w);
    }

}