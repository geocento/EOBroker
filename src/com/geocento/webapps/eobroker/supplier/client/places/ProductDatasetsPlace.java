package com.geocento.webapps.eobroker.supplier.client.places;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductDatasetsPlace extends EOBrokerPlace {

    static public enum TOKENS {};

    public ProductDatasetsPlace() {
    }

    public ProductDatasetsPlace(String token) {
        super(token);
    }

    @Prefix("productdatasets")
    public static class Tokenizer implements PlaceTokenizer<ProductDatasetsPlace> {
        @Override
        public String getToken(ProductDatasetsPlace place) {
            return place.getToken();
        }

        @Override
        public ProductDatasetsPlace getPlace(String token) {
            return new ProductDatasetsPlace(token);
        }
    }
}
