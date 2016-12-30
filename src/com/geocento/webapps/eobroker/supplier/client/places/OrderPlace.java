package com.geocento.webapps.eobroker.supplier.client.places;

import com.geocento.webapps.eobroker.common.shared.entities.requests.RequestDTO;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class OrderPlace extends EOBrokerPlace {

    public static enum TOKENS {id, type};

    public OrderPlace() {
    }

    public OrderPlace(String token) {
        super(token);
    }

    public OrderPlace(String id, RequestDTO.TYPE type) {
        this(TOKENS.id.toString() + "=" + id + "&" + TOKENS.type.toString() + "=" + type);
    }

    @Prefix("order")
    public static class Tokenizer implements PlaceTokenizer<OrderPlace> {
        @Override
        public String getToken(OrderPlace place) {
            return place.getToken();
        }

        @Override
        public OrderPlace getPlace(String token) {
            return new OrderPlace(token);
        }
    }
}
