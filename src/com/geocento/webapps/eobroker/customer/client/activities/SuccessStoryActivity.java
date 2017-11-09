package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.SuccessStoryPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.SuccessStoryView;
import com.geocento.webapps.eobroker.customer.shared.SuccessStoryDTO;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class SuccessStoryActivity extends TemplateActivity implements SuccessStoryView.Presenter {

    private SuccessStoryView successStoryView;

    public CompanyDTO company;

    public SuccessStoryActivity(SuccessStoryPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        successStoryView = clientFactory.getSuccessStoryView();
        successStoryView.setPresenter(this);
        setTemplateView(successStoryView.asWidget());
        selectMenu("successStory");
        setTitleText("Success story");
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    @Override
    protected void bind() {
        super.bind();
    }

    private void handleHistory() {

        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        Long successStoryId = null;
        try {
            successStoryId = Long.parseLong(tokens.get(SuccessStoryPlace.TOKENS.id.toString()));
        } catch (Exception e) {

        }
        if(successStoryId == null) {
            Window.alert("Success story ID cannot be null");
            History.back();
        }

        try {
            REST.withCallback(new MethodCallback<SuccessStoryDTO>() {

                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideLoading();
                    displayError("Could not find company");
                }

                @Override
                public void onSuccess(Method method, SuccessStoryDTO successStoryDTO) {
                    hideLoading();
                    successStoryView.displaySuccessStory(successStoryDTO);
                }
            }).call(ServicesUtil.assetsService).getSuccessStory(successStoryId);
        } catch (RequestException e) {
        }
    }

}
