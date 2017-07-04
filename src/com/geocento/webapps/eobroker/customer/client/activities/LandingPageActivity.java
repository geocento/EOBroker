package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.places.LandingPagePlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.LandingPageView;
import com.geocento.webapps.eobroker.customer.shared.FollowingEventDTO;
import com.google.gwt.event.shared.EventBus;
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
public class LandingPageActivity extends TemplateActivity implements LandingPageView.Presenter {

    private LandingPageView landingPageView;

    private int start = 0;
    private int limit = 10;

    public LandingPageActivity(LandingPagePlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        landingPageView = clientFactory.getLandingPageView();
        landingPageView.setPresenter(this);
        setTemplateView(landingPageView.asWidget());
        selectMenu("home");
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

        // load the page's content
        loadNewsItems();
        loadFollowingEvents();
        //loadRecommendations();
    }

    private void loadFollowingEvents() {
        if(start == 0) {
            landingPageView.clearNewsFeed();
        }
        landingPageView.setLoadingFollowingEvents(true);
        try {
            REST.withCallback(new MethodCallback<List<FollowingEventDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    landingPageView.setLoadingFollowingEvents(false);
                }

                @Override
                public void onSuccess(Method method, List<FollowingEventDTO> followingEventDTOs) {
                    landingPageView.setLoadingFollowingEvents(false);
                    start += followingEventDTOs.size();
                    landingPageView.addNewsFollowingEvents(followingEventDTOs.size() == limit, followingEventDTOs);
                }
            }).call(ServicesUtil.assetsService).getFollowingEvents(start, limit);
        } catch (Exception e) {

        }
    }

    private void loadNewsItems() {
        REST.withCallback(new MethodCallback<List<NewsItem>>() {
            @Override
            public void onFailure(Method method, Throwable exception) {
                Window.alert("Error loading news items please reload");
            }

            @Override
            public void onSuccess(Method method, List<NewsItem> newsItems) {
                NewsItem newsItem = new NewsItem();
                newsItem.setTitle("Welcome to the EO Broker portal");
                newsItem.setDescription("The marketplace for images and services tailored to the Oil and Gas industry");
                newsItem.setImageUrl("http://www.ogeo-portal.eu/images/1440.jpg");
                newsItems.add(0, newsItem);
                landingPageView.setNewsItems(newsItems);
            }
        }).call(ServicesUtil.assetsService).getNewsItems();
    }

    @Override
    public void loadMoreFollowingEvents() {
        loadFollowingEvents();
    }
}
