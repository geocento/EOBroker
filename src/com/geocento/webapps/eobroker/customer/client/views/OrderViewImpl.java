package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialRow;

/**
 * Created by thomas on 09/05/2016.
 */
public class OrderViewImpl extends Composite implements OrderView {

    private Presenter presenter;

    interface OrderViewUiBinder extends UiBinder<Widget, OrderViewImpl> {
    }

    private static OrderViewUiBinder ourUiBinder = GWT.create(OrderViewUiBinder.class);

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialRow requestThread;
    @UiField
    MaterialRow requestInformation;

    public OrderViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void setRequest(RequestDTO requestDTO) {
        requestInformation.clear();
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}