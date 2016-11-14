package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HasText;
import gwt.material.design.client.ui.html.Span;

/**
 * Created by thomas on 14/11/2016.
 */
public class UserWidget extends Span implements HasText {

    static private String[] colors = new String[] {"red", "green", "blue"};

    public UserWidget() {
        super(Document.get().createSpanElement(), "circle");
        setTextColor("white");
        setBackgroundColor("red");
        getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
        getElement().getStyle().setTextTransform(Style.TextTransform.CAPITALIZE);
        getElement().getStyle().setTextAlign(Style.TextAlign.CENTER);
    }

    public UserWidget(String userName) {
        this();
        setUser(userName);
    }

    public void setUser(String userName) {
        setText(userName.substring(0, 1));
        setBackgroundColor(colors[(Math.abs(userName.hashCode()) % colors.length)]);
    }

    public void setSize(int size) {
        super.setFontSize(((int) size / 2) + "px");
        setWidth(size + "px");
        setHeight(size + "px");
        getElement().getStyle().setLineHeight(size, Style.Unit.PX);
    }

    @Override
    public String getText() {
        return getElement().getInnerHTML();
    }

    @Override
    public void setText(String text) {
        getElement().setInnerHTML(text);
    }
}
