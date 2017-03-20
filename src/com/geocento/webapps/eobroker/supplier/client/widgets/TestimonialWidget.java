package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.UserWidget;
import com.geocento.webapps.eobroker.supplier.shared.dtos.TestimonialDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.bubble.MaterialBubble;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by thomas on 14/03/2017.
 */
public class TestimonialWidget extends Composite {

    interface TestimonialWidgetUiBinder extends UiBinder<Widget, TestimonialWidget> {
    }

    private static TestimonialWidgetUiBinder ourUiBinder = GWT.create(TestimonialWidgetUiBinder.class);

    @UiField
    MaterialBubble panel;
    @UiField
    HTMLPanel testimonial;
    @UiField
    MaterialLabel creationDate;
    @UiField
    UserWidget user;

    public TestimonialWidget(TestimonialDTO testimonialDTO) {
        initWidget(ourUiBinder.createAndBindUi(this));

        user.setUser(testimonialDTO.getFromUser().getName());
        testimonial.add(new HTML("<b>" + testimonialDTO.getFromUser().getName() + "</b> " +
                "<i>" + testimonialDTO.getTestimonial() + "</i>"));
        creationDate.setText(DateUtils.dateFormat.format(testimonialDTO.getCreationDate()));
    }
}