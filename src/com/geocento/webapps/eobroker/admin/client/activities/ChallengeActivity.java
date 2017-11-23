package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.ChallengePlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.ChallengeView;
import com.geocento.webapps.eobroker.admin.shared.dtos.ChallengeDTO;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
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
public class ChallengeActivity extends TemplateActivity implements ChallengeView.Presenter {

    private ChallengeView challengeView;

    private ChallengeDTO challenge;

    public ChallengeActivity(ChallengePlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        challengeView = clientFactory.getChallengeView();
        challengeView.setPresenter(this);
        panel.setWidget(challengeView.asWidget());
        setTemplateView(challengeView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        Long challengeId = null;
        if(tokens.containsKey(ChallengePlace.TOKENS.id.toString())) {
            challengeId = Long.parseLong(tokens.get(ChallengePlace.TOKENS.id.toString()));
        }
        if(challengeId != null) {
            try {
                REST.withCallback(new MethodCallback<ChallengeDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, ChallengeDTO response) {
                        setChallenge(response);
                    }
                }).call(ServicesUtil.assetsService).getChallenge(challengeId);
            } catch (RequestException e) {
            }
        } else {
            ChallengeDTO challenge = new ChallengeDTO();
            setChallenge(challenge);
        }
    }

    private void setChallenge(ChallengeDTO challenge) {
        this.challenge = challenge;
        challengeView.setTitleLine(challenge.getId() == null ? "Create challenge" : "Edit challenge");
        challengeView.getName().setText(challenge.getName());
        challengeView.setIconUrl(challenge.getImageUrl());
        challengeView.getDescription().setText(challenge.getShortDescription());
        challengeView.setFullDescription(challenge.getDescription());
        challengeView.setProducts(challenge.getProductDTOs());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(challengeView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                challenge.setName(challengeView.getName().getText());
                challenge.setShortDescription(challengeView.getDescription().getText());
                challenge.setDescription(challengeView.getFullDescription());
                challengeView.setLoading("Saving challenge...");
                try {
                    REST.withCallback(new MethodCallback<Long>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            challengeView.setLoadingError("Error saving challenge");
                        }

                        @Override
                        public void onSuccess(Method method, Long challengeId) {
                            challengeView.hideLoading("Challenge saved");
                            challenge.setId(challengeId);
                        }
                    }).call(ServicesUtil.assetsService).saveChallenge(challenge);
                } catch (RequestException e) {
                }
            }
        }));
    }

}
