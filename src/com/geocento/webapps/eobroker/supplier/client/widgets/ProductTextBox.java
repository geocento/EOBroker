package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.material.MaterialSearch;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import gwt.material.design.client.base.SearchObject;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 03/11/2016.
 */
public class ProductTextBox extends MaterialSearch {

    public static interface Presenter {

        void selectProduct(ProductDTO productDTO);

    }

    private Presenter presenter;

    private long lastCall = 0;

    public ProductTextBox() {

        setPlaceholder("Type in keyword for product");

        setPresenter(new MaterialSearch.Presenter() {

            @Override
            public void textChanged(String text) {
                if(text == null || text.length() == 0) {
                    presenter.selectProduct(null);
                } else {
                    updateSuggestions();
                }
            }

            @Override
            public void suggestionSelected(SearchObject suggestion) {
                presenter.selectProduct((ProductDTO) suggestion.getO());
            }

            @Override
            public void textSelected(String text) {

            }
        });

    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void updateSuggestions() {
        this.lastCall++;
        final long lastCall = this.lastCall;

        String text = getText();
        if(text != null && text.length() > 0) {
            REST.withCallback(new MethodCallback<List<ProductDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<ProductDTO> response) {
                    // show only if last one to be called
                    if (lastCall == ProductTextBox.this.lastCall) {
                        setFocus(true);
                        List<SearchObject> results = ListUtil.mutate(response, new ListUtil.Mutate<ProductDTO, SearchObject>() {
                            @Override
                            public SearchObject mutate(ProductDTO productDTO) {
                                return new SearchObject(productDTO.getName(), "", productDTO);
                            }
                        });
                        displayListSearches(results);
                    }
                }
            }).call(ServicesUtil.assetsService).findProducts(getText());
        }

    }

    public void setProduct(ProductDTO product) {
        setSelectedObject(product != null ? new SearchObject(product.getName(), "", product) : null);
        setText(product != null && product.getName() != null ? product.getName() : "");
    }

    public ProductDTO getProduct() {
        return getSelectedObject() != null ? (ProductDTO) getSelectedObject().getO() : null;
    }

    public void clearProduct() {
        setProduct(null);
    }
}
