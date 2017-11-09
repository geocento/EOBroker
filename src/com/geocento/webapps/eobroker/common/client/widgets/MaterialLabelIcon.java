package com.geocento.webapps.eobroker.common.client.widgets;

import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;

/**
 * Created by thomas on 13/10/2016.
 */
public class MaterialLabelIcon extends HTMLPanel {

    private Image image;
    private Element span;
    private String text;
    private String imageWidth;
    private String imageHeight;
    private int spacing = 5;

    public MaterialLabelIcon() {
        super("");
        span = DOM.createElement("span");
        span.getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
        DOM.insertChild(getElement(), span, 0);
        getElement().getStyle().setWhiteSpace(Style.WhiteSpace.NOWRAP);
    }

    public MaterialLabelIcon(String name) {
        this();
        setText(name);
    }

    public MaterialLabelIcon(String iconURL, String name) {
        this();
        setImageUrl(iconURL);
        setText(name);
    }

    public MaterialLabelIcon(ImageResource imageResource, String name) {
        this();
        setImageResource(imageResource);
        setText(name);
    }

    public void setImageUrl(String imageUrl) {
        createImageMaybe();
        image.setUrl(imageUrl == null || imageUrl.length() == 0 ? "./images/noImage.png" : imageUrl);
    }

    public void setImageResource(ImageResource resource) {
        createImageMaybe();
        image.setResource(resource);
    }

    private void createImageMaybe() {
        if(image == null) {
            image = new Image();
            image.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
            updateImage();
            DOM.insertChild(getElement(), image.getElement(), 0);
        }
    }

    public void setImageWidth(String width) {
        this.imageWidth = width;
        updateImage();
    }

    public void setImageHeight(String height) {
        this.imageHeight = height;
        updateImage();
    }

    public void setSpacing(int spacing) {
        this.spacing = spacing;
        updateImage();
    }

    private void updateImage() {
        if(image == null) {
            return;
        }
        if(imageWidth != null) {
            image.setWidth(imageWidth);
        }
        if(imageHeight != null) {
            image.setHeight(imageHeight);
        }
        image.getElement().getStyle().setMarginRight(spacing, Style.Unit.PX);
    }

    public void setText(String text) {
        this.text = text;
        span.setInnerText(text);
    }

}
