package com.geocento.webapps.eobroker.admin.client.events;

import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 31/08/2016.
 */
public class RemoveNewsItem extends GwtEvent<RemoveNewsItemHandler> {
    public static Type<RemoveNewsItemHandler> TYPE = new Type<RemoveNewsItemHandler>();

    private final NewsItem newsItem;

    public RemoveNewsItem(NewsItem element) {
        this.newsItem = element;
    }

    public NewsItem getNewsItem() {
        return newsItem;
    }

    public Type<RemoveNewsItemHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveNewsItemHandler handler) {
        handler.onRemoveNewsItem(this);
    }
}
