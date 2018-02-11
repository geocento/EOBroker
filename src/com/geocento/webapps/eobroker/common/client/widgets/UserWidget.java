package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.HasText;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.ui.html.Span;

/**
 * Created by thomas on 14/11/2016.
 */
public class UserWidget extends Span implements HasText {

    static private Color[] colors = new Color[]{Color.RED, Color.AMBER, Color.BLUE};

    public UserWidget() {
        super(Document.get().createSpanElement(), "circle");
        setTextColor(Color.WHITE);
        setBackgroundColor(Color.RED);
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

    public void setUserImage(String userImageUrl) {
        setText("");
        getElement().getStyle().setProperty("background", "url('" + userImageUrl + "') no-repeat");
        getElement().getStyle().setProperty("backgroundSize", "100% 100%");
    }

    public void setSize(int size) {
        super.setFontSize(((int) size / 2) + "px");
        setWidth(size + "px");
        setHeight(size + "px");
        getElement().getStyle().setLineHeight(size, Style.Unit.PX);
    }

    public void setUserDescription(String description) {
        setTooltip(description);
        setTooltipPosition(Position.RIGHT);
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
