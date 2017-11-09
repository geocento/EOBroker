package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.UserWidget;
import com.geocento.webapps.eobroker.supplier.shared.dtos.EndorsementDTO;
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
public class EndorsementWidget extends Composite {

    interface TestimonialWidgetUiBinder extends UiBinder<Widget, EndorsementWidget> {
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
    MaterialLabel userName;
    @UiField
    MaterialPanel userPanel;

    public EndorsementWidget(EndorsementDTO endorsementDTO) {

        initWidget(ourUiBinder.createAndBindUi(this));

        user.setUser(endorsementDTO.getFromUser().getName());
        userName.setText(endorsementDTO.getFromUser() == null ? "" : "From user '" + endorsementDTO.getFromUser().getName() + "'");
        testimonial.add(new HTML("<i>" + endorsementDTO.getTestimonial() + "</i>"));
        creationDate.setText("written on " + DateUtils.dateFormat.format(endorsementDTO.getCreationDate()));
    }

}