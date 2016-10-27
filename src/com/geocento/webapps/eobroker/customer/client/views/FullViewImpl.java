package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.ConversationPlace;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.places.ProductFeasibilityPlace;
import com.geocento.webapps.eobroker.customer.client.widgets.*;
import com.geocento.webapps.eobroker.customer.shared.CompanyDescriptionDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDescriptionDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceDescriptionDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.masonry.MaterialMasonry;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 09/05/2016.
 */
public class FullViewImpl extends Composite implements FullView {

    private Presenter presenter;

    interface DummyUiBinder extends UiBinder<Widget, FullViewImpl> {
    }

    private static DummyUiBinder ourUiBinder = GWT.create(DummyUiBinder.class);

    static interface Style extends CssResource {

        String section();
    }

    @UiField Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    HTMLPanel details;
    @UiField
    MaterialTitle title;
    @UiField
    MaterialImage image;

    public FullViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        template.setTitleText("Product form");
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void hideLoading() {
        template.hideLoading();
    }

    @Override
    public void displayError(String message) {
        template.displayError(message);
    }

    @Override
    public void displaySuccess(String message) {
        template.displaySuccess(message);
    }

    @Override
    public void displayLoading() {
        template.displayLoading();
    }

    @Override
    public void displayCompany(CompanyDescriptionDTO companyDescriptionDTO) {
        clearDetails();
        image.setUrl(companyDescriptionDTO.getIconURL());
        title.setTitle(companyDescriptionDTO.getName());
        title.setDescription(companyDescriptionDTO.getDescription());
        MaterialRow materialRow = new MaterialRow();
        {
            MaterialAnchorButton materialLink = new MaterialAnchorButton("Contact");
            materialLink.setBackgroundColor("blue");
            materialLink.setTextColor("white");
            materialLink.setMargin(4);
            materialLink.setHref("#" + PlaceHistoryHelper.convertPlace(
                    new ConversationPlace(
                            ConversationPlace.TOKENS.companyid.toString() + "=" + companyDescriptionDTO.getId() + "&" +
                                    ConversationPlace.TOKENS.topic.toString() + "=" + "Information on your company")));
            materialRow.add(materialLink);
        }
        {
            MaterialAnchorButton materialLink = new MaterialAnchorButton("Website");
            materialLink.setBackgroundColor("blue");
            materialLink.setTextColor("white");
            materialLink.setMargin(4);
            materialLink.setHref(companyDescriptionDTO.getWebsite());
            materialLink.setTarget("_blank;");
            materialRow.add(materialLink);
        }
        details.add(materialRow);
        details.add(new HTML("<p class='" + style.section() + "'>Full description</p>"));
        details.add(new HTMLPanel(companyDescriptionDTO.getFullDescription()));
        details.add(new HTML("<p class='" + style.section() + "'>Services provided</p>"));
        materialRow = new MaterialRow();
        details.add(materialRow);
        for(ProductServiceDTO productServiceDTO : companyDescriptionDTO.getProductServices()) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
            materialColumn.add(new ProductServiceWidget(productServiceDTO));
            materialRow.add(materialColumn);
        }
    }

    @Override
    public void clearDetails() {
        details.clear();
    }

    @Override
    public void displayTitle(String title) {
        template.setTitleText(title);
    }

    @Override
    public void displayProductService(ProductServiceDescriptionDTO productServiceDescriptionDTO) {
        clearDetails();
        image.setUrl(productServiceDescriptionDTO.getServiceImage());
        title.setTitle(productServiceDescriptionDTO.getName());
        title.setDescription(productServiceDescriptionDTO.getDescription());
        CompanyDTO companyDTO = productServiceDescriptionDTO.getCompany();
        details.add(new HTMLPanel("<p style='line-height: 40px; font-style: italic;'>This service is provided by <img style='vertical-align: middle; margin: 0px 10px;' src='" + companyDTO.getIconURL() + "' height='40px'/><a href='#" +
                PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + companyDTO.getId()))
        + "' target='_blank;'>" + companyDTO.getName() + "</a></p>"));
        MaterialRow materialRow = new MaterialRow();
        {
            MaterialAnchorButton materialLink = new MaterialAnchorButton("Contact");
            materialLink.setBackgroundColor("blue");
            materialLink.setTextColor("white");
            materialLink.setMargin(4);
            materialLink.setHref("#" + PlaceHistoryHelper.convertPlace(
                    new ConversationPlace(
                            ConversationPlace.TOKENS.companyid.toString() + "=" + companyDTO.getId() + "&" +
                            ConversationPlace.TOKENS.topic.toString() + "=" + "Information on product service '" + productServiceDescriptionDTO.getName() + "'")));
            materialRow.add(materialLink);
        }
        {
            MaterialAnchorButton materialLink = new MaterialAnchorButton("Website");
            materialLink.setBackgroundColor("blue");
            materialLink.setTextColor("white");
            materialLink.setMargin(4);
            materialLink.setHref(productServiceDescriptionDTO.getWebsite());
            materialLink.setTarget("_blank;");
            materialRow.add(materialLink);
        }
        if(productServiceDescriptionDTO.isHasFeasibility()) {
            MaterialAnchorButton materialLink = new MaterialAnchorButton("Simulate");
            materialLink.setBackgroundColor("blue");
            materialLink.setTextColor("white");
            materialLink.setMargin(4);
            materialLink.setHref("#" +
                    PlaceHistoryHelper.convertPlace(
                            new ProductFeasibilityPlace(ProductFeasibilityPlace.TOKENS.productservice.toString() + "=" + productServiceDescriptionDTO.getId())));
            materialRow.add(materialLink);
        }
        details.add(materialRow);
        details.add(new HTML("<p class='" + style.section() + "'>Full description</p>"));
        details.add(new HTMLPanel(productServiceDescriptionDTO.getFullDescription()));
/*
        details.add(new HTMLPanel("<dl>" +
                "<dt><b>More Information</b>:</dt><dd>go to <a href='" + productServiceDescriptionDTO.getWebsite() + "' target='_blank'>service website</a></dd>" +
                "</dl>"));
*/
        details.add(new HTML("<p class='" + style.section() + "'>This service provides the following standard product</p>"));
        MaterialMasonry materialMasonry = new MaterialMasonry();
        details.add(materialMasonry);
        MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
        materialColumn.add(new ProductWidget(productServiceDescriptionDTO.getProduct()));
        materialMasonry.add(materialColumn);
/*
        details.add(new HTML("<p class='" + style.section() + "'>This service is provided by the following company</p>"));
        materialMasonry = new MaterialMasonry();
        details.add(materialMasonry);
        materialColumn = new MaterialColumn(12, 6, 4);
        materialColumn.add(new CompanyWidget(productServiceDescriptionDTO.getCompany()));
        materialMasonry.add(materialColumn);
*/
    }

    @Override
    public void displayProduct(ProductDescriptionDTO productDescriptionDTO) {
        clearDetails();
        image.setUrl(productDescriptionDTO.getImageUrl());
        title.setTitle(productDescriptionDTO.getName());
        title.setDescription(productDescriptionDTO.getShortDescription());
        details.add(new HTMLPanel(productDescriptionDTO.getDescription()));
        MaterialPanel badges = new MaterialPanel();
        badges.setPadding(10);
/*
        MaterialLabel materialLabel = new MaterialLabel("Thematic");
        badges.add(materialLabel);
*/
        MaterialButton thematic = new MaterialButton(ButtonType.RAISED, "Thematic: " + productDescriptionDTO.getThematic().toString(), null);
        thematic.setMarginRight(20);
        badges.add(thematic);
        MaterialButton sector = new MaterialButton(ButtonType.RAISED, "Sector: " + productDescriptionDTO.getSector().toString(), null);
        badges.add(sector);
        //badges.add(new MaterialBadge("Sector: " + productDescriptionDTO.getSector().toString(), "white", "blue"));
        details.add(badges);
        if(productDescriptionDTO.getProductServices().size() == 0) {
            details.add(new HTML("<p class='" + style.section() + "'>No on-demand services are available for this product</p>"));
            details.add(new HTML("<p>We are sorry but we do not have any supplier currently supporting on-demand generation of this product as a service</p>"));
        } else {
            details.add(new HTML("<p class='" + style.section() + "'>This product can be provided by the following on-demand services</p>"));
            MaterialRow materialRow = new MaterialRow();
            details.add(materialRow);
            for (ProductServiceDTO productServiceDTO : productDescriptionDTO.getProductServices()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
                materialColumn.add(new ProductServiceWidget(productServiceDTO));
                materialRow.add(materialColumn);
            }
        }
        details.add(new HTML("<p class='" + style.section() + "'>You might also be interested in...</p>"));
        MaterialRow otherItemsRow = new MaterialRow();
        details.add(otherItemsRow);
        if(productDescriptionDTO.hasImageRule()) {
            MaterialColumn serviceColumn = new MaterialColumn(12, 6, 4);
            otherItemsRow.add(serviceColumn);
            serviceColumn.add(new ImageSearchWidget(productDescriptionDTO.getName()));
            serviceColumn = new MaterialColumn(12, 6, 4);
            otherItemsRow.add(serviceColumn);
            serviceColumn.add(new ImageRequestWidget(productDescriptionDTO.getName()));
        }
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}