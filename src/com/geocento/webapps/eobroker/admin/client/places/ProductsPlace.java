package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductsPlace extends EOBrokerPlace {

    public static enum TOKENS {start, limit, orderby};

    public ProductsPlace() {
    }

    public ProductsPlace(int start, int limit, String orderby) {
        this(
                TOKENS.start.toString() + "=" + start + "&" +
                        TOKENS.limit.toString() + "=" + limit +
                        (orderby == null ? "" : "&" + TOKENS.orderby.toString() + "=" + orderby));
    }

    public ProductsPlace(String token) {
        super(token);
    }

    @Prefix("product")
    public static class Tokenizer implements PlaceTokenizer<ProductsPlace> {
        @Override
        public String getToken(ProductsPlace place) {
            return place.getToken();
        }

        @Override
        public ProductsPlace getPlace(String token) {
            return new ProductsPlace(token);
        }
    }
}
