package com.geocento.webapps.eobroker.customer.client.widgets;

import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 30/05/2016.
 */
public class ServiceOracle extends MaterialSuggestionOracle {

    @Override
    public void requestSuggestions(Request request, Callback callback) {
        Response resp = new Response();
        List<Suggestion> suggestions = new ArrayList<Suggestion>();
        suggestions.add(new Suggestion() {
            @Override
            public String getDisplayString() {
                return "Test";
            }

            @Override
            public String getReplacementString() {
                return "test";
            }
        });
        if(suggestions.isEmpty()){
            callback.onSuggestionsReady(request, resp);
            return;
        }

        String text = request.getQuery();
        text = text.toLowerCase();

        resp.setSuggestions(suggestions);
        callback.onSuggestionsReady(request, resp);
    }
}
