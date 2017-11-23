package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.events.RemoveChallenge;
import com.geocento.webapps.eobroker.admin.client.events.RemoveChallengeHandler;
import com.geocento.webapps.eobroker.admin.client.places.ChallengesPlace;
import com.geocento.webapps.eobroker.admin.client.places.ChallengePlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.ChallengesView;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.admin.shared.dtos.ChallengeDTO;
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
public class ChallengesActivity extends TemplateActivity implements ChallengesView.Presenter {

    private int start = 0;
    private int limit = 24;
    private String orderby = "";
    private String filter;

    private ChallengesView challengesView;

    public ChallengesActivity(ChallengesPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        challengesView = clientFactory.getChallengesView();
        challengesView.setPresenter(this);
        panel.setWidget(challengesView.asWidget());
        setTemplateView(challengesView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());
        // load challenges
        loadChallenges();
    }

    @Override
    protected void bind() {
        super.bind();

        activityEventBus.addHandler(RemoveChallenge.TYPE, new RemoveChallengeHandler() {
            @Override
            public void onRemoveChallenge(RemoveChallenge event) {
                try {
                    ChallengeDTO challengeDTO = event.getChallengeDTO();
                    if(Window.confirm("Are you sure you want to remove the challenge " + challengeDTO.getName() + "?")) {
                        displayLoading("Removing challenge");
                        REST.withCallback(new MethodCallback<Void>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                hideLoading();
                                displayError(method.getResponse().getText());
                            }

                            @Override
                            public void onSuccess(Method method, Void response) {
                                hideLoading();
                                displaySuccess("Challenge " + challengeDTO.getName() + " has been removed!");
                                reloadChallenges();
                            }
                        }).call(ServicesUtil.assetsService).removeChallenge(challengeDTO.getId());
                    }
                } catch (RequestException e) {
                }
            }
        });

        handlers.add(challengesView.getCreateNewButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new ChallengePlace());
            }
        }));

        handlers.add(challengesView.getImportCSV().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {

            }
        }));

    }

    private void reloadChallenges() {
        start = 0;
        loadChallenges();
    }

    private void loadChallenges() {
        if(start == 0) {
            challengesView.clearChallenges();
        }
        try {
            challengesView.setChallengesLoading(true);
            REST.withCallback(new MethodCallback<List<ChallengeDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    challengesView.setChallengesLoading(false);
                }

                @Override
                public void onSuccess(Method method, List<ChallengeDTO> response) {
                    challengesView.setChallengesLoading(false);
                    start += response.size();
                    challengesView.addChallenges(response.size() == limit, response);
                }
            }).call(ServicesUtil.assetsService).listChallenges(start, limit, orderby, filter);
        } catch (RequestException e) {
        }
    }

    @Override
    public void loadMore() {
        loadChallenges();
    }

    @Override
    public void changeFilter(String value) {
        start = 0;
        filter = value;
        loadChallenges();
    }

    @Override
    public void reload() {
        changeFilter(null);
    }

}
