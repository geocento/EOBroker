package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.TestimonialPlace;
import com.geocento.webapps.eobroker.customer.client.places.TestimonialsPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.TestimonialsView;
import com.geocento.webapps.eobroker.customer.shared.TestimonialDTO;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class TestimonialsActivity extends TemplateActivity implements TestimonialsView.Presenter {

    private TestimonialsView testimonialsView;

    public TestimonialsActivity(TestimonialsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        testimonialsView = clientFactory.getTestimonialsView();
        testimonialsView.setPresenter(this);
        setTemplateView(testimonialsView.asWidget());
        selectMenu("testimonials");
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(testimonialsView.getAddTestimonial().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new TestimonialPlace());
            }
        }));
    }

    private void handleHistory() {

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        // no need for company id
        displayFullLoading("Loading testimonials...");
        try {
            REST.withCallback(new MethodCallback<List<TestimonialDTO>>() {

                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Problem loading testimonials");
                }

                @Override
                public void onSuccess(Method method, List<TestimonialDTO> testimonialDTOs) {
                    hideFullLoading();
                    testimonialsView.displayTestimonials(testimonialDTOs);
                }

            }).call(ServicesUtil.assetsService).listTestimonials();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

}
