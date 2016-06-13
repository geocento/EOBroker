package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by thomas on 09/05/2016.
 */
public class CompanyViewImpl extends Composite implements CompanyView {

    private Presenter presenter;

    interface CompanyViewUiBinder extends UiBinder<Widget, CompanyViewImpl> {
    }

    private static CompanyViewUiBinder ourUiBinder = GWT.create(CompanyViewUiBinder.class);

    public CompanyViewImpl(ClientFactoryImpl clientFactory) {

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