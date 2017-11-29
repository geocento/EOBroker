package com.geocento.webapps.eobroker.admin.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductCategoriesPlace extends EOBrokerPlace {

    public static enum TOKENS {start, limit, orderby};

    public ProductCategoriesPlace() {
    }

    public ProductCategoriesPlace(int start, int limit, String orderby) {
        this(
                TOKENS.start.toString() + "=" + start + "&" +
                        TOKENS.limit.toString() + "=" + limit +
                        (orderby == null ? "" : "&" + TOKENS.orderby.toString() + "=" + orderby));
    }

    public ProductCategoriesPlace(String token) {
        super(token);
    }

    @Prefix("productCategories")
    public static class Tokenizer implements PlaceTokenizer<ProductCategoriesPlace> {
        @Override
        public String getToken(ProductCategoriesPlace place) {
            return place.getToken();
        }

        @Override
        public ProductCategoriesPlace getPlace(String token) {
            return new ProductCategoriesPlace(token);
        }
    }
}
