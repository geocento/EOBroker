package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.events.RemoveNewsItem;
import com.geocento.webapps.eobroker.admin.client.places.NewsItemPlace;
import com.geocento.webapps.eobroker.admin.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 09/06/2016.
 */
public class NewsItemWidget extends Composite {

    interface NewsItemWidgetUiBinder extends UiBinder<Widget, NewsItemWidget> {
    }

    private static NewsItemWidgetUiBinder ourUiBinder = GWT.create(NewsItemWidgetUiBinder.class);

    @UiField
    MaterialImage image;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLink edit;
    @UiField
    MaterialLink remove;

    public NewsItemWidget(final NewsItem newsItem) {
        initWidget(ourUiBinder.createAndBindUi(this));
        image.setUrl(newsItem.getImageUrl() == null || newsItem.getImageUrl().length() == 0 ? "./images/noImage.png" : newsItem.getImageUrl());
        title.setText(newsItem.getTitle());
        description.setText(newsItem.getDescription());
        edit.setHref("#" + PlaceHistoryHelper.convertPlace(new NewsItemPlace(newsItem.getId())));
        remove.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(Window.confirm("Are you sure you want to remove this new items?")) {
                    Admin.clientFactory.getEventBus().fireEvent(new RemoveNewsItem(newsItem));
                }
            }
        });
    }
}