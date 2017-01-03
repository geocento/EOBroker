package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import gwt.material.design.client.base.SearchObject;
import gwt.material.design.client.ui.MaterialSearch;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 03/11/2016.
 */
public class ProductTextBox extends MaterialSearch {

    public ProductTextBox() {

        setPlaceholder("Type in keyword for product");

        addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                REST.withCallback(new MethodCallback<List<ProductDTO>>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, List<ProductDTO> response) {
                        setListSearches(ListUtil.mutate(response, new ListUtil.Mutate<ProductDTO, SearchObject>() {
                            @Override
                            public SearchObject mutate(ProductDTO productDTO) {
                                return new SearchObject(productDTO.getName(), "", productDTO);
                            }
                        }));
                    }
                }).call(ServicesUtil.assetsService).findProducts(getValue());
            }
        });
    }

    public void setProduct(ProductDTO product) {
        setSelectedObject(product != null ? new SearchObject(product.getName(), "", product) : null);
        setText(product != null && product.getName() != null ? product.getName() : "");
    }

    public ProductDTO getProduct() {
        return getSelectedObject() != null ? (ProductDTO) getSelectedObject().getO() : null;
    }

    public void clearProduct() {
        setText("");
        setFocus(false);
    }
}
