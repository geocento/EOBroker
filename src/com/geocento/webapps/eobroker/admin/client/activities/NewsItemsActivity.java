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
        int start = 0;
        int limit = 25;
        if(tokens.containsKey(NewsItemsPlace.TOKENS.start.toString())) {
            try {
                start = Integer.parseInt(tokens.get(NewsItemsPlace.TOKENS.start.toString()));
            } catch (Exception e) {

            }
        }
        if(tokens.containsKey(NewsItemsPlace.TOKENS.limit.toString())) {
            try {
                limit = Integer.parseInt(tokens.get(NewsItemsPlace.TOKENS.limit.toString()));
            } catch (Exception e) {

            }
        }
        orderby = tokens.get(NewsItemsPlace.TOKENS.orderby.toString());
        // load all newsItems
        try {
            final int finalStart = start;
            final int finalLimit = limit;
            final String finalOrderby = orderby;
            REST.withCallback(new MethodCallback<List<NewsItem>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Error loading news items please try again");
                }

                @Override
                public void onSuccess(Method method, List<NewsItem> response) {
                        newsItemsView.setNewsItems(finalStart, finalLimit, finalOrderby, response);
                }
            }).call(ServicesUtil.assetsService).listNewsItems(start, limit, orderby);
        } catch (RequestException e) {
        }
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

}
