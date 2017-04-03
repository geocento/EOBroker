package com.geocento.webapps.eobroker.common.client.widgets;

import com.geocento.webapps.eobroker.common.client.styles.StyleResources;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Image;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 31/03/2017.
 */
public class MaterialLoadingLink extends MaterialLink {

    private Image loadingIcon;

    public MaterialLoadingLink() {
    }

    public void setLoading(boolean loading) {
        setEnabled(!loading);
        if(loading) {
            // insert loading icon
            if (loadingIcon == null) {
                loadingIcon = new Image();
                loadingIcon.setResource(StyleResources.INSTANCE.loadingSmall());
                loadingIcon.getElement().getStyle().setPaddingRight(5, Style.Unit.PX);
                loadingIcon.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.MIDDLE);
                loadingIcon.getElement().getStyle().setDisplay(Style.Display.INLINE_BLOCK);
                getElement().insertFirst(loadingIcon.getElement());
            }
        } else {
            getElement().removeChild(loadingIcon.getElement());
            loadingIcon = null;
        }
    }
}
