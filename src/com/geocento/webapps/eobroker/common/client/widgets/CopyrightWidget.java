package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;

import java.util.Date;

public class CopyrightWidget extends Composite {

    interface CopyrightWidgetUiBinder extends UiBinder<HTMLPanel, CopyrightWidget> {
    }

    private static CopyrightWidgetUiBinder ourUiBinder = GWT.create(CopyrightWidgetUiBinder.class);

    @UiField
    SpanElement copyrightYear;

    public static DateTimeFormat yearFormat = DateTimeFormat.getFormat("yyyy");

    public CopyrightWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));

        // set the copyright year to this year
        copyrightYear.setInnerText(yearFormat.format(new Date()));
    }
}