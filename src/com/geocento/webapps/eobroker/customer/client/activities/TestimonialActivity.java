package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.TestimonialPlace;
import com.geocento.webapps.eobroker.customer.client.places.TestimonialsPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.TestimonialView;
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

/**
 * Created by thomas on 09/05/2016.
 */
public class TestimonialActivity extends TemplateActivity implements TestimonialView.Presenter {

    private TestimonialView testimonialView;

    public CompanyDTO company;

    public TestimonialActivity(TestimonialPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        testimonialView = clientFactory.getTestimonialView();
        testimonialView.setPresenter(this);
        setTemplateView(testimonialView.asWidget());
        selectMenu("testimonials");
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        handlers.add(testimonialView.getCreateButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                TestimonialDTO testimonialDTO = new TestimonialDTO();
                testimonialDTO.setCompanyDTO(company);
                testimonialDTO.setTestimonial(testimonialView.getTestimonial().getHTML());
                try {
                    REST.withCallback(new MethodCallback<Long>() {

                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Could not find company");
                        }

                        @Override
                        public void onSuccess(Method method, Long id) {
                            hideLoading();
                            clientFactory.getPlaceController().goTo(new TestimonialsPlace());
                        }
                    }).call(ServicesUtil.assetsService).createTestimonial(testimonialDTO);
                } catch (RequestException e) {
                }
            }
        }));
    }

    @Override
    public void selectSuggestion(Suggestion suggestion) {
        String parameters = suggestion.getUri().split("::")[1];
        // load offer
        switch (suggestion.getCategory()) {
            case companies:
/*
                try {
                    REST.withCallback(new MethodCallback<CompanyDTO>() {

                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Could not find company");
                        }

                        @Override
                        public void onSuccess(Method method, CompanyDTO companyDTO) {
                            hideLoading();
                            //testimonialView.displayCompany(companyDescriptionDTO);
                            company = companyDTO;
                        }
                    }).call(ServicesUtil.assetsService).getCompany(Long.parseLong(parameters));
                } catch (RequestException e) {
                }
*/
                // we just need the id
                company = new CompanyDTO();
                company.setId(Long.parseLong(parameters));
                break;
        }
    }
}
