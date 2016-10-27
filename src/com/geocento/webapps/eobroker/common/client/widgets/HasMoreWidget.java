package com.geocento.webapps.eobroker.common.client.widgets;

import com.geocento.webapps.eobroker.customer.client.widgets.FeasibilityHeader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPreLoader;

/**
 * Created by thomas on 09/06/2016.
 */
public class HasMoreWidget extends Composite {

    interface HasMoreWidgetUiBinder extends UiBinder<Widget, HasMoreWidget> {
    }

    private static HasMoreWidgetUiBinder ourUiBinder = GWT.create(HasMoreWidgetUiBinder.class);

    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLink loadMore;
    @UiField
    MaterialPreLoader loader;

    public HasMoreWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
        setLoading(false);
    }

    public HasClickHandlers getLoadMore() {
        return loadMore;
    }

    public void setLoading(boolean loading) {
        loader.setVisible(loading);
        loadMore.setVisible(!loading);
    }

}