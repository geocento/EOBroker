package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.supplier.shared.dtos.CompanyRoleDTO;
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
public class CompanyRoleWidget extends Composite {

    interface CompanyWidgetUiBinder extends UiBinder<Widget, CompanyRoleWidget> {
    }

    private static CompanyWidgetUiBinder ourUiBinder = GWT.create(CompanyWidgetUiBinder.class);

    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLink remove;
    @UiField
    MaterialImageLoading imagePanel;

    private CompanyRoleDTO companyRole;

    public CompanyRoleWidget(CompanyRoleDTO companyRoleDTO) {

        this.companyRole = companyRoleDTO;

        initWidget(ourUiBinder.createAndBindUi(this));

        ((MaterialCard) getWidget()).setBackgroundColor(CategoryUtils.getColor(Category.companies));

        imagePanel.setImageUrl(companyRoleDTO.getCompanyDTO().getIconURL());
        title.setText(companyRoleDTO.getCompanyDTO().getName());
        description.setText(companyRoleDTO.getRole());
    }

    public CompanyRoleDTO getCompanyRole() {
        return companyRole;
    }

    public HasClickHandlers getRemove() {
        return remove;
    }

}