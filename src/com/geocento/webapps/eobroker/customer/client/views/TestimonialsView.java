package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.shared.TestimonialDTO;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.IsWidget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public interface TestimonialsView extends IsWidget {

    void setPresenter(Presenter presenter);

    void displayTestimonials(List<TestimonialDTO> testimonialDTOs);

    HasClickHandlers getAddTestimonial();

    public interface Presenter {
    }

}
