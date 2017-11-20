package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveSuccessStory;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveSuccessStoryHandler;
import com.geocento.webapps.eobroker.supplier.client.places.SuccessStoriesPlace;
import com.geocento.webapps.eobroker.supplier.client.places.SuccessStoryPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.DashboardView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SuccessStoryDTO;
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
public class SuccessStoriesActivity extends TemplateActivity implements DashboardView.Presenter {

    private DashboardView dashboardView;

    public SuccessStoriesActivity(SuccessStoriesPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        dashboardView = clientFactory.getDashboardView();
        dashboardView.setPresenter(this);
        setTemplateView(dashboardView.getTemplateView());
        panel.setWidget(dashboardView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        // no need for company id
        loadSuccessStories();

    }

    private void loadSuccessStories() {
        displayFullLoading("Loading success stories...");
        try {
            REST.withCallback(new MethodCallback<List<SuccessStoryDTO>>() {

                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Problem loading company information");
                }

                @Override
                public void onSuccess(Method method, List<SuccessStoryDTO> successStoryDTOs) {
                    hideFullLoading();
                    dashboardView.displaySuccessStories(successStoryDTOs);
                }

            }).call(ServicesUtil.assetsService).getSuccessStories();
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void bind() {
        super.bind();

        activityEventBus.addHandler(RemoveSuccessStory.TYPE, new RemoveSuccessStoryHandler() {
            @Override
            public void onRemoveSuccessStory(RemoveSuccessStory event) {
                if (Window.confirm("Are you sure you want to remove this success story?")) {
                    displayLoading("Removing success story...");
                    try {
                        REST.withCallback(new MethodCallback<Void>() {

                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                hideLoading();
                                Window.alert("Problem removing success story");
                            }

                            @Override
                            public void onSuccess(Method method, Void result) {
                                hideLoading();
                                loadSuccessStories();
                            }

                        }).call(ServicesUtil.assetsService).removeSuccessStory(event.getId());
                    } catch (RequestException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        handlers.add(dashboardView.getAddSuccessStory().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new SuccessStoryPlace());
            }
        }));

    }

}
