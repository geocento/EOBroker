package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLoadingLink;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.shared.SoftwareDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
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
public class SoftwareWidget extends Composite {

    interface SoftwareUiBinder extends UiBinder<Widget, SoftwareWidget> {
    }

    private static SoftwareUiBinder ourUiBinder = GWT.create(SoftwareUiBinder.class);

    @UiField
    MaterialLink companyLogo;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel shortDescription;
    @UiField
    MaterialImageLoading imagePanel;
    @UiField
    MaterialTooltip followTooltip;
    @UiField
    MaterialLoadingLink follow;

    public SoftwareWidget(SoftwareDTO softwareDTO) {

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.software));

        imagePanel.setImageUrl(softwareDTO.getImageUrl());
        imagePanel.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.softwareid.toString() + "=" + softwareDTO.getId())));

        Image logoImage = new Image(softwareDTO.getCompanyDTO().getIconURL());
        logoImage.setHeight("20px");
        companyLogo.add(logoImage);

        title.setText(softwareDTO.getName());
        shortDescription.setText(softwareDTO.getDescription());
        companyLogo.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + softwareDTO.getCompanyDTO().getId())));

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
                        softwareDTO.setFollowing(result);
                        setFollowing(softwareDTO.getFollowing());
                    }
                }).call(ServicesUtil.assetsService).followSoftware(softwareDTO.getId(), !softwareDTO.getFollowing());
            } catch (Exception e) {
                follow.setLoading(false);
            }
        });
        setFollowing(softwareDTO.getFollowing());
    }

    private void setFollowing(boolean following) {
        follow.setText(following ? "FOLLOWING" : "FOLLOW");
        followTooltip.setText(following ? "Your are following this software solution, click to unfollow" : "Click to follow this software solution and be notified of changes");
        follow.setTextColor(following ? Color.AMBER : Color.WHITE);
    }
}