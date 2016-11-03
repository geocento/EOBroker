package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductDatasetPlace extends EOBrokerPlace {

    static public enum TOKENS {id};

    public ProductDatasetPlace() {
    }

    public ProductDatasetPlace(String token) {
        super(token);
    }

    @Prefix("productdataset")
    public static class Tokenizer implements PlaceTokenizer<ProductDatasetPlace> {
        @Override
        public String getToken(ProductDatasetPlace place) {
            return place.getToken();
        }

        @Override
        public ProductDatasetPlace getPlace(String token) {
            return new ProductDatasetPlace(token);
        }
    }
}
