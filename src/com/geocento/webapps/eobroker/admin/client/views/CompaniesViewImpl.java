package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.views.TemplateView;
import com.geocento.webapps.eobroker.admin.client.widgets.CompaniesList;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class CompaniesViewImpl extends Composite implements CompaniesView {

    interface CompaniesViewUiBinder extends UiBinder<Widget, CompaniesViewImpl> {
    }

    private static CompaniesViewUiBinder ourUiBinder = GWT.create(CompaniesViewUiBinder.class);

    public static interface Style extends CssResource {

        String navOpened();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    CompaniesList companies;

    private Presenter presenter;

    public CompaniesViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setCompanies(List<CompanyDTO> companyDTOs) {
        this.companies.setRowData(0, companyDTOs);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}