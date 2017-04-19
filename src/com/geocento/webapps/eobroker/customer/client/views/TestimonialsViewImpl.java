package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.widgets.TestimonialWidget;
import com.geocento.webapps.eobroker.customer.shared.TestimonialDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class TestimonialsViewImpl extends Composite implements TestimonialsView {

    private Presenter presenter;

    interface DummyUiBinder extends UiBinder<Widget, TestimonialsViewImpl> {
    }

    private static DummyUiBinder ourUiBinder = GWT.create(DummyUiBinder.class);

    @UiField
    MaterialButton addTestimonial;
    @UiField
    MaterialPanel testimonials;

    public TestimonialsViewImpl(ClientFactoryImpl clientFactory) {

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayTestimonials(List<TestimonialDTO> testimonialDTOs) {
        this.testimonials.clear();
        if(testimonialDTOs == null || testimonialDTOs.size() == 0) {
            this.testimonials.add(new MaterialLabel("No testimonials, create new testimonial using the add button"));
            return;
        }
        MaterialRow materialRow = new MaterialRow();
        this.testimonials.add(materialRow);
        for(TestimonialDTO testimonialDTO : testimonialDTOs) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
            materialColumn.add(new TestimonialWidget(testimonialDTO));
            materialRow.add(materialColumn);
        }
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public HasClickHandlers getAddTestimonial() {
        return addTestimonial;
    }

}