package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.widgets.CompaniesList;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingWidgetList;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialTextBox;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class CompaniesViewImpl extends Composite implements CompaniesView {

    interface CompaniesViewUiBinder extends UiBinder<Widget, CompaniesViewImpl> {
    }

    private static CompaniesViewUiBinder ourUiBinder = GWT.create(CompaniesViewUiBinder.class);

    public static interface Style extends CssResource {
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    CompaniesList companies;
    @UiField
    MaterialButton createNew;
    @UiField
    MaterialTextBox filter;

    private Presenter presenter;

    public CompaniesViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        companies.setPresenter(new AsyncPagingWidgetList.Presenter() {
            @Override
            public void loadMore() {
                presenter.loadMore();
            }
        });

        filter.addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                presenter.changeFilter(event.getValue());
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasClickHandlers getCreateNewButton() {
        return createNew;
    }

    @Override
    public void clearCompanies() {
        this.companies.clearData();
    }

    @Override
    public void setCompaniesLoading(boolean loading) {
        companies.setLoading(loading);
    }

    @Override
    public void addCompanies(boolean hasMore, List<CompanyDTO> companyDTOs) {
        this.companies.addData(companyDTOs, hasMore);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}