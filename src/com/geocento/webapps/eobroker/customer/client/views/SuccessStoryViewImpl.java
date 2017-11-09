package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.DateUtil;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLabelIcon;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialMessage;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.widgets.EndorsementWidget;
import com.geocento.webapps.eobroker.customer.shared.EndorsementDTO;
import com.geocento.webapps.eobroker.customer.shared.SuccessStoryDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialRow;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class SuccessStoryViewImpl extends Composite implements SuccessStoryView {

    interface TestimonialViewUiBinder extends UiBinder<Widget, SuccessStoryViewImpl> {
    }

    private static TestimonialViewUiBinder ourUiBinder = GWT.create(TestimonialViewUiBinder.class);

    static DateTimeFormat fmt = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH);

    @UiField
    HTMLPanel fullDescription;
    @UiField
    MaterialLabelIcon productCategory;
    @UiField
    MaterialLabelIcon company;
    @UiField
    MaterialRow endorsements;
    @UiField
    MaterialLabelIcon clientCompany;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialImageLoading iconUrl;
    @UiField
    MaterialLink date;

    private Presenter presenter;

    public SuccessStoryViewImpl(ClientFactoryImpl clientFactory) {

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displaySuccessStory(SuccessStoryDTO successStoryDTO) {
        iconUrl.setImageUrl(successStoryDTO.getImageUrl());
        title.setText(successStoryDTO.getName());
        company.setImageUrl(successStoryDTO.getCompany().getIconURL());
        company.setText(successStoryDTO.getCompany().getName());
        clientCompany.setImageUrl(successStoryDTO.getCustomer().getIconURL());
        clientCompany.setText(successStoryDTO.getCustomer().getName());
        productCategory.setImageUrl(successStoryDTO.getProductDTO().getImageUrl());
        productCategory.setText(successStoryDTO.getProductDTO().getName());
        if(successStoryDTO.getDate() != null) {
            date.setVisible(true);
            date.setText(fmt.format(successStoryDTO.getDate()));
        } else {
            date.setVisible(false);
        }
        fullDescription.add(new HTML(successStoryDTO.getDescription()));
        List<EndorsementDTO> endorsementDTOs = successStoryDTO.getEndorsements();
        endorsements.clear();
        if(endorsementDTOs == null || endorsementDTOs.size() == 0) {
            MaterialMessage materialMessage = new MaterialMessage();
            materialMessage.displayWarningMessage("This success story hasn't been endorsed by the client company yet");
            endorsements.add(materialMessage);
        } else {
            for(EndorsementDTO endorsementDTO : endorsementDTOs) {
                endorsements.add(new EndorsementWidget(endorsementDTO));
            }
        }
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}