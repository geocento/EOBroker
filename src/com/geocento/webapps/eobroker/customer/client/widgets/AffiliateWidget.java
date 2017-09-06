package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
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
public class AffiliateWidget extends Composite {

    interface AffiliateWidgetUiBinder extends UiBinder<Widget, AffiliateWidget> {
    }

    private static AffiliateWidgetUiBinder ourUiBinder = GWT.create(AffiliateWidgetUiBinder.class);

    @UiField
    MaterialImageLoading imagePanel;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLink remove;

    private final CompanyDTO companyDTO;

    public AffiliateWidget(CompanyDTO companyDTO) {

        this.companyDTO = companyDTO;

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.companies));

        imagePanel.setImageUrl(companyDTO.getIconURL());
        imagePanel.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + companyDTO.getId())));

        title.setText(companyDTO.getName());
        description.setText(companyDTO.getDescription());
    }

    public HasClickHandlers getRemove() {
        return remove;
    }

    public CompanyDTO getCompanyDTO() {
        return companyDTO;
    }
}