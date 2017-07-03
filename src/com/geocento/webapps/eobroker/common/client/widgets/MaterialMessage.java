package com.geocento.webapps.eobroker.common.client.widgets;

import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialLabel;

/**
 * Created by thomas on 03/07/2017.
 */
public class MaterialMessage extends MaterialLabel {

    public MaterialMessage() {
    }

    public void displaySuccessMessage(String message) {
        setTextColor(Color.GREEN);
        setText(message);
    }

    public void displayErrorMessage(String message) {
        setTextColor(Color.RED);
        setText(message);
    }

    public void displayWarningMessage(String message) {
        setTextColor(Color.ORANGE);
        setText(message);
    }
}
