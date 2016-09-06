package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class NewsItemsPlace extends EOBrokerPlace {

    public static enum TOKENS {start, limit, orderby};

    public NewsItemsPlace() {
    }

    public NewsItemsPlace(int start, int limit, String orderby) {
        this(
                TOKENS.start.toString() + "=" + start + "&" +
                        TOKENS.limit.toString() + "=" + limit +
                        (orderby == null ? "" : "&" + TOKENS.orderby.toString() + "=" + orderby));
    }

    public NewsItemsPlace(String token) {
        super(token);
    }

    @Prefix("newsitems")
    public static class Tokenizer implements PlaceTokenizer<NewsItemsPlace> {
        @Override
        public String getToken(NewsItemsPlace place) {
            return place.getToken();
        }

        @Override
        public NewsItemsPlace getPlace(String token) {
            return new NewsItemsPlace(token);
        }
    }
}
