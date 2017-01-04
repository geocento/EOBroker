package com.geocento.webapps.eobroker.supplier.shared.dtos;

/**
 * Created by thomas on 04/01/2017.
 */
public class StyleDTO {

    String styleName;
    String sldBody;

    public StyleDTO() {
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getSldBody() {
        return sldBody;
    }

    public void setSldBody(String sldBody) {
        this.sldBody = sldBody;
    }
}
