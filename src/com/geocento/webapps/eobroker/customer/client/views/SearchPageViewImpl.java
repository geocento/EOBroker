package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.LoginPagePlace;
import com.geocento.webapps.eobroker.customer.client.widgets.ProductServiceWidget;
import com.geocento.webapps.eobroker.customer.client.widgets.ProductWidget;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.ProgressType;
import gwt.material.design.client.events.SearchFinishEvent;
import gwt.material.design.client.ui.*;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class SearchPageViewImpl extends Composite implements SearchPageView {

    interface SearchPageUiBinder extends UiBinder<Widget, SearchPageViewImpl> {
    }

    private static SearchPageUiBinder ourUiBinder = GWT.create(SearchPageUiBinder.class);

    static public interface Style extends CssResource {

        String productTitle();
        String productServicesTitle();
        String alternativesTitle();
    }

    @UiField
    Style style;

    @UiField
    MaterialLink signIn;
    @UiField
    MaterialSearch textSearch;
    @UiField
    MaterialNavBar navBarSearch;
    @UiField
    MaterialLink currentSearch;
    @UiField
    MaterialNavBar navBar;
    @UiField
    HTMLPanel categories;
    @UiField
    MaterialCollapsibleItem categoriesPanel;
    @UiField
    MaterialSideNav filtersPanel;
    @UiField
    MaterialContainer container;

    private Presenter presenter;

    public SearchPageViewImpl(ClientFactoryImpl clientFactory) {
        initWidget(ourUiBinder.createAndBindUi(this));
        textSearch.addCloseHandler(new CloseHandler<String>() {
            @Override
            public void onClose(CloseEvent<String> event) {
                displayTextSearch(false);
            }
        });
        textSearch.addSearchFinishHandler(new SearchFinishEvent.SearchFinishHandler() {
            @Override
            public void onSearchFinish(SearchFinishEvent event) {
                currentSearch.setText(textSearch.getSelectedObject().getKeyword());
                MaterialToast.fireToast("You search : " + textSearch.getSelectedObject().getKeyword());
                displayTextSearch(false);
            }
        });
        textSearch.addDomHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                displayTextSearch(false);
            }
        }, BlurEvent.getType());

        // add categories controls
        categories.clear();
        for(Category category : Category.values()) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox();
            materialCheckBox.setText(category.getName());
            materialCheckBox.setObject(category);
            categories.add(materialCheckBox);
            materialCheckBox.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // TODO - update display
                }
            });
        }
        categoriesPanel.expand();

        filtersPanel.show();
    }

    private void displayTextSearch(boolean display) {
        navBar.setVisible(!display);
        navBarSearch.setVisible(display);
        textSearch.setText(currentSearch.getText());
        textSearch.setFocus(display);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setCurrentSearch(String search) {
        currentSearch.setText(search);
    }

    @Override
    public void displayLoadingResults(String message) {
        navBar.showProgress(ProgressType.INDETERMINATE);
    }

    @Override
    public void hideLoadingResults() {
        navBar.hideProgress();
    }

    @Override
    public void clearResults() {
        container.clear();
    }

    @Override
    public void setProductSelection(ProductDTO productDTO, List<ProductServiceDTO> services, List<ProductDTO> others) {
        clearResults();
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "Selected product", style.productTitle());
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        productRow.add(materialColumn);
        materialColumn.add(new ProductWidget(productDTO));
        addTitle(productRow, "EO Broker services offering this product", style.productServicesTitle());
        for(ProductServiceDTO productServiceDTO : services) {
            MaterialColumn serviceColumn = new MaterialColumn(12, 12, 6);
            productRow.add(serviceColumn);
            serviceColumn.add(new ProductServiceWidget(productServiceDTO));
        }
        addTitle(productRow, "Also", style.alternativesTitle());
    }

    private void addTitle(MaterialRow productRow, String message, String style) {
        HTMLPanel htmlPanel = new HTMLPanel("<span class='flow-text'>" + message + "</span>");
        htmlPanel.addStyleName(style);
        MaterialColumn titleMaterialColumn = new MaterialColumn(12, 12, 12);
        productRow.add(titleMaterialColumn);
        titleMaterialColumn.add(htmlPanel);
    }

    @Override
    public void setCategory(Category category) {
        for(Widget widget : categories) {
            if(widget instanceof MaterialCheckBox) {
                MaterialCheckBox checkBox = ((MaterialCheckBox) widget);
                checkBox.setValue(((Category) checkBox.getObject()) == category);
            }
        }
    }

    @Override
    public HasClickHandlers getChangeSearch() {
        return currentSearch;
    }

    @Override
    public void setMatchingProducts(List<ProductDTO> suggestedProducts) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "Products matching your request", style.productTitle());
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        productRow.add(materialColumn);
        for(ProductDTO productDTO : suggestedProducts) {
            materialColumn.add(new ProductWidget(productDTO));
        }
        addTitle(productRow, "Also", style.alternativesTitle());
    }

    @Override
    public void setMatchingServices(List<ProductServiceDTO> productServices) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "EO Broker services matching your request", style.productServicesTitle());
        for(ProductServiceDTO productServiceDTO : productServices) {
            MaterialColumn serviceColumn = new MaterialColumn(12, 12, 6);
            productRow.add(serviceColumn);
            serviceColumn.add(new ProductServiceWidget(productServiceDTO));
        }
    }

    @Override
    public void displayProductsList(List<ProductDTO> products, int start, int limit, String text) {
        HTMLPanel panel = new HTMLPanel("<span class='flow-text'>TODO - add controls to navigate through the list</span>");
        container.add(panel);
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        productRow.add(materialColumn);
        for(ProductDTO productDTO : products) {
            materialColumn.add(new ProductWidget(productDTO));
        }
    }

    @UiHandler("signIn")
    void signIn(ClickEvent clickEvent) {
        Customer.clientFactory.getPlaceController().goTo(new LoginPagePlace());
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}