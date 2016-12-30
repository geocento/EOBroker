package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.shared.requests.ImageryResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ImagerySupplierResponseDTO;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImageryResponseViewImpl extends RequestViewImpl implements ImageryResponseView {

    private ImageryResponseView.Presenter presenter;

    public ImageryResponseViewImpl(ClientFactoryImpl clientFactory) {
        super(clientFactory);

        setCategory(Category.imagery);
    }

    @Override
    public void setPresenter(ImageryResponseView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayImageryRequest(ImageryResponseDTO imageryServiceRequestDTO) {
        resetTabs();
        setStatus(imageryServiceRequestDTO.getStatus());
        this.requestDescription.clear();
        addRequestValue("Image type", imageryServiceRequestDTO.getImageType());
        addRequestValue("Period of interest",
                DateUtils.formatTimePeriod(imageryServiceRequestDTO.getStart(), imageryServiceRequestDTO.getStop()));
        addRequestValue("Application", imageryServiceRequestDTO.getApplication());
        addRequestValue("Additional information", imageryServiceRequestDTO.getAdditionalInformation());
        displayAoI(AoIUtil.fromWKT(imageryServiceRequestDTO.getAoiWKT()));
        List<ImagerySupplierResponseDTO> responses = imageryServiceRequestDTO.getSupplierResponses();
        resetTabs();
        for(final ImagerySupplierResponseDTO imagerySupplierResponseDTO : responses) {
            addResponseTab(imagerySupplierResponseDTO.getId() + "", imagerySupplierResponseDTO.getCompany().getName(), new ClickHandler() {

                @Override
                public void onClick(ClickEvent event) {
                    presenter.responseSelected(imagerySupplierResponseDTO);
                }
            });
        }
    }

    @Override
    public void displayImageryResponse(ImagerySupplierResponseDTO imagerySupplierResponseDTO) {
        if(imagerySupplierResponseDTO == null) {
            selectTab("request");
        } else {
            // make sure we select the tab
            selectTab(imagerySupplierResponseDTO.getId() + "");
            displayResponseSupplier(imagerySupplierResponseDTO.getCompany().getIconURL(), imagerySupplierResponseDTO.getCompany().getName());
            displayResponse(imagerySupplierResponseDTO.getResponse());
            displayMessages(imagerySupplierResponseDTO.getMessages());
        }
    }

}