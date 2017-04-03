package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.views.TemplateView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by thomas on 09/05/2016.
 */
public class TestimonialsViewImpl extends Composite implements TestimonialsView {

    private Presenter presenter;

    interface DummyUiBinder extends UiBinder<Widget, TestimonialsViewImpl> {
    }

    private static DummyUiBinder ourUiBinder = GWT.create(DummyUiBinder.class);

    @UiField(provided = true)
    TemplateView template;

    public TestimonialsViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        template.setTitleText("Product form");
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
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