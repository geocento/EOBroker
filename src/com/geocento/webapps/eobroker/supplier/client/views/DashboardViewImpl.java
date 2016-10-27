package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.widgets.CompanyWidget;
import com.geocento.webapps.eobroker.supplier.client.widgets.DatasetProviderWidget;
import com.geocento.webapps.eobroker.supplier.client.widgets.ProductServiceWidget;
import com.geocento.webapps.eobroker.supplier.shared.dtos.DatasetProviderDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialLabel;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class DashboardViewImpl extends Composite implements DashboardView {

    private Presenter presenter;

    interface DashboardViewUiBinder extends UiBinder<Widget, DashboardViewImpl> {
    }

    private static DashboardViewUiBinder ourUiBinder = GWT.create(DashboardViewUiBinder.class);

    @UiField
    HTMLPanel services;
    @UiField
    CompanyWidget companyWidget;
    @UiField
    MaterialButton editCompany;
    @UiField
    MaterialButton addService;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialButton addDataset;
    @UiField
    HTMLPanel datasets;

    public DashboardViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setServices(List<ProductServiceDTO> productServiceDTOs) {
        services.clear();
        if(productServiceDTOs == null || productServiceDTOs.size() == 0) {
            services.add(new MaterialLabel("No service, click on the button below to add a new service"));
            return;
        }
        for(ProductServiceDTO productServiceDTO : productServiceDTOs) {
            services.add(new ProductServiceWidget(productServiceDTO));
        }
    }

    @Override
    public void setDatasets(List<DatasetProviderDTO> datasetProviderDTOs) {
        datasets.clear();
        if(datasetProviderDTOs == null || datasetProviderDTOs.size() == 0) {
            datasets.add(new MaterialLabel("No datasets, click on the button below to add a new dataset"));
            return;
        }
        for(DatasetProviderDTO datasetProviderDTO : datasetProviderDTOs) {
            datasets.add(new DatasetProviderWidget(datasetProviderDTO));
        }
    }

    @Override
    public void setCompany(CompanyDTO companyDTO) {
        companyWidget.setCompany(companyDTO);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public HasClickHandlers getAddService() {
        return addService;
    }

    @Override
    public HasClickHandlers getAddDataset() {
        return addDataset;
    }

    @Override
    public HasClickHandlers editCompany() {
        return editCompany;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}