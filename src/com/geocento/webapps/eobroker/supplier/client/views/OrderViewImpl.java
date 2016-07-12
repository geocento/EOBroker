package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.shared.dtos.UserDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialColumn;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTitle;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class OrderViewImpl extends Composite implements OrderView {

    private Presenter presenter;

    interface OrdersViewUiBinder extends UiBinder<Widget, OrderViewImpl> {
    }

    private static OrdersViewUiBinder ourUiBinder = GWT.create(OrdersViewUiBinder.class);

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialTitle title;
    @UiField
    MaterialRow formValues;

    public OrderViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void displayTitle(String title) {
        this.title.setTitle(title);
    }

    @Override
    public void displayUser(UserDTO customer) {
        this.title.setDescription("From user '" + customer.getName() + "'");
    }

    @Override
    public void displayFormValue(List<FormElementValue> formValues) {
        this.formValues.clear();
        for(FormElementValue formElementValue : formValues) {
            MaterialColumn nameColumn = new MaterialColumn(12, 12, 6);
            nameColumn.add(new Label(formElementValue.getName()));
            this.formValues.add(nameColumn);
            MaterialColumn valueColumn = new MaterialColumn(12, 12, 6);
            valueColumn.add(new Label(formElementValue.getValue()));
            this.formValues.add(valueColumn);
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