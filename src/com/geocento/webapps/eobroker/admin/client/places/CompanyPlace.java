package com.geocento.webapps.eobroker.admin.client.places;

import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

/**
 * Created by thomas on 09/05/2016.
 */
public class CompanyPlace extends EOBrokerPlace {

    static public enum TOKENS {id};

    public CompanyPlace() {
    }

    public CompanyPlace(CompanyDTO companyDTO) {
        this(TOKENS.id.toString() + "=" + companyDTO.getId());
    }

    public CompanyPlace(Long id) {
        token = TOKENS.id.toString() + "=" + id.longValue();
    }

    public CompanyPlace(String token) {
        super(token);
    }

    public CompanyPlace(NewsItem newsItem) {
        this(TOKENS.id.toString() + "=" + newsItem.getId());
    }

    @Prefix("company")
    public static class Tokenizer implements PlaceTokenizer<CompanyPlace> {
        @Override
        public String getToken(CompanyPlace place) {
            return place.getToken();
        }

        @Override
        public CompanyPlace getPlace(String token) {
            return new CompanyPlace(token);
        }
    }
}
