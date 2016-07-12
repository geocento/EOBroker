package com.geocento.webapps.eobroker.customer.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductFeasibilityPlace extends EOBrokerPlace {

    static public enum TOKENS {productservice, aoi}

    public ProductFeasibilityPlace() {
    }

    public ProductFeasibilityPlace(String token) {
        super(token);
    }

    @Prefix("productfeasibility")
    public static class Tokenizer implements PlaceTokenizer<ProductFeasibilityPlace> {
        @Override
        public String getToken(ProductFeasibilityPlace place) {
            return place.getToken();
        }

        @Override
        public ProductFeasibilityPlace getPlace(String token) {
            return new ProductFeasibilityPlace(token);
        }
    }
}
