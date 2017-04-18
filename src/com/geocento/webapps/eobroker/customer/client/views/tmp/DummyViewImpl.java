package com.geocento.webapps.eobroker.customer.client.views.tmp;

import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by thomas on 09/05/2016.
 */
public class DummyViewImpl extends Composite implements DummyFormView {

    private Presenter presenter;

    interface DummyUiBinder extends UiBinder<Widget, DummyViewImpl> {
    }

    private static DummyUiBinder ourUiBinder = GWT.create(DummyUiBinder.class);

    public DummyViewImpl(ClientFactoryImpl clientFactory) {

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