package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.*;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.widgets.ProductDatasetWidget;
import com.geocento.webapps.eobroker.customer.client.widgets.ProductServiceWidget;
import com.geocento.webapps.eobroker.customer.client.widgets.SoftwareWidget;
import com.geocento.webapps.eobroker.customer.shared.Offer;
import com.geocento.webapps.eobroker.customer.shared.ProductDatasetDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.shared.SoftwareDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
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

    private Presenter presenter;

    interface LandingPageUiBinder extends UiBinder<Widget, LandingPageViewImpl> {
    }

    private static LandingPageUiBinder ourUiBinder = GWT.create(LandingPageUiBinder.class);

    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.MaterialSlider slider;
    @UiField
    MaterialRow offers;

    private ClientFactoryImpl clientFactory;

    public LandingPageViewImpl(final ClientFactoryImpl clientFactory) {

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
    public void setLoadingOffers(boolean loading) {
        offers.clear();
        if(loading) {
            offers.add(new LoadingWidget("Loading..."));
        }
    }

    @Override
    public void setOffers(List<Offer> offers) {
        this.offers.clear();
        for(Offer offer : offers) {
            MaterialColumn materialColumn = new MaterialColumn(6, 4, 3);
            this.offers.add(materialColumn);
            if(offer instanceof ProductServiceDTO) {
                materialColumn.add(new ProductServiceWidget((ProductServiceDTO) offer));
            } else if(offer instanceof ProductDatasetDTO) {
                materialColumn.add(new ProductDatasetWidget((ProductDatasetDTO) offer));
            } else if(offer instanceof SoftwareDTO) {
                materialColumn.add(new SoftwareWidget((SoftwareDTO) offer));
            }
        }
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}