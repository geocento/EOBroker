package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.shared.requests.ImageryResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ImagerySupplierResponseDTO;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialRow;
import gwt.material.design.client.ui.MaterialTab;
import gwt.material.design.client.ui.MaterialTabItem;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImageryResponseViewImpl extends RequestViewImpl implements ImageryResponseView {

    private ImageryResponseView.Presenter presenter;

    private MaterialTab materialTab;

    public ImageryResponseViewImpl(ClientFactoryImpl clientFactory) {
        super(clientFactory);
    }

    @Override
    public void setPresenter(ImageryResponseView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayImageryRequest(ImageryResponseDTO imageryServiceRequestDTO) {
        this.requestDescription.clear();
        addRequestValue("Image type", imageryServiceRequestDTO.getImageType());
        addRequestValue("Period of interest",
                DateUtils.formatTimePeriod(imageryServiceRequestDTO.getStart(), imageryServiceRequestDTO.getStop()));
        addRequestValue("Application", imageryServiceRequestDTO.getApplication());
        addRequestValue("Additional information", imageryServiceRequestDTO.getAdditionalInformation());
        displayAoI(AoIUtil.fromWKT(imageryServiceRequestDTO.getAoiWKT()));
        List<ImagerySupplierResponseDTO> responses = imageryServiceRequestDTO.getSupplierResponses();
        tabs.clear();
        if(responses.size() > 1) {
            tabs.setVisible(true);
            materialTab = new MaterialTab();
            for(final ImagerySupplierResponseDTO imagerySupplierResponseDTO : responses) {
                MaterialTabItem materialTabItem = new MaterialTabItem();
                materialTabItem.setId(imagerySupplierResponseDTO.getId() + "");
                materialTab.add(materialTabItem);
                MaterialLink materialLink = new MaterialLink(imagerySupplierResponseDTO.getCompany().getName());
                materialLink.setHref("#" + imagerySupplierResponseDTO.getId());
                materialTabItem.add(materialLink);
                materialLink.addClickHandler(new ClickHandler() {

                    @Override
                    public void onClick(ClickEvent event) {
                        presenter.responseSelected(imagerySupplierResponseDTO);
                    }
                });
                MaterialRow materialRow = new MaterialRow();
                materialRow.setId(imagerySupplierResponseDTO.getId() + "");
                tabs.add(materialRow);
            }
            tabs.add(materialTab);
            materialTab.onLoad();
        } else {
            tabs.setVisible(false);
            displayImageryResponse(responses.get(0));
        }
    }

    @Override
    public void displayImageryResponse(ImagerySupplierResponseDTO imagerySupplierResponseDTO) {
        if(tabs.isVisible() && materialTab != null) {
            // make sure we select the tab
            materialTab.selectTab(imagerySupplierResponseDTO.getId() + "");
        }
        displayResponse(imagerySupplierResponseDTO.getResponse());
        displayMessages(imagerySupplierResponseDTO.getMessages());
    }

}