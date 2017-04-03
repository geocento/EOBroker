package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLoadingLink;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialTooltip;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

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
    MaterialLoadingLink follow;
    @UiField
    MaterialTooltip followTooltip;

    public ProductServiceWidget(ProductServiceDTO productServiceDTO) {

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.productservices));

        imagePanel.setImageUrl(productServiceDTO.getServiceImage());
        imagePanel.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.productserviceid.toString() + "=" + productServiceDTO.getId())));

        Image logoImage = new Image(productServiceDTO.getCompanyLogo());
        logoImage.setHeight("20px");
        companyLogo.add(logoImage);

        title.setText(productServiceDTO.getName());
        shortDescription.setText(productServiceDTO.getDescription());
        companyLogo.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + productServiceDTO.getCompanyId())));

        follow.addClickHandler(event -> {
            try {
                follow.setLoading(true);
                REST.withCallback(new MethodCallback<Boolean>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        follow.setLoading(false);
                        Window.alert("Could not follow on-demand service please try again");
                    }

                    @Override
                    public void onSuccess(Method method, Boolean result) {
                        follow.setLoading(false);
                        productServiceDTO.setFollowing(result);
                        setFollowing(productServiceDTO.getFollowing());
                    }
                }).call(ServicesUtil.assetsService).followProductService(productServiceDTO.getId(), !productServiceDTO.getFollowing());
            } catch (Exception e) {
                follow.setLoading(false);
            }
        });
        setFollowing(productServiceDTO.getFollowing());
    }

    private void setFollowing(boolean following) {
        follow.setText(following ? "FOLLOWING" : "FOLLOW");
        followTooltip.setText(following ? "Your are following this on-demand service, click to unfollow" : "Click to follow this on-demand and be notified of changes");
        follow.setTextColor(following ? Color.AMBER : Color.WHITE);
    }
}