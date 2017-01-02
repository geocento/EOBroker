package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.widgets.DatasetsList;
import com.geocento.webapps.eobroker.admin.shared.dtos.DatasetProviderDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class DatasetProvidersViewImpl extends Composite implements DatasetProvidersView {

    interface DatasetProvidersViewUiBinder extends UiBinder<Widget, DatasetProvidersViewImpl> {
    }

    private static DatasetProvidersViewUiBinder ourUiBinder = GWT.create(DatasetProvidersViewUiBinder.class);

    public static interface Style extends CssResource {
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    DatasetsList datasets;
    @UiField
    MaterialButton createNew;

    private Presenter presenter;

    public DatasetProvidersViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setDatasets(List<DatasetProviderDTO> datasetProviderDTOs) {
        this.datasets.setRowData(0, datasetProviderDTOs);
    }

    @Override
    public HasClickHandlers getCreateNewButton() {
        return createNew;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}