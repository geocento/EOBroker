package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.SuccessStoryPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.SuccessStoryView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SuccessStoryEditDTO;
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
public class SuccessStoryActivity extends TemplateActivity implements SuccessStoryView.Presenter {

    private SuccessStoryView successStoryView;

    private SuccessStoryEditDTO successStoryEditDTO;

    public SuccessStoryActivity(SuccessStoryPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        successStoryView = clientFactory.getSuccessStoryView();
        successStoryView.setPresenter(this);
        setTemplateView(successStoryView.getTemplateView());
        panel.setWidget(successStoryView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        successStoryView.setTitleLine("Edit your success story information");
        Long successStoryId = null;
        if(tokens.containsKey(SuccessStoryPlace.TOKENS.id.toString())) {
            try {
                successStoryId = Long.parseLong(tokens.get(SuccessStoryPlace.TOKENS.id.toString()));
            } catch (Exception e) {
            }
        }
        if(successStoryId != null) {
            try {
                displayFullLoading("Loading success story...");
                REST.withCallback(new MethodCallback<SuccessStoryEditDTO>() {

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideFullLoading();
                        Window.alert("Problem loading success story");
                    }

                    @Override
                    public void onSuccess(Method method, SuccessStoryEditDTO successStoryEditDTO) {
                        hideFullLoading();
                        setSuccessStory(successStoryEditDTO);
                    }

                }).call(ServicesUtil.assetsService).getSuccessStory(successStoryId);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } else {
            setSuccessStory(new SuccessStoryEditDTO());
        }
    }

    private void setSuccessStory(SuccessStoryEditDTO successStoryEditDTO) {
        this.successStoryEditDTO = successStoryEditDTO;
        successStoryView.getName().setText(successStoryEditDTO.getName());
        successStoryView.getDescription().setText(successStoryEditDTO.getDescription());
        successStoryView.setFullDescription(successStoryEditDTO.getFullDescription());
        successStoryView.setIconUrl(successStoryEditDTO.getImageUrl());
        successStoryView.setDate(successStoryEditDTO.getDate());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(successStoryView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                successStoryEditDTO.setName(successStoryView.getName().getText());
                successStoryEditDTO.setDescription(successStoryView.getDescription().getText());
                successStoryEditDTO.setFullDescription(successStoryView.getFullDescription());
                try {
                    displayLoading("Saving success story...");
                    REST.withCallback(new MethodCallback<Void>() {

                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Error saving success story...");
                        }

                        @Override
                        public void onSuccess(Method method, Void result) {
                            hideLoading();
                            displaySuccess("Success story saved");
                        }

                    }).call(ServicesUtil.assetsService).updateSuccessStory(successStoryEditDTO);
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));

        handlers.add(successStoryView.getViewClient().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.alert("Not implemented yet");
/*
                // TODO - how do we get to use the place instead?
                Window.open(GWT.getHostPageBaseURL() + "#fullview:softwareid=" + softwareDTO.getId(), "_fullview;", null);
*/
            }
        }));
    }

}
