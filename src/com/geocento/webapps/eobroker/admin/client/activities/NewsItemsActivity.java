package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.NewsItemPlace;
import com.geocento.webapps.eobroker.admin.client.places.NewsItemsPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.NewsItemsView;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
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
public class NewsItemsActivity extends TemplateActivity implements NewsItemsView.Presenter {

    private int start = 0;
    private int limit = 10;
    private String orderby = "";
    private String filter;

    private NewsItemsView newsItemsView;

    public NewsItemsActivity(NewsItemsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        newsItemsView = clientFactory.getNewsItemsView();
        newsItemsView.setPresenter(this);
        panel.setWidget(newsItemsView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        String orderby = null;
        orderby = tokens.get(NewsItemsPlace.TOKENS.orderby.toString());
        // load news items
        loadNewsItems();
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(newsItemsView.getCreateNewsItemButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new NewsItemPlace());
            }
        }));
    }


    private void loadNewsItems() {
        if(start == 0) {
            newsItemsView.clearNewsItems();
        }
        try {
            newsItemsView.setNewsItemsLoading(true);
            REST.withCallback(new MethodCallback<List<NewsItem>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    newsItemsView.setNewsItemsLoading(false);
                }

                @Override
                public void onSuccess(Method method, List<NewsItem> response) {
                    newsItemsView.setNewsItemsLoading(false);
                    start += response.size();
                    newsItemsView.addNewsItems(response.size() == limit, response);
                }
            }).call(ServicesUtil.assetsService).listNewsItems(start, limit, orderby, filter);
        } catch (RequestException e) {
        }
    }

    @Override
    public void loadMore() {
        loadNewsItems();
    }

    @Override
    public void changeFilter(String value) {
        start = 0;
        filter = value;
        loadNewsItems();
    }

}