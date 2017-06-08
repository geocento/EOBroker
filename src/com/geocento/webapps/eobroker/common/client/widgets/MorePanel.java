package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 02/06/2017.
 */
public class MorePanel extends Composite {

    interface MorePanelUiBinder extends UiBinder<Widget, MorePanel> {
    }

    private static MorePanelUiBinder ourUiBinder = GWT.create(MorePanelUiBinder.class);

    @UiField
    MaterialPanel content;
    @UiField
    MaterialLabel moreLabel;
    @UiField
    HTMLPanel moreBackground;

    private int maxHeight;

    private boolean fullContentDisplayed;

    public MorePanel() {
        this(50);
    }

    public MorePanel(int maxHeight) {

        initWidget(ourUiBinder.createAndBindUi(this));

        this.maxHeight = maxHeight;

        displayMoreLabel(false);
        displayMoreContent(true);

        moreLabel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                displayMoreContent(!isFullContentDisplayed());
            }
        }, ClickEvent.getType());
    }

    private void updateMoreDisplay() {
        displayMoreLabel(content.getOffsetHeight() > maxHeight);
    }

    private void displayMoreLabel(boolean display) {
        moreBackground.setVisible(display);
        moreLabel.setVisible(display);
    }

    public boolean isFullContentDisplayed() {
        return fullContentDisplayed;
    }

    private void displayMoreContent(boolean display) {
        fullContentDisplayed = display;
        moreLabel.setText(fullContentDisplayed ? "Show less..." : "Show more...");
        if(fullContentDisplayed) {
            content.getElement().getStyle().clearProperty("maxHeight");
        } else {
            content.getElement().getStyle().setProperty("maxHeight", maxHeight + "px");
        }
    }

    @UiChild(tagname = "content")
    public void setContent(IsWidget w) {
        content.clear();
        content.add(w);
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                updateMoreDisplay();
                displayMoreContent(false);
            }
        });
    }

}