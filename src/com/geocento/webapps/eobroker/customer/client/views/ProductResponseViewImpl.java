package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceSupplierResponseDTO;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTMLPanel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTab;
import gwt.material.design.client.ui.MaterialTabItem;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductResponseViewImpl extends RequestViewImpl implements ProductResponseView {

    private ProductResponseView.Presenter presenter;

    public ProductResponseViewImpl(ClientFactoryImpl clientFactory) {
        super(clientFactory);
    }

    @Override
    public void setPresenter(ProductResponseView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayProductRequest(ProductServiceResponseDTO productServiceResponseDTO) {
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
            tabs.setVisible(true);
            MaterialTab materialTab = new MaterialTab();
            materialTab.setBackgroundColor("transparent");
            for(final ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO : responses) {
                MaterialTabItem materialTabItem = new MaterialTabItem();
                materialTabItem.setId(productServiceSupplierResponseDTO.getId() + "");
                materialTab.add(materialTabItem);
                MaterialLink materialLink = new MaterialLink(productServiceSupplierResponseDTO.getCompany().getName());
                materialLink.setHref("#" + productServiceSupplierResponseDTO.getId());
                materialTabItem.add(materialLink);
                materialLink.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.responseSelected(productServiceSupplierResponseDTO);
                    }
                });
                MaterialRow materialRow = new MaterialRow();
                materialRow.setId(productServiceSupplierResponseDTO.getId() + "");
                tabs.add(materialRow);
            }
            tabs.add(materialTab);
        } else {
            tabs.setVisible(false);
            displayProductResponse(responses.get(0));
        }
    }

    @Override
    public void displayProductResponse(ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO) {
        if(tabs.isVisible()) {
            // make sure we select the tab
            //tabs.selectTab(productServiceSupplierResponseDTO.getId() + "");
        }
        displayResponse(productServiceSupplierResponseDTO.getResponse());
        displayMessages(productServiceSupplierResponseDTO.getMessages());
    }

    private void addResponse(final ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO) {
        MaterialTabItem materialTabItem = new MaterialTabItem();
        materialTabItem.setId(productServiceSupplierResponseDTO.getId() + "");
        tabs.add(materialTabItem);
        MaterialLink materialLink = new MaterialLink(productServiceSupplierResponseDTO.getCompany().getName());
        materialTabItem.add(materialLink);
        materialLink.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                presenter.responseSelected(productServiceSupplierResponseDTO);
            }
        });
    }

}