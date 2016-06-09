package com.geocento.webapps.eobroker.client.events;

import com.geocento.webapps.eobroker.shared.entities.Category;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 09/06/2016.
 */
public class SuggestionSelected extends GwtEvent<SuggestionSelectedHandler> {

    public static Type<SuggestionSelectedHandler> TYPE = new Type<SuggestionSelectedHandler>();

    private final Category category;
    private final String name;

    public SuggestionSelected(Category category, String name) {
        this.category = category;
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public Type<SuggestionSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(SuggestionSelectedHandler handler) {
        handler.onSuggestionSelected(this);
    }
}
