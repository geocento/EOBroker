package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 09/06/2016.
 */
public class MoreWidget extends Composite implements ResizeHandler {

    interface MoreWidgetUiBinder extends UiBinder<Widget, MoreWidget> {
    }

    private static MoreWidgetUiBinder ourUiBinder = GWT.create(MoreWidgetUiBinder.class);

    static interface Style extends CssResource {

        String description();
    }

    @UiField
    Style style;

    @UiField
    MaterialLabel description;
    @UiField
    MaterialLink more;
    @UiField
    MaterialLink less;
    @UiField
    MaterialPanel content;

    private boolean showFull;

    public MoreWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
        // TODO - check length of text first
        displayFull(false);
        more.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                toggleMore();
            }
        });
        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                onResize(null);
            }
        });
    }

    private void toggleMore() {
        displayFull(!showFull);
    }

    private void displayFull(boolean display) {
        this.showFull = display;
        content.setStyleName(style.description(), display);
        onResize(null);
    }

    public void setText(String description) {
        this.description.setText(description);
        onResize(null);
    }

    @Override
    public void onResize(ResizeEvent event) {
        // check size of text and whether it fits or not
        // use the less link for that
        boolean longText = less.isVisible();
    }

}