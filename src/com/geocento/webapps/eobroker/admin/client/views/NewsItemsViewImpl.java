package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.widgets.NewsItemList;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingWidgetList;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialTextBox;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class NewsItemsViewImpl extends Composite implements NewsItemsView {

    interface NewsItemsViewUiBinder extends UiBinder<Widget, NewsItemsViewImpl> {
    }

    private static NewsItemsViewUiBinder ourUiBinder = GWT.create(NewsItemsViewUiBinder.class);

    public static interface Style extends CssResource {
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    NewsItemList newsItems;
    @UiField
    MaterialButton createNew;
    @UiField
    MaterialTextBox filter;

    private Presenter presenter;

    public NewsItemsViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        newsItems.setPresenter(new AsyncPagingWidgetList.Presenter() {
            @Override
            public void loadMore() {
                presenter.loadMore();
            }
        });

        filter.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.changeFilter(event.getValue());
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void addNewsItems(boolean hasMore, List<NewsItem> newsItems) {
        this.newsItems.addData(newsItems, hasMore);
    }

    @Override
    public HasClickHandlers getCreateNewsItemButton() {
        return createNew;
    }

    @Override
    public void clearNewsItems() {
        newsItems.clearData();
    }

    @Override
    public void setNewsItemsLoading(boolean loading) {
        newsItems.setLoading(loading);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}