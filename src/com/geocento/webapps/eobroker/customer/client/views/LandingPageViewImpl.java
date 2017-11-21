package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingWidgetList;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialMessage;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.widgets.FollowingEventsList;
import com.geocento.webapps.eobroker.customer.shared.FollowingEventDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.TextAlign;
import gwt.material.design.client.ui.*;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class LandingPageViewImpl extends Composite implements LandingPageView {

    interface LandingPageUiBinder extends UiBinder<Widget, LandingPageViewImpl> {
    }

    private static LandingPageUiBinder ourUiBinder = GWT.create(LandingPageUiBinder.class);

    public interface Style extends CssResource {

        String followingEvent();
    }

    @UiField
    Style style;

    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.MaterialSlider slider;
    @UiField
    FollowingEventsList followingEvents;
    @UiField
    MaterialMessage followingMessage;

    private Presenter presenter;

    private ClientFactoryImpl clientFactory;

    public LandingPageViewImpl(final ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        followingEvents.setPresenter(new AsyncPagingWidgetList.Presenter() {
            @Override
            public void loadMore() {
                if (presenter != null) {
                    presenter.loadMoreFollowingEvents();
                }
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displaySearchError(String message) {
        MaterialToast.fireToast(message);
    }

    @Override
    public void setNewsItems(List<NewsItem> newsItems) {
        slider.clear();
        for(NewsItem newsItem : newsItems) {
            MaterialSlideItem materialSlideItem = new MaterialSlideItem();
            materialSlideItem.add(new MaterialImage(URL.encode(newsItem.getImageUrl())));
            MaterialSlideCaption materialSlideCaption = new MaterialSlideCaption();
            materialSlideCaption.setTextAlign(TextAlign.CENTER);
            materialSlideCaption.add(new MaterialTitle(newsItem.getTitle(), newsItem.getDescription()));
            materialSlideItem.add(materialSlideCaption);
            slider.add(materialSlideItem);
            if(newsItem.getWebsiteUrl() != null) {
                materialSlideCaption.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
                materialSlideCaption.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Window.open(newsItem.getWebsiteUrl(), "_blank;", null);
                    }
                });
            }
        }
        slider.initialize();
    }

    @Override
    public void setLoadingFollowingEvents(boolean loading) {
        followingEvents.setLoading(loading);
    }

    @Override
    public void addNewsFollowingEvents(boolean hasMore, List<FollowingEventDTO> followingEventDTOs) {
        followingEvents.addData(followingEventDTOs, hasMore);
    }

    @Override
    public void clearNewsFeed() {
        this.followingEvents.clearData();
    }

    @Override
    public void displayFollowingMessage(String message) {
        followingMessage.setVisible(true);
        followingMessage.setText(message);
    }

    @Override
    public void hideFollowingMessage() {
        followingMessage.setVisible(false);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}