package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceSupplierResponseDTO;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTMLPanel;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductResponseViewImpl extends RequestViewImpl implements ProductResponseView {

    private ProductResponseView.Presenter presenter;

    public ProductResponseViewImpl(ClientFactoryImpl clientFactory) {
        super(clientFactory);

        setCategory(Category.productservices);
    }

    @Override
    public void setPresenter(ProductResponseView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayProductRequest(ProductServiceResponseDTO productServiceResponseDTO) {
        resetTabs();
        setStatus(productServiceResponseDTO.getStatus());
        this.requestDescription.clear();
        addRequestValue("Product requested", productServiceResponseDTO.getProduct().getName());
        if(productServiceResponseDTO.getFormValues().size() == 0) {
            this.requestDescription.add(new HTMLPanel("<p>No data provided</p>"));
        } else {
            for (FormElementValue formElementValue : productServiceResponseDTO.getFormValues()) {
                addRequestValue(formElementValue.getName(), formElementValue.getValue());
            }
        }
        displayAoI(AoIUtil.fromWKT(productServiceResponseDTO.getAoIWKT()));
        List<ProductServiceSupplierResponseDTO> responses = productServiceResponseDTO.getSupplierResponses();
        if(responses.size() > 1) {
            for(final ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO : responses) {
                addResponseTab(productServiceSupplierResponseDTO.getId() + "", productServiceSupplierResponseDTO.getCompany().getName(), new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.responseSelected(productServiceSupplierResponseDTO);
                    }
                });
            }
        } else {
            tabs.setVisible(false);
            displayProductResponse(responses.get(0));
        }
    }

    @Override
    public void displayProductResponse(ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO) {
        if(productServiceSupplierResponseDTO == null) {
            selectTab("request");
        } else {
            // make sure we select the tab
            selectTab(productServiceSupplierResponseDTO.getId() + "");
            displayResponseSupplier(productServiceSupplierResponseDTO.getCompany().getIconURL(), productServiceSupplierResponseDTO.getCompany().getName());
            displayResponse(productServiceSupplierResponseDTO.getResponse());
            displayMessages(productServiceSupplierResponseDTO.getMessages());
        }
    }

}