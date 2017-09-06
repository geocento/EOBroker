package com.geocento.webapps.eobroker.admin.client.activities;

import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.places.NewsItemPlace;
import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.admin.client.views.NewsItemView;
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

/**
 * Created by thomas on 09/05/2016.
 */
public class NewsItemActivity extends TemplateActivity implements NewsItemView.Presenter {

    private NewsItemView newsItemView;

    private NewsItem newsItem;

    public NewsItemActivity(NewsItemPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        newsItemView = clientFactory.getNewsItemView();
        newsItemView.setPresenter(this);
        panel.setWidget(newsItemView.asWidget());
        setTemplateView(newsItemView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        Long newsItemId = null;
        if(tokens.containsKey(NewsItemPlace.TOKENS.id.toString())) {
            newsItemId = Long.parseLong(tokens.get(NewsItemPlace.TOKENS.id.toString()));
        }
        if(newsItemId != null) {
            // load all newsItems
            try {
                REST.withCallback(new MethodCallback<NewsItem>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, NewsItem response) {
                        setNewsItem(response);
                    }
                }).call(ServicesUtil.assetsService).getNewsItem(newsItemId);
            } catch (RequestException e) {
            }
        } else {
            NewsItem newsItem = new NewsItem();
            setNewsItem(newsItem);
        }
    }

    private void setNewsItem(NewsItem newsItem) {
        this.newsItem = newsItem;
        newsItemView.setPageTitle(newsItem.getId() == null ? "Create news item" : "Edit news item");
        newsItemView.getNewsItemTitle().setText(newsItem.getTitle());
        newsItemView.setImageUrl(newsItem.getImageUrl());
        newsItemView.getDescription().setText(newsItem.getDescription());
        newsItemView.getWebsiteUrl().setText(newsItem.getWebsiteUrl());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(newsItemView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                newsItem.setTitle(newsItemView.getNewsItemTitle().getText());
                newsItem.setImageUrl(newsItemView.getImageUrl());
                newsItem.setDescription(newsItemView.getDescription().getText());
                newsItem.setWebsiteUrl(newsItemView.getWebsiteUrl().getText());
                newsItemView.setLoading("Saving newsItem...");
                try {
                    REST.withCallback(new MethodCallback<Long>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            newsItemView.setLoadingError("Error saving newsItem");
                        }

                        @Override
                        public void onSuccess(Method method, Long newsItemId) {
                            newsItemView.hideLoading("NewsItem saved");
                            newsItem.setId(newsItemId);
                        }
                    }).call(ServicesUtil.assetsService).saveNewsItem(newsItem);
                } catch (RequestException e) {
                }
            }
        }));
    }

}
