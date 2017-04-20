package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCard;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 09/06/2016.
 */
public class CompanyWidget extends Composite {

    interface CompanyWidgetUiBinder extends UiBinder<Widget, CompanyWidget> {
    }

    private static CompanyWidgetUiBinder ourUiBinder = GWT.create(CompanyWidgetUiBinder.class);

    @UiField
    MaterialImageLoading imagePanel;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField(provided = true)
    CompanyFollowWidget follow;
    @UiField
    MaterialLink offerings;

    public CompanyWidget(CompanyDTO companyDTO) {

        follow = new CompanyFollowWidget(companyDTO);

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.companies));

        imagePanel.setImageUrl(companyDTO.getIconURL());
        imagePanel.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + companyDTO.getId())));

        title.setText(companyDTO.getName());
        description.setText(companyDTO.getDescription());

        offerings.setHref(PlaceHistoryHelper.convertPlace(
                new FullViewPlace(
                        Utils.generateTokens(FullViewPlace.TOKENS.companyid.toString(), companyDTO.getId() + "",
                                FullViewPlace.TOKENS.tab.toString(), "offering"))));
    }

}