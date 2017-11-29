package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.widgets.ProductCategoriesList;
import com.geocento.webapps.eobroker.admin.client.widgets.SimpleFileUpload;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingWidgetList;
import com.geocento.webapps.eobroker.common.client.widgets.material.MaterialFileUploader;
import com.geocento.webapps.eobroker.common.shared.entities.ProductCategory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.SuccessEvent;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialTextBox;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductCategoriesViewImpl extends Composite implements ProductCategoriesView {

    interface DashboardViewUiBinder extends UiBinder<Widget, ProductCategoriesViewImpl> {
    }

    private static DashboardViewUiBinder ourUiBinder = GWT.create(DashboardViewUiBinder.class);

    public static interface Style extends CssResource {
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    ProductCategoriesList productCategories;
    @UiField
    MaterialButton createNew;
    @UiField
    MaterialTextBox filter;
    @UiField
    SimpleFileUpload importCSV;
    @UiField
    SimpleFileUpload assignCategoriesCSV;

    private Presenter presenter;

    public ProductCategoriesViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        productCategories.setPresenter(new AsyncPagingWidgetList.Presenter() {
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

        importCSV.setUrl(GWT.getHostPageBaseURL() + "admin/api/upload/productcategories/import");
        importCSV.addSuccessHandler(event -> presenter.reloadProductCategories());

        assignCategoriesCSV.setUrl(GWT.getHostPageBaseURL() + "admin/api/upload/productcategories/assign/import");
        assignCategoriesCSV.addSuccessHandler(event -> presenter.reloadProductCategories());

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearProductCategories() {
        productCategories.clearData();
    }

    @Override
    public void addProductCategories(boolean hasMore, List<ProductCategory> productCategories) {
        this.productCategories.addData(productCategories, hasMore);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public HasClickHandlers getCreateNew() {
        return createNew;
    }

    @Override
    public void setProductCategoriesLoading(boolean loading) {
        productCategories.setLoading(loading);
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}