package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 09/06/2016.
 */
public class ProductServiceWidget extends Composite {

    interface ProductServiceUiBinder extends UiBinder<MaterialCard, ProductServiceWidget> {
    }

    private static ProductServiceUiBinder ourUiBinder = GWT.create(ProductServiceUiBinder.class);

    @UiField
    MaterialLink companyLogo;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel shortDescription;
    @UiField
    MaterialImageLoading imagePanel;
    @UiField
    MaterialLink info;

    public ProductServiceWidget(ProductServiceDTO productServiceDTO) {

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.productservices));

        imagePanel.setImageUrl(productServiceDTO.getServiceImage());
        imagePanel.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.productserviceid.toString() + "=" + productServiceDTO.getId())));

        Image logoImage = new Image(productServiceDTO.getCompanyLogo());
        companyLogo.add(logoImage);

        title.setText(productServiceDTO.getName());
        shortDescription.setText(productServiceDTO.getDescription());
        companyLogo.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + productServiceDTO.getCompanyId())));
        info.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.productserviceid.toString() + "=" + productServiceDTO.getId())));
    }

}