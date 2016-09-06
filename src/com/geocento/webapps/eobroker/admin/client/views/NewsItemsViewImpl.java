package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.places.NewsItemsPlace;
import com.geocento.webapps.eobroker.admin.client.places.ProductsPlace;
import com.geocento.webapps.eobroker.admin.client.widgets.NewsItemList;
import com.geocento.webapps.eobroker.admin.client.widgets.ProductsList;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingCellTable;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;

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

    private Presenter presenter;

    public NewsItemsViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        newsItems.setPresenter(new AsyncPagingCellTable.Presenter() {
            @Override
            public void rangeChanged(int start, int length, Column<?, ?> column, boolean isAscending) {
                clientFactory.getPlaceController().goTo(new NewsItemsPlace(start, length, null));
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setNewsItems(int start, int limit, String orderby, List<NewsItem> newsItemList) {
        this.newsItems.setPagerSize(limit);
        //this.newsItems.setOrderBy(orderby);
        this.newsItems.setRowData(start, newsItemList);
    }

    @Override
    public HasClickHandlers getCreateNewsItemButton() {
        return createNew;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}