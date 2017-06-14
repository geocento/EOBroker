package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 02/06/2017.
 */
public class ExpandPanel extends Composite {

    interface ExpandPanelUiBinder extends UiBinder<Widget, ExpandPanel> {
    }

    private static ExpandPanelUiBinder ourUiBinder = GWT.create(ExpandPanelUiBinder.class);

    @UiField
    MaterialLink label;
    @UiField
    MaterialPanel content;
    @UiField
    MaterialPanel header;

    public ExpandPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));

        setOpen(false);

        label.addClickHandler(event -> {
            setOpen(!isOpen());
        });
    }

    public void setOpen(boolean open) {
        content.setVisible(open);
        updateDisplay();
    }

    private void updateDisplay() {
        label.setIconType(isOpen() ? IconType.KEYBOARD_ARROW_DOWN : IconType.KEYBOARD_ARROW_RIGHT);
    }

    private boolean isOpen() {
        return content.isVisible();
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public void setLabelStyle(String styleName) {
        header.setStyleName(styleName);
    }

    public void setLabelColor(Color color) {
        label.setIconColor(color);
        label.setTextColor(color);
    }

    public void setHeaderStyleNames(String headerStyleNames) {
        header.addStyleName(headerStyleNames);
    }

    public void setContentMargin(int margin) {
        content.setPaddingLeft(margin);
    }

    @UiChild(tagname = "header")
    public void setHeader(Widget widget) {
        label.setText("");
        header.add(widget);
    }

    @UiChild(tagname = "content")
    public void setContent(Widget widget) {
        content.clear();
        content.add(widget);
    }

}