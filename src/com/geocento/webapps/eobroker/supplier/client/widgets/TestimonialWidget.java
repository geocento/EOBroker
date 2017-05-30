package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLabelIcon;
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
import gwt.material.design.client.ui.MaterialPanel;

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
    @UiField
    MaterialLabelIcon topic;
    @UiField
    MaterialLabel userName;
    @UiField
    MaterialPanel userPanel;

    public TestimonialWidget(TestimonialDTO testimonialDTO) {

        initWidget(ourUiBinder.createAndBindUi(this));

        if(testimonialDTO.getFromUser() != null) {
            user.setUser(testimonialDTO.getFromUser().getName());
            userName.setText(testimonialDTO.getFromUser() == null ? "" : "From user '" + testimonialDTO.getFromUser().getName() + "'");
        } else {
            displayUser(false);
        }
        // TODO - check for offerings
        if(testimonialDTO.getCompanyDTO() != null) {
            topic.setImageUrl(testimonialDTO.getCompanyDTO().getIconURL());
            topic.setText("On company " + testimonialDTO.getCompanyDTO().getName());
        } else {
            displayTopic(false);
        }
/*
        topic.add(new HTML("On company " +
                "<img style='max-height: 24px; vertical-align: middle;' src='" + testimonialDTO.getCompanyDTO().getIconURL() + "'/> " +
                testimonialDTO.getCompanyDTO().getName()));
*/
        testimonial.add(new HTML("<i>" + testimonialDTO.getTestimonial() + "</i>"));
        creationDate.setText("written on " + DateUtils.dateFormat.format(testimonialDTO.getCreationDate()));
    }

    public void displayUser(boolean display) {
        userPanel.setVisible(display);
    }

    public void displayTopic(boolean display) {
        topic.setVisible(display);
    }
}