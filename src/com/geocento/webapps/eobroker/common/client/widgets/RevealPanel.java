package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import gwt.material.design.addins.client.iconmorph.MaterialIconMorph;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 23/12/2016.
 */
public class RevealPanel extends Composite {

    interface RevealPanelUiBinder extends UiBinder<MaterialPanel, RevealPanel> {
    }

    private static RevealPanelUiBinder ourUiBinder = GWT.create(RevealPanelUiBinder.class);

    @UiField
    MaterialIcon downwardArrow;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialPanel content;
    @UiField
    MaterialIcon forwardArrow;
    @UiField
    MaterialIconMorph switchIcon;

    public RevealPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @UiHandler("forwardArrow")
    void clickDisclose(ClickEvent clickEvent) {
        disclose(true);
    }

    @UiHandler("downwardArrow")
    void clickHide(ClickEvent clickEvent) {
        disclose(true);
    }

    private void disclose(boolean display) {
        content.setVisible(display);
    }
}