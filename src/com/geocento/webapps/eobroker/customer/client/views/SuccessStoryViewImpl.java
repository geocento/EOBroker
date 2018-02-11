package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLabelIcon;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialMessage;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.ConversationPlace;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.widgets.EndorsementWidget;
import com.geocento.webapps.eobroker.customer.client.widgets.ProductDatasetWidget;
import com.geocento.webapps.eobroker.customer.client.widgets.ProductServiceWidget;
import com.geocento.webapps.eobroker.customer.client.widgets.SoftwareWidget;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class SuccessStoryViewImpl extends Composite implements SuccessStoryView {

    interface SuccessStoryViewUiBinder extends UiBinder<Widget, SuccessStoryViewImpl> {
    }

    private static SuccessStoryViewUiBinder ourUiBinder = GWT.create(SuccessStoryViewUiBinder.class);

    static DateTimeFormat fmt = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH);

    @UiField
    HTMLPanel fullDescription;
    @UiField
    MaterialRow endorsements;
    @UiField
    MaterialLabelIcon clientCompany;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialImageLoading iconUrl;
    @UiField
    MaterialLabel date;
    @UiField
    MaterialRow offeringsUsed;
    @UiField
    MaterialPanel colorPanel;
    @UiField
    MaterialNavBar navigation;
    @UiField
    MaterialIcon endorsementsIcon;
    @UiField
    MaterialPanel actions;

    private Presenter presenter;

    public SuccessStoryViewImpl(ClientFactoryImpl clientFactory) {

        initWidget(ourUiBinder.createAndBindUi(this));

        colorPanel.setBackgroundColor(CategoryUtils.getColor(Category.successStories));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displaySuccessStory(SuccessStoryDTO successStoryDTO) {
        navigation.clear();
        addBreadcrumb(successStoryDTO.getCompany());
        addBreadcrumb(successStoryDTO.getProductDTO());
        iconUrl.setImageUrl(successStoryDTO.getImageUrl());
        title.setText(successStoryDTO.getName());
        clientCompany.setImageUrl(successStoryDTO.getCustomer().getIconURL());
        clientCompany.setText(successStoryDTO.getCustomer().getName());
        boolean hasDate = successStoryDTO.getPeriod() != null;
        date.setVisible(hasDate);
        if(hasDate) {
            date.setText(successStoryDTO.getPeriod());
        }
        actions.clear();
        addAction("CONTACT", "#" + PlaceHistoryHelper.convertPlace(
                new ConversationPlace(
                        Utils.generateTokens(
                                ConversationPlace.TOKENS.companyid.toString(), successStoryDTO.getCompany().getId() + "",
                                ConversationPlace.TOKENS.topic.toString(), "Information on success story '" + successStoryDTO.getName() + "'"))));

        fullDescription.clear();
        fullDescription.add(new HTML(successStoryDTO.getDescription()));
        List<EndorsementDTO> endorsementDTOs = successStoryDTO.getEndorsements();
        endorsements.clear();
        boolean hasEndorsements = !ListUtil.isNullOrEmpty(endorsementDTOs);
        endorsementsIcon.setVisible(hasEndorsements);
        if(hasEndorsements) {
            MaterialMessage materialMessage = new MaterialMessage();
            materialMessage.displayWarningMessage("This success story hasn't been endorsed by the client company yet");
            endorsements.add(materialMessage);
        } else {
            for(EndorsementDTO endorsementDTO : endorsementDTOs) {
                endorsements.add(new EndorsementWidget(endorsementDTO));
            }
        }
        offeringsUsed.clear();
        for(ProductDatasetDTO productDatasetDTO : successStoryDTO.getDatasets()) {
            addDatasetOffering(productDatasetDTO);
        }
        for(ProductServiceDTO productServiceDTO : successStoryDTO.getServices()) {
            addServiceOffering(productServiceDTO);
        }
        for(SoftwareDTO softwareDTO : successStoryDTO.getSoftware()) {
            addSoftwareOffering(softwareDTO);
        }
    }

    private void addOffering(Widget widget) {
        MaterialColumn materialColumn = new MaterialColumn(12, 4, 3);
        materialColumn.add(widget);
        offeringsUsed.add(materialColumn);
    }

    private void addServiceOffering(ProductServiceDTO serviceDTO) {
        addOffering(new ProductServiceWidget(serviceDTO));
    }

    private void addDatasetOffering(ProductDatasetDTO productDatasetDTO) {
        addOffering(new ProductDatasetWidget(productDatasetDTO));
    }

    private void addSoftwareOffering(SoftwareDTO softwareDTO) {
        addOffering(new SoftwareWidget(softwareDTO));
    }

    // TODO - add to a generic class
    private void addBreadcrumb(Object dto) {
        navigation.setVisible(true);
        MaterialBreadcrumb materialBreadcrumb = new MaterialBreadcrumb();
        Color color = Color.WHITE; //CategoryUtils.getColor(category);
        materialBreadcrumb.setTextColor(color);
        materialBreadcrumb.setIconColor(color);
        String token = "";
        IconType iconType = IconType.ERROR;
        String text = "Unknown";
        String id = null;
        if(dto instanceof CompanyDTO) {
            token = FullViewPlace.TOKENS.companyid.toString();
            iconType = CategoryUtils.getIconType(Category.companies);
            text = ((CompanyDTO) dto).getName();
            id = ((CompanyDTO) dto).getId() + "";
        } else if(dto instanceof ProductDTO) {
            token = FullViewPlace.TOKENS.productid.toString();
            iconType = CategoryUtils.getIconType(Category.products);
            text = ((ProductDTO) dto).getName();
            id = ((ProductDTO) dto).getId() + "";
        }
        materialBreadcrumb.setIconType(iconType);
        materialBreadcrumb.setText(text);
        materialBreadcrumb.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(token + "=" + id)));
        navigation.add(materialBreadcrumb);
    }

    private void addAction(String label, String url) {
        MaterialAnchorButton materialAnchorButton = addAction(label);
        materialAnchorButton.setHref(url);
    }

    private void addAction(String label, ClickHandler clickHandler) {
        MaterialAnchorButton materialAnchorButton = addAction(label);
        materialAnchorButton.addClickHandler(clickHandler);
    }

    private MaterialAnchorButton addAction(String label) {
        MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton(label);
        materialAnchorButton.setMarginLeft(20);
        actions.add(materialAnchorButton);
        return materialAnchorButton;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}