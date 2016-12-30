package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.RequestsPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.RequestsView;
import com.geocento.webapps.eobroker.common.shared.entities.requests.RequestDTO;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class RequestsActivity extends TemplateActivity implements RequestsView.Presenter {

    private RequestsView ordersView;

    public RequestsActivity(RequestsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        ordersView = clientFactory.getOrdersView();
        ordersView.setPresenter(this);
        panel.setWidget(ordersView.asWidget());
        setTemplateView(ordersView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        // load all current requests
        try {
            REST.withCallback(new MethodCallback<List<RequestDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Failed to load your requests");
                }

                @Override
                public void onSuccess(Method method, List<RequestDTO> requestDTOs) {
                    ordersView.setRequests(requestDTOs);
                }
            }).call(ServicesUtil.requestsService).getRequests(Request.STATUS.submitted);
        } catch (RequestException e) {
        }
    }

}
