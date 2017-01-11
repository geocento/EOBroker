package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.requests.RequestDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.widgets.RequestWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialRow;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class OrdersViewImpl extends Composite implements OrdersView {

    private Presenter presenter;

    interface OrdersViewUiBinder extends UiBinder<Widget, OrdersViewImpl> {
    }

    private static OrdersViewUiBinder ourUiBinder = GWT.create(OrdersViewUiBinder.class);

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialRow requestsList;

    public OrdersViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        template.setPlace(null);
    }

    @Override
    public void setRequests(List<RequestDTO> requestDTOs) {
        requestsList.clear();
        for(RequestDTO requestDTO : requestDTOs) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
            materialColumn.add(new RequestWidget(requestDTO));
            requestsList.add(materialColumn);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}