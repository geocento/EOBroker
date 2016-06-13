package com.geocento.webapps.eobroker.customer.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 09/06/2016.
 */
public class TextSelected extends GwtEvent<TextSelectedHandler> {

    public static Type<TextSelectedHandler> TYPE = new Type<TextSelectedHandler>();

    public Type<TextSelectedHandler> getAssociatedType() {
        return TYPE;
    }

    private String text;

    public TextSelected(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    protected void dispatch(TextSelectedHandler handler) {
        handler.onTextSelected(this);
    }
}
