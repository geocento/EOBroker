package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.ConversationPlace;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.places.ProductFeasibilityPlace;
import com.geocento.webapps.eobroker.customer.client.widgets.*;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.addins.client.masonry.MaterialMasonry;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.WavesType;
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

        String subsection();

        String tabPanel();
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
    @UiField
    HTMLPanel tags;

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
    public void displayCompany(final CompanyDescriptionDTO companyDescriptionDTO) {
        clearDetails();
        image.setUrl(companyDescriptionDTO.getIconURL());
        title.setTitle(companyDescriptionDTO.getName());
        title.setDescription(companyDescriptionDTO.getDescription());
        tags.clear();
        MaterialPanel badges = new MaterialPanel();
        badges.setPadding(10);
        MaterialChip email = new MaterialChip();
        email.setText("Contact");
        email.setBackgroundColor("grey");
        email.setTextColor("white");
        email.setLetterBackgroundColor("blue");
        email.setLetterColor("white");
        email.setIconType(IconType.CONTACT_MAIL);
        email.setMarginRight(20);
        email.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                template.getClientFactory().getPlaceController().goTo(
                        new ConversationPlace(
                                ConversationPlace.TOKENS.companyid.toString() + "=" + companyDescriptionDTO.getId() +
                                "&" + ConversationPlace.TOKENS.topic.toString() + "=Request for information"
                        ));
            }
        });
        badges.add(email);
        MaterialChip website = new MaterialChip();
        website.setText("Website");
        website.setBackgroundColor("grey");
        website.setTextColor("white");
        website.setLetterBackgroundColor("green");
        website.setLetterColor("white");
        website.setIconType(IconType.WEB);
        website.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(companyDescriptionDTO.getWebsite(), "_blank", null);
            }
        });
        badges.add(website);
        tags.add(badges);
        MaterialTab materialTab = createTabs();
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);
        HTMLPanel fullDescriptionPanel = new HTMLPanel(companyDescriptionDTO.getFullDescription());
        addTab(materialTab, "Description", fullDescriptionPanel, size);
        // create tab panel for services
        HTMLPanel servicesPanel = null;
        if(companyDescriptionDTO.getProductServices().size() == 0) {
            servicesPanel = new HTMLPanel("<p class='" + style.subsection() + "'>This company does not provide on-demand services</p>");
        } else {
            servicesPanel = new HTMLPanel("<p class='" + style.subsection() + "'>On-demand services provided</p>");
            MaterialRow materialRow = new MaterialRow();
            servicesPanel.add(materialRow);
            for (ProductServiceDTO productServiceDTO : companyDescriptionDTO.getProductServices()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
                materialColumn.add(new ProductServiceWidget(productServiceDTO));
                materialRow.add(materialColumn);
            }
        }
        addTab(materialTab, "Offer (" + companyDescriptionDTO.getProductServices().size() + ")", servicesPanel, size);
        addTab(materialTab, "Others", new HTMLPanel(""), size);
        materialTab.selectTab("fullViewTab0");
        details.add(materialTab.getParent().getParent());
        details.add(new HTML("<p class='" + style.subsection() + "'>Other similar companies...</p>"));
        MaterialRow otherItemsRow = new MaterialRow();
        details.add(otherItemsRow);
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
    public void displayProductService(final ProductServiceDescriptionDTO productServiceDescriptionDTO) {
        clearDetails();
        image.setUrl(productServiceDescriptionDTO.getServiceImage());
        title.setTitle(productServiceDescriptionDTO.getName());
        title.setDescription(productServiceDescriptionDTO.getDescription());
        tags.clear();
        MaterialPanel badges = new MaterialPanel();
        badges.setPadding(10);
        MaterialChip company = new MaterialChip();
        final CompanyDTO companyDTO = productServiceDescriptionDTO.getCompany();
        company.setText(companyDTO.getName());
        company.setBackgroundColor("grey");
        company.setTextColor("white");
        company.setUrl(companyDTO.getIconURL());
        company.setMarginRight(20);
        company.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                template.getClientFactory().getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + companyDTO.getId()));
            }
        });
        company.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
        badges.add(company);
        MaterialChip product = new MaterialChip();
        final ProductDTO productDTO = productServiceDescriptionDTO.getProduct();
        product.setText(productDTO.getName());
        product.setBackgroundColor("grey");
        product.setTextColor("white");
        product.setUrl(productDTO.getImageUrl());
        product.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                template.getClientFactory().getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + productDTO.getId()));
            }
        });
        badges.add(product);
        tags.add(badges);
        MaterialTab materialTab = createTabs();
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);
        addTab(materialTab, "Description", new HTMLPanel(productServiceDescriptionDTO.getFullDescription()), size);
        // create tab panel for services
        MaterialRow featuresPanel = new MaterialRow();
        MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
        featuresPanel.add(materialColumn);
        // TODO - add list of features provided
        materialColumn.add(createSubsection("List of features provided"));
        // add map with extent of data
        materialColumn = new MaterialColumn(12, 6, 6);
        featuresPanel.add(materialColumn);
        materialColumn.add(createSubsection("Extent of service"));
        final MapContainer mapContainer = new MapContainer();
        mapContainer.setHeight("200px");
        mapContainer.displayEdit(false);
        mapContainer.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {

            }

            @Override
            public void onSuccess(Void result) {
            }
        });
        materialColumn.add(mapContainer);
        addTab(materialTab, "Features", featuresPanel, size);
        // create tab panel for services
        HTMLPanel termsAndConditionsPanel = new HTMLPanel("<p class='" + style.subsection() + "'>No terms and conditions specified</p>");
        addTab(materialTab, "Terms and Conditions", termsAndConditionsPanel, size);
        addTab(materialTab, "Others", new HTMLPanel(""), size);
        materialTab.selectTab("fullViewTab0");
        details.add(materialTab.getParent().getParent());
        details.add(new HTML("<p class='" + style.section() + "'>You might also be interested in...</p>"));
        MaterialRow otherItemsRow = new MaterialRow();
        details.add(otherItemsRow);
        otherItemsRow.add(new MaterialLabel("Add recommendations..."));
    }

    @Override
    public void displayProduct(ProductDescriptionDTO productDescriptionDTO) {
        clearDetails();
        image.setUrl(productDescriptionDTO.getImageUrl());
        title.setTitle(productDescriptionDTO.getName());
        title.setDescription(productDescriptionDTO.getShortDescription());
        tags.clear();
        MaterialPanel badges = new MaterialPanel();
        badges.setPadding(10);
        MaterialChip thematic = new MaterialChip();
        thematic.setText(productDescriptionDTO.getThematic().toString());
        thematic.setBackgroundColor("grey");
        thematic.setTextColor("white");
        thematic.setLetterBackgroundColor("blue");
        thematic.setLetterColor("white");
        thematic.setLetter("T");
        thematic.setMarginRight(20);
        badges.add(thematic);
        MaterialChip sector = new MaterialChip();
        sector.setText(productDescriptionDTO.getSector().toString());
        sector.setBackgroundColor("grey");
        sector.setTextColor("white");
        sector.setLetterBackgroundColor("green");
        sector.setLetterColor("white");
        sector.setLetter("S");
        badges.add(sector);
        tags.add(badges);
        MaterialTab materialTab = createTabs();
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);
        HTMLPanel fullDescriptionPanel = new HTMLPanel(productDescriptionDTO.getDescription());
        fullDescriptionPanel.getElement().getStyle().setPadding(20, com.google.gwt.dom.client.Style.Unit.PX);
        addTab(materialTab, "Description", fullDescriptionPanel, size);
        // create tab panel for services
        HTMLPanel servicesPanel = null;
        if(productDescriptionDTO.getProductServices().size() == 0) {
            servicesPanel = new HTMLPanel("<p class='" + style.subsection() + "'>No on-demand services are available for this product</p>" +
                    "<p>We are sorry but we do not have any supplier currently supporting on-demand generation of this product as a service</p>");
        } else {
            servicesPanel = new HTMLPanel("<p class='" + style.subsection() + "'>This product can be provided by the following on-demand services</p>");
            MaterialRow materialRow = new MaterialRow();
            servicesPanel.add(materialRow);
            for (ProductServiceDTO productServiceDTO : productDescriptionDTO.getProductServices()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
                materialColumn.add(new ProductServiceWidget(productServiceDTO));
                materialRow.add(materialColumn);
            }
        }
        addTab(materialTab, "On-demand (" + productDescriptionDTO.getProductServices().size() + ")", servicesPanel, size);
        // create tab panel for services
        HTMLPanel productDatasetPanel = null;
        if(productDescriptionDTO.getProductDatasets().size() == 0) {
            productDatasetPanel = new HTMLPanel("<p class='" + style.subsection() + "'>No off the shelf data is available for this product</p>" +
                    "<p>We are sorry but we do not have any supplier currently providing off the shelf data for this product</p>");
        } else {
            productDatasetPanel = new HTMLPanel("<p class='" + style.subsection() + "'>The following off the shelf data items implement this product</p>");
            MaterialRow materialRow = new MaterialRow();
            productDatasetPanel.add(materialRow);
            for (ProductDatasetDTO productDatasetDTO : productDescriptionDTO.getProductDatasets()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 4, 3);
                materialColumn.add(new ProductDatasetWidget(productDatasetDTO));
                materialRow.add(materialColumn);
            }
        }
        addTab(materialTab, "Off-the-shelf (" + productDescriptionDTO.getProductDatasets().size() + ")", productDatasetPanel, size);
        addTab(materialTab, "Others", new HTMLPanel(""), size);
        materialTab.selectTab("fullViewTab0");
        details.add(materialTab.getParent().getParent());
        details.add(new HTML("<p class='" + style.subsection() + "'>You might also be interested in...</p>"));
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

    private void addTab(MaterialTab materialTab, String name, Panel panel, int size) {
        String tabId = "fullViewTab" + materialTab.getWidgetCount();
        MaterialTabItem materialTabItem = new MaterialTabItem();
        materialTabItem.setWaves(WavesType.GREEN);
        materialTabItem.setGrid("s" + size + " m" + size + " l" + size);
        MaterialLink materialLink = new MaterialLink(name);
        materialLink.setHref("#" + tabId);
        materialTabItem.add(materialLink);
        materialTab.add(materialTabItem);
        MaterialRow materialRow = (MaterialRow) materialTab.getParent().getParent();
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        materialRow.add(materialColumn);
        materialColumn.add(panel);
        materialColumn.setId(tabId);
        panel.addStyleName(style.tabPanel());
    }

    private MaterialTab createTabs() {
        MaterialRow materialRow = new MaterialRow();
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        materialRow.add(materialColumn);
        MaterialTab materialTab = new MaterialTab();
        materialColumn.add(materialTab);
        return materialTab;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void displayProductDataset(final ProductDatasetDescriptionDTO productDatasetDescriptionDTO) {
        clearDetails();
        image.setUrl(productDatasetDescriptionDTO.getImageUrl());
        title.setTitle(productDatasetDescriptionDTO.getName());
        title.setDescription(productDatasetDescriptionDTO.getDescription());
        tags.clear();
        MaterialPanel badges = new MaterialPanel();
        badges.setPadding(10);
        MaterialChip company = new MaterialChip();
        final CompanyDTO companyDTO = productDatasetDescriptionDTO.getCompany();
        company.setText(companyDTO.getName());
        company.setBackgroundColor("grey");
        company.setTextColor("white");
        company.setUrl(companyDTO.getIconURL());
        company.setMarginRight(20);
        company.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                template.getClientFactory().getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + companyDTO.getId()));
            }
        });
        company.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
        badges.add(company);
        MaterialChip product = new MaterialChip();
        final ProductDTO productDTO = productDatasetDescriptionDTO.getProduct();
        product.setText(productDTO.getName());
        product.setBackgroundColor("grey");
        product.setTextColor("white");
        product.setUrl(productDTO.getImageUrl());
        product.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                template.getClientFactory().getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + productDTO.getId()));
            }
        });
        badges.add(product);
        tags.add(badges);
        MaterialTab materialTab = createTabs();
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);
        addTab(materialTab, "Description", new HTMLPanel(productDatasetDescriptionDTO.getFullDescription()), size);
        // create tab panel for services
        MaterialRow featuresPanel = new MaterialRow();
        MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
        featuresPanel.add(materialColumn);
        // TODO - add list of features provided
        materialColumn.add(createSubsection("List of features provided"));
        // add map with extent of data
        materialColumn = new MaterialColumn(12, 6, 6);
        featuresPanel.add(materialColumn);
        materialColumn.add(createSubsection("Extent of data"));
        final MapContainer mapContainer = new MapContainer();
        mapContainer.setHeight("200px");
        mapContainer.displayEdit(false);
        mapContainer.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {

            }

            @Override
            public void onSuccess(Void result) {
                mapContainer.displayAoI(AoIUtil.fromWKT(productDatasetDescriptionDTO.getExtent()));
            }
        });
        materialColumn.add(mapContainer);
        addTab(materialTab, "Features", featuresPanel, size);
        // create tab panel for services
        HTMLPanel termsAndConditionsPanel = new HTMLPanel("<p class='" + style.subsection() + "'>No terms and conditions specified</p>");
        addTab(materialTab, "Terms and Conditions", termsAndConditionsPanel, size);
        addTab(materialTab, "Others", new HTMLPanel(""), size);
        materialTab.selectTab("fullViewTab0");
        details.add(materialTab.getParent().getParent());
        details.add(new HTML("<p class='" + style.section() + "'>You might also be interested in...</p>"));
        MaterialRow otherItemsRow = new MaterialRow();
        details.add(otherItemsRow);
        otherItemsRow.add(new MaterialLabel("Add recommendations..."));
    }

    private Widget createSubsection(String message) {
        MaterialLabel label = new MaterialLabel(message);
        label.addStyleName(style.subsection());
        return label;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}