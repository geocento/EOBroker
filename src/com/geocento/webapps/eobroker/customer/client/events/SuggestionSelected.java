package com.geocento.webapps.eobroker.customer.client.events;

import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 09/06/2016.
 */
public class SuggestionSelected extends GwtEvent<SuggestionSelectedHandler> {

    public static Type<SuggestionSelectedHandler> TYPE = new Type<SuggestionSelectedHandler>();

    private Suggestion suggestion;

    public SuggestionSelected(Suggestion suggestion) {
        this.suggestion = suggestion;
    }

    public Suggestion getSuggestion() {
        return suggestion;
    }

    public Type<SuggestionSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(SuggestionSelectedHandler handler) {
        handler.onSuggestionSelected(this);
    }
}
