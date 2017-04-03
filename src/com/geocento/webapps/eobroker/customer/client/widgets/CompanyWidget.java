package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLoadingLink;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FollowCompany;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialTooltip;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

/**
 * Created by thomas on 09/06/2016.
 */
public class CompanyWidget extends Composite {

    interface CompanyWidgetUiBinder extends UiBinder<Widget, CompanyWidget> {
    }

    private static CompanyWidgetUiBinder ourUiBinder = GWT.create(CompanyWidgetUiBinder.class);

    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLoadingLink follow;
    @UiField
    MaterialImageLoading imagePanel;
    @UiField
    MaterialTooltip followTooltip;

    public CompanyWidget(CompanyDTO companyDTO) {

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.companies));

        imagePanel.setImageUrl(companyDTO.getIconURL());
        imagePanel.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + companyDTO.getId())));

        title.setText(companyDTO.getName());
        description.setText(companyDTO.getDescription());

        follow.addClickHandler(event -> {
            // manage directly instead
            Customer.clientFactory.getEventBus().fireEvent(new FollowCompany(true, companyDTO));

            try {
                follow.setLoading(true);
                REST.withCallback(new MethodCallback<Boolean>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        follow.setLoading(false);
                        Window.alert("Could not follow company please try again");
                    }

                    @Override
                    public void onSuccess(Method method, Boolean result) {
                        follow.setLoading(false);
                        companyDTO.setFollowing(result);
                        setFollowing(companyDTO.getFollowing());
                    }
                }).call(ServicesUtil.assetsService).followCompany(companyDTO.getId(), !companyDTO.getFollowing());
            } catch (Exception e) {
                follow.setLoading(false);
            }
        });
        setFollowing(companyDTO.getFollowing());
    }

    private void setFollowing(boolean following) {
        follow.setText(following ? "FOLLOWING" : "FOLLOW");
        followTooltip.setText(following ? "Your are following this company, click to unfollow" : "Click to follow this company and be notified of changes in their offering");
        follow.setTextColor(following ? Color.AMBER : Color.WHITE);
    }
}