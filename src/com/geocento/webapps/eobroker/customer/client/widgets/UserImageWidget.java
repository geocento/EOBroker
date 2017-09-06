package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.DateUtil;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.shared.UserDTO;
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
public class UserImageWidget extends Composite {

    interface AffiliateWidgetUiBinder extends UiBinder<Widget, UserImageWidget> {
    }

    private static AffiliateWidgetUiBinder ourUiBinder = GWT.create(AffiliateWidgetUiBinder.class);

    @UiField
    MaterialImageLoading imagePanel;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLink contact;

    private final UserDTO userDTO;

    public UserImageWidget(UserDTO userDTO) {

        this.userDTO = userDTO;

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.companies));

        imagePanel.setImageUrl(userDTO.getIconURL());
        //imagePanel.addClickHandler(event -> Customer.clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + userDTO.getId())));

        title.setText(userDTO.getFullName() != null ? userDTO.getFullName() : userDTO.getName());
        description.setText("Registered since " + DateUtil.displayDateOnly(userDTO.getCreationDate()));
    }

}