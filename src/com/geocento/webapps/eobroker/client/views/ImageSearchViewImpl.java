package com.geocento.webapps.eobroker.client.views;

import com.geocento.webapps.eobroker.client.ClientFactoryImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialDropDown;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImageSearchViewImpl extends Composite implements ImageSearchView {

    private Presenter presenter;

    interface SearchPageUiBinder extends UiBinder<Widget, ImageSearchViewImpl> {
    }

    private static SearchPageUiBinder ourUiBinder = GWT.create(SearchPageUiBinder.class);
    @UiField
    MaterialDropDown providerDropdown;

    public ImageSearchViewImpl(ClientFactoryImpl clientFactory) {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}