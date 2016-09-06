package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.events.RemoveNewsItem;
import com.geocento.webapps.eobroker.admin.client.places.NewsItemPlace;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.HTML;

/**
 * Created by thomas on 31/08/2016.
 */
public class NewsItemList extends ElementList<NewsItem> {

    @Override
    protected Column<NewsItem, NewsItem> createElementCell() {
        return new Column<NewsItem, NewsItem>(new AbstractCell<NewsItem>() {
            @Override
            public void render(Context context, NewsItem value, SafeHtmlBuilder sb) {
                // Value can be null, so do a null check..
                if (value == null) {
                    return;
                }

                sb.appendHtmlConstant("<table style='width: 100%; background: #ffffff; border: #eee 1px solid; padding: 0px; margin: 0px;'>");

                // Add the contact image.
                sb.appendHtmlConstant("<tr><td style='width: 60px;'>");
                sb.appendHtmlConstant(new HTML("<img width='50px' height='50px' src=\"" + value.getImageUrl() + "\" />").getHTML());
                sb.appendHtmlConstant("</td>");

                // Add the name and address.
                sb.appendHtmlConstant("<td style='font-size:95%; text-align:'><div>");
                sb.append(SafeHtmlUtils.fromTrustedString("<a style='font-size: 1em; font-weight: bold; color: #333;' target='_blank' href='" + value.getWebsiteUrl() + "'>"));
                sb.appendEscaped(value.getTitle());
                sb.appendHtmlConstant("</a></div><div style='font-size: 0.9em; font-style: italic; color: #333; height: 30px; overflow: hidden; text-overflow: ellipsis; margin-top: 5px;'>");
                sb.appendEscaped(value.getDescription());
                sb.appendHtmlConstant("</div></td></tr></table>");
            }
        }) {
            @Override
            public NewsItem getValue(NewsItem object) {
                return object;
            }
        };
    }

    @Override
    protected void removeElement(NewsItem element) {
        Admin.clientFactory.getEventBus().fireEvent(new RemoveNewsItem(element));
    }

    @Override
    protected void editElement(NewsItem element) {
        Admin.clientFactory.getPlaceController().goTo(new NewsItemPlace(element));
    }
}
