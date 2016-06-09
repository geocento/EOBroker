package com.geocento.webapps.eobroker.client.views;

import com.geocento.webapps.eobroker.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.client.eobroker;
import com.geocento.webapps.eobroker.client.places.LoginPagePlace;
import com.geocento.webapps.eobroker.client.widgets.ProductWidget;
import com.geocento.webapps.eobroker.shared.entities.Category;
import com.geocento.webapps.eobroker.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.shared.entities.dtos.ProductServiceDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
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

    private Presenter presenter;

    private MaterialRow productRow;

    interface SearchPageUiBinder extends UiBinder<Widget, SearchPageViewImpl> {
    }

    private static SearchPageUiBinder ourUiBinder = GWT.create(SearchPageUiBinder.class);

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
    public void addProduct(ProductDTO productDTO, List<ProductServiceDTO> services) {
        if(productRow == null) {
            productRow = new MaterialRow();
            container.add(productRow);
        }
        MaterialColumn materialColumn = new MaterialColumn(6, 6, 12);
        productRow.add(materialColumn);
        materialColumn.add(new ProductWidget(productDTO));
    }

    @Override
    public void setCategory(Category category) {

    }

    @UiHandler("signIn")
    void signIn(ClickEvent clickEvent) {
        eobroker.clientFactory.getPlaceController().goTo(new LoginPagePlace());
    }

    @UiHandler("currentSearch")
    void search(ClickEvent clickEvent) {
        displayTextSearch(true);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}