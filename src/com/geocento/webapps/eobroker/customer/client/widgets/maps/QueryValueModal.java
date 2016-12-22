package com.geocento.webapps.eobroker.customer.client.widgets.maps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.RootPanel;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialTextBox;
import gwt.material.design.client.ui.MaterialTitle;

/**
 * Created by thomas on 15/11/2016.
 */
public class QueryValueModal {

    interface QueryValueModalUiBinder extends UiBinder<MaterialModal, QueryValueModal> {
    }

    private static QueryValueModalUiBinder ourUiBinder = GWT.create(QueryValueModalUiBinder.class);

    public static interface Presenter {
        void onValue(String value);
        void onCancel();
    }

    @UiField
    MaterialTextBox value;
    @UiField
    MaterialPanel values;
    @UiField
    MaterialTitle title;

    private final MaterialModal materialModal;

    private static QueryValueModal instance = null;

    private Presenter presenter;

    public QueryValueModal() {

        materialModal = ourUiBinder.createAndBindUi(this);

        // add to document
        RootPanel.get().add(materialModal);

    }

    public static QueryValueModal getInstance() {
        if(instance == null) {
            instance = new QueryValueModal();
        }
        return instance;
    }

    public void getValue(String title, String description, String initialValue, Presenter presenter) {
        this.presenter = presenter;
        this.title.setTitle(title);
        this.title.setDescription(description);
        value.setText(initialValue != null ? initialValue : "");
        materialModal.openModal();
    }

    @UiHandler("submit")
    void submit(ClickEvent clickEvent) {
        hide();
        if(presenter != null) {
            presenter.onValue(value.getValue());
        }
    }

    @UiHandler("cancel")
    void cancel(ClickEvent clickEvent) {
        hide();
    }

    private void hide() {
        materialModal.closeModal();
    }

}