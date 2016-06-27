package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.widgets.CompanyWidget;
import com.geocento.webapps.eobroker.customer.client.widgets.ProductServiceWidget;
import com.geocento.webapps.eobroker.customer.client.widgets.ProductWidget;
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
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialTitle;

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
        details.add(new HTMLPanel(companyDescriptionDTO.getFullDescription()));
        details.add(new HTMLPanel("<dl>" +
                "<dt><b>Contact</b>:</dt><dd><a>Message</a> or direct email at <a href='mailto:" + companyDescriptionDTO.getContactEmail() + "'>" + companyDescriptionDTO.getContactEmail() + "</a></dd>" +
                "<dt><b>Company website</b>:</dt><dd><a href='" + companyDescriptionDTO.getWebsite() + "' target='_blank'>" + companyDescriptionDTO.getWebsite() + "</a></dd>" +
                "</dl>"));
        details.add(new HTML("<p class='" + style.section() + "'>Services provided</p>"));
        for(ProductServiceDTO productServiceDTO : companyDescriptionDTO.getProductServices()) {
            details.add(new ProductServiceWidget(productServiceDTO));
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
        details.add(new HTMLPanel(productServiceDescriptionDTO.getFullDescription()));
        details.add(new HTMLPanel("<dl>" +
                "<dt><b>More Information</b>:</dt><dd>" + productServiceDescriptionDTO.getWebsite() + "</dd>" +
                "</dl>"));
        details.add(new HTML("<p class='" + style.section() + "'>This service provides the following standard product</p>"));
        details.add(new ProductWidget(productServiceDescriptionDTO.getProduct()));
        details.add(new HTML("<p class='" + style.section() + "'>This service is provided by the following company</p>"));
        details.add(new CompanyWidget(productServiceDescriptionDTO.getCompany()));
    }

    @Override
    public void displayProduct(ProductDescriptionDTO productDescriptionDTO) {
        clearDetails();
        image.setUrl(productDescriptionDTO.getImageUrl());
        title.setTitle(productDescriptionDTO.getName());
        title.setDescription(productDescriptionDTO.getShortDescription());
        details.add(new HTMLPanel(productDescriptionDTO.getDescription()));
        details.add(new HTMLPanel("<dl>" +
                "<dt><b>Thematic</b>:</dt><dd>" + productDescriptionDTO.getThematic().toString() + "</dd>" +
                "<dt><b>Sector</b>:</dt><dd>" + productDescriptionDTO.getSector().toString() + "</dd>" +
                "<dt><b>More Information</b>:</dt><dd>go to <a href='" + productDescriptionDTO.getWebsite() + "' target='_blank'>service website</a></dd>" +
                "</dl>"));
        details.add(new HTML("<p class='" + style.section() + "'>This product can be provided by the following services</p>"));
        for(ProductServiceDTO productServiceDTO : productDescriptionDTO.getProductServices()) {
            details.add(new ProductServiceWidget(productServiceDTO));
        }
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}