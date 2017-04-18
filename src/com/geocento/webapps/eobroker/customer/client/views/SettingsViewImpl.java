package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by thomas on 09/05/2016.
 */
public class SettingsViewImpl extends Composite implements SettingsView {

    private Presenter presenter;

    interface DummyUiBinder extends UiBinder<Widget, SettingsViewImpl> {
    }

    private static DummyUiBinder ourUiBinder = GWT.create(DummyUiBinder.class);

    public SettingsViewImpl(ClientFactoryImpl clientFactory) {

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