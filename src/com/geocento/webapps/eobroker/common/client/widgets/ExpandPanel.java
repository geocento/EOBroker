package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 02/06/2017.
 */
public class ExpandPanel extends Composite {

    interface ExpandPanelUiBinder extends UiBinder<Widget, ExpandPanel> {
    }

    private static ExpandPanelUiBinder ourUiBinder = GWT.create(ExpandPanelUiBinder.class);

    @UiField
    DisclosurePanel panel;
    @UiField
    MaterialLink label;

    public ExpandPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));

        setOpen(false);
        panel.addOpenHandler(event -> updateDisplay());
        panel.addCloseHandler(event -> updateDisplay());
    }

    public void setOpen(boolean open) {
        panel.setOpen(open);
        updateDisplay();
    }

    private void updateDisplay() {
        boolean isOpen = panel.isOpen();
        label.setIconType(isOpen ? IconType.ARROW_DOWNWARD : IconType.ARROW_FORWARD);
    }

    public void setLabel(String label) {
        this.label.setText(label);
    }

    public void setLabelStyle(String styleName) {
        this.label.getParent().setStyleName(styleName);
    }

    public void setLabelColor(Color color) {
        label.setIconColor(color);
        label.setTextColor(color);
    }

    @UiChild(tagname = "content")
    public void setContent(Widget widget) {
        panel.setContent(widget);
    }

}