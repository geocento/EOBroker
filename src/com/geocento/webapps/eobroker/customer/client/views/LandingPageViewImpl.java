package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.EOBrokerPlace;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.widgets.FollowingEventWidget;
import com.geocento.webapps.eobroker.customer.shared.FollowingEventDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
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
    MaterialRow newsFeed;

    private Presenter presenter;

    private ClientFactoryImpl clientFactory;

    public LandingPageViewImpl(final ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

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
        }
        slider.initialize();
    }

    @Override
    public void setLoadingNewsFeed(boolean loading) {
        newsFeed.clear();
        if(loading) {
            newsFeed.add(new LoadingWidget("Loading..."));
        }
    }

    @Override
    public void setNewsFeed(List<FollowingEventDTO> followingEventDTOs) {
        this.newsFeed.clear();
        if(followingEventDTOs.size() == 0) {
            MaterialColumn materialColumn = new MaterialColumn(12, 12, 6);
            this.newsFeed.add(materialColumn);
            materialColumn.add(new MaterialLabel("No news in your network, follow more companies and products to be up to date with the EO Broker"));
        } else {
            for (FollowingEventDTO followingEventDTO : followingEventDTOs) {
                MaterialColumn materialColumn = new MaterialColumn(12, 12, 6);
                this.newsFeed.add(materialColumn);
                FollowingEventWidget followingEventWidget = new FollowingEventWidget(followingEventDTO);
                followingEventWidget.getAction().addClickHandler(event -> {
                    EOBrokerPlace place = null;
                    switch (followingEventDTO.getCategory()) {
                        case companies:
                            switch (followingEventDTO.getType()) {
                                case TESTIMONIAL:
                                    // TODO - replace by a full page for testimonies
                                    place = new FullViewPlace(
                                            Utils.generateTokens(
                                                    FullViewPlace.TOKENS.companyid.toString(), followingEventDTO.getCompanyDTO().getId() + "",
                                                    FullViewPlace.TOKENS.tab.toString(), "credentials"));
                                    break;
                                case OFFER:
                                    place = new FullViewPlace(
                                            Utils.generateTokens(
                                                    FullViewPlace.TOKENS.companyid.toString(), followingEventDTO.getLinkId(),
                                                    FullViewPlace.TOKENS.tab.toString(), "offering"));
                                    break;
                            }
                            break;
                        case products:
                            switch (followingEventDTO.getType()) {
                                case TESTIMONIAL:
                                    // TODO - replace by a full page for testimonies
                                    break;
                                case OFFER:
                                    place = new FullViewPlace(
                                            Utils.generateTokens(
                                                    FullViewPlace.TOKENS.productid.toString(), followingEventDTO.getLinkId(),
                                                    FullViewPlace.TOKENS.tab.toString(), "offering"));
                                    break;
                            }
                            break;
                        case productservices:
                            switch (followingEventDTO.getType()) {
                                case TESTIMONIAL:
                                    // TODO - replace by a full page for testimonies
                                    break;
                                case OFFER:
                                    place = new FullViewPlace(
                                            Utils.generateTokens(
                                                    FullViewPlace.TOKENS.productserviceid.toString(), followingEventDTO.getLinkId(),
                                                    FullViewPlace.TOKENS.tab.toString(), "description"));
                                    break;
                            }
                            break;
                    }
                    if (place != null) {
                        clientFactory.getPlaceController().goTo(place);
                    }
                });
                followingEventWidget.addStyleName(style.followingEvent());
                materialColumn.add(followingEventWidget);
            }
        }
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}