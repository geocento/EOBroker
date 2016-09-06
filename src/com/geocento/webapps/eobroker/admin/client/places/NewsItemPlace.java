package com.geocento.webapps.eobroker.admin.client.places;

import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class NewsItemPlace extends EOBrokerPlace {

    static public enum TOKENS {id};

    public NewsItemPlace() {
    }

    public NewsItemPlace(Long id) {
        token = TOKENS.id.toString() + "=" + id.longValue();
    }

    public NewsItemPlace(String token) {
        super(token);
    }

    public NewsItemPlace(NewsItem newsItem) {
        this(TOKENS.id.toString() + "=" + newsItem.getId());
    }

    @Prefix("newsitem")
    public static class Tokenizer implements PlaceTokenizer<NewsItemPlace> {
        @Override
        public String getToken(NewsItemPlace place) {
            return place.getToken();
        }

        @Override
        public NewsItemPlace getPlace(String token) {
            return new NewsItemPlace(token);
        }
    }
}
