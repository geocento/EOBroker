package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.*;
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
import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.constants.WavesType;
import gwt.material.design.client.ui.*;

import java.util.List;

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

        String vertical();
    }

    @UiField Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialRow details;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialImage image;
    @UiField
    HTMLPanel tags;
    @UiField
    MaterialLabel description;

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
    public void clearDetails() {
        details.clear();
    }

    @Override
    public void displayTitle(String title) {
        template.setTitleText(title);
    }

    @Override
    public void displayProduct(ProductDescriptionDTO productDescriptionDTO) {
        clearDetails();
        image.setUrl(productDescriptionDTO.getImageUrl());
        title.setText(productDescriptionDTO.getName());
        description.setText(productDescriptionDTO.getShortDescription());
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
        // add actions
        {
            MaterialPanel actionsPanel = new MaterialPanel();
            details.add(actionsPanel);
            MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton("FOLLOW");
            materialAnchorButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.alert("TODO...");
                }
            });
            actionsPanel.add(materialAnchorButton);
            materialAnchorButton.setMargin(20);
        }
        // add the tabs now
        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);
        HTMLPanel fullDescriptionPanel = new HTMLPanel(productDescriptionDTO.getDescription());
        fullDescriptionPanel.getElement().getStyle().setPadding(20, com.google.gwt.dom.client.Style.Unit.PX);
        addTab(materialTab, tabsPanel, "Description", fullDescriptionPanel, size);
        // create tab panel for services
        MaterialPanel servicesPanel = new MaterialPanel();
        if(productDescriptionDTO.getProductServices().size() == 0) {
            servicesPanel.add(createSubsection("No on-demand services are available for this product"));
            servicesPanel.add(new HTML("<p>We are sorry but we do not have any supplier currently supporting on-demand generation of this product as a service</p>"));
        } else {
            servicesPanel.add(createSubsection("This product can be provided by the following on-demand services"));
            MaterialRow materialRow = new MaterialRow();
            servicesPanel.add(materialRow);
            for (ProductServiceDTO productServiceDTO : productDescriptionDTO.getProductServices()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                ProductServiceWidget productServiceWidget = new ProductServiceWidget(productServiceDTO);
/*
                productServiceWidget.displayQuote(true);
*/
                materialColumn.add(productServiceWidget);
                materialRow.add(materialColumn);
            }
            MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton();
            materialAnchorButton.setText("Request quote");
            materialAnchorButton.setHref("#" + PlaceHistoryHelper.convertPlace(new ProductFormPlace(productDescriptionDTO.getId())));
            servicesPanel.add(materialAnchorButton);
        }
        addTab(materialTab, tabsPanel, "On-demand (" + productDescriptionDTO.getProductServices().size() + ")", servicesPanel, size);
        // create tab panel for services
        MaterialPanel productDatasetPanel = new MaterialPanel();
        if(productDescriptionDTO.getProductDatasets().size() == 0) {
            productDatasetPanel.add(createSubsection("No off the shelf data is available for this product"));
            productDatasetPanel.add(new HTML("<p>We are sorry but we do not have any supplier currently providing off the shelf data for this product</p>"));
        } else {
            productDatasetPanel.add(createSubsection("The following off the shelf data items implement this product"));
            MaterialRow materialRow = new MaterialRow();
            productDatasetPanel.add(materialRow);
            for (ProductDatasetDTO productDatasetDTO : productDescriptionDTO.getProductDatasets()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 4, 3);
                materialColumn.add(new ProductDatasetWidget(productDatasetDTO));
                materialRow.add(materialColumn);
            }
        }
        addTab(materialTab, tabsPanel, "Off-the-shelf (" + productDescriptionDTO.getProductDatasets().size() + ")", productDatasetPanel, size);
        // add the others panel
        int othersCount = 0;
        HTMLPanel othersPanel = new HTMLPanel("");
        MaterialRow othersRow = new MaterialRow();
        othersPanel.add(othersRow);
        othersRow.add(createSubsection("Software solutions"));
        othersRow.add(new MaterialLabel("TODO..."));
        othersRow.add(createSubsection("Projects"));
        othersRow.add(new MaterialLabel("TODO..."));
        if(productDescriptionDTO.hasImageRule()) {
            othersRow.add(createSubsection("Search or request imagery for '" + productDescriptionDTO.getName() + "'"));
            {
                MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton();
                materialAnchorButton.setText("Search imagery");
                materialAnchorButton.setHref("#" + PlaceHistoryHelper.convertPlace(new ImageSearchPlace(Utils.generateTokens(ImageSearchPlace.TOKENS.product.toString(), productDescriptionDTO.getId() + ""))));
                othersRow.add(materialAnchorButton);
            }
            {
                MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton();
                materialAnchorButton.setText("Request imagery");
                materialAnchorButton.setHref("#" + PlaceHistoryHelper.convertPlace(new RequestImageryPlace()));
                othersRow.add(materialAnchorButton);
            }
        }
        addTab(materialTab, tabsPanel, "Others (" + othersCount + ")", othersPanel, size);
        details.add(tabsPanel);
        materialTab.selectTab("fullViewTab0");
        // add suggestions
        addColumnSection("Similar products of interest");
        addColumnLine(new MaterialLabel("TODO..."));
    }

    @Override
    public void displayProductService(final ProductServiceDescriptionDTO productServiceDescriptionDTO) {
        clearDetails();
        image.setUrl(productServiceDescriptionDTO.getServiceImage());
        title.setText(productServiceDescriptionDTO.getName());
        description.setText(productServiceDescriptionDTO.getDescription());
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
        // add actions
        {
            MaterialPanel actionsPanel = new MaterialPanel();
            details.add(actionsPanel);
            if (productServiceDescriptionDTO.isHasFeasibility()) {
                MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton("CHECK FEASIBILITY");
                materialAnchorButton.setHref("#" + PlaceHistoryHelper.convertPlace(
                        new ProductFeasibilityPlace(
                                ProductFeasibilityPlace.TOKENS.productservice.toString() + "=" + productServiceDescriptionDTO.getId())));
                actionsPanel.add(materialAnchorButton);
                materialAnchorButton.setMargin(20);
            }
            {
                MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton("REQUEST QUOTE");
                materialAnchorButton.setHref("#" + PlaceHistoryHelper.convertPlace(
                        new ProductFormPlace(
                                ProductFormPlace.TOKENS.id.toString() + "=" + productServiceDescriptionDTO.getProduct().getId())));
                actionsPanel.add(materialAnchorButton);
                materialAnchorButton.setMargin(20);
            }
            {
                MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton("ASK QUESTION");
                materialAnchorButton.setHref("#" + PlaceHistoryHelper.convertPlace(
                        new ConversationPlace(
                                Utils.generateTokens(
                                ConversationPlace.TOKENS.companyid.toString(), productServiceDescriptionDTO.getCompany().getId() + "",
                                ConversationPlace.TOKENS.topic.toString(), "Information on service '" + productServiceDescriptionDTO.getName() + "'"))));
                actionsPanel.add(materialAnchorButton);
                materialAnchorButton.setMargin(20);
            }
        }
        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);
        addTab(materialTab, tabsPanel, "Description", new HTMLPanel(productServiceDescriptionDTO.getFullDescription()), size);
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
        mapContainer.setEdit(false);
        mapContainer.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {

            }

            @Override
            public void onSuccess(Void result) {
            }
        });
        materialColumn.add(mapContainer);
        addTab(materialTab, tabsPanel, "Features", featuresPanel, size);
        // create tab panel for services
        HTMLPanel termsAndConditionsPanel = new HTMLPanel("<p class='" + style.subsection() + "'>No terms and conditions specified</p>");
        addTab(materialTab, tabsPanel, "Terms and Conditions", termsAndConditionsPanel, size);
        addTab(materialTab, tabsPanel, "Others", new HTMLPanel(""), size);
        materialTab.selectTab("fullViewTab0");
        details.add(tabsPanel);
        addColumnSection("You might also be interested in...");
        List<ProductServiceDTO> suggestedServices = productServiceDescriptionDTO.getSuggestedServices();
        if(suggestedServices == null || suggestedServices.size() == 0) {
            addColumnLine(new MaterialLabel("No suggestions..."));
        } else {
            for(ProductServiceDTO productServiceDTO : productServiceDescriptionDTO.getSuggestedServices()) {
                materialColumn = new MaterialColumn(12, 6, 3);
                ProductServiceWidget productServiceWidget = new ProductServiceWidget(productServiceDTO);
                materialColumn.add(productServiceWidget);
                details.add(materialColumn);
            }
        }
    }

    @Override
    public void displayProductDataset(final ProductDatasetDescriptionDTO productDatasetDescriptionDTO) {
        clearDetails();
        image.setUrl(productDatasetDescriptionDTO.getImageUrl());
        title.setText(productDatasetDescriptionDTO.getName());
        description.setText(productDatasetDescriptionDTO.getDescription());
        tags.clear();
        MaterialPanel badges = new MaterialPanel();
        badges.setPadding(10);
        {
            MaterialChip commercial = new MaterialChip();
            commercial.setText(productDatasetDescriptionDTO.isCommercial() ? "Commercial" : "Free");
            commercial.setBackgroundColor(productDatasetDescriptionDTO.isCommercial() ? "amber" : "green");
            commercial.setTextColor("white");
            commercial.setMarginRight(20);
            badges.add(commercial);
        }
        {
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
        }
        {
            MaterialChip product = new MaterialChip();
            final ProductDTO productDTO = productDatasetDescriptionDTO.getProduct();
            product.setText(productDTO.getName());
            product.setBackgroundColor("grey");
            product.setTextColor("white");
            product.setUrl(productDTO.getImageUrl() == null ? "./images/noImage.png" : productDTO.getImageUrl());
            product.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    template.getClientFactory().getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + productDTO.getId()));
                }
            });
            badges.add(product);
        }
        // add actions
        {
            MaterialPanel actionsPanel = new MaterialPanel();
            details.add(actionsPanel);
            {
                MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton("ASK QUESTION");
                materialAnchorButton.setHref("#" + PlaceHistoryHelper.convertPlace(
                        new ConversationPlace(
                                Utils.generateTokens(
                                        ConversationPlace.TOKENS.companyid.toString(), productDatasetDescriptionDTO.getCompany().getId() + "",
                                        ConversationPlace.TOKENS.topic.toString(), "Information on off the shelf product '" + productDatasetDescriptionDTO.getName() + "'"))));
                actionsPanel.add(materialAnchorButton);
                materialAnchorButton.setMargin(20);
            }
        }
        tags.add(badges);
        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);
        addTab(materialTab, tabsPanel, "Description", new HTMLPanel(productDatasetDescriptionDTO.getFullDescription()), size);
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
        mapContainer.setEdit(false);
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
        addTab(materialTab, tabsPanel, "Features", featuresPanel, size);
        // add access panel
        {
            MaterialPanel accessPanel = new MaterialPanel();
            accessPanel.add(createSubsection("Methods to access the data"));
            for(DatasetAccess datasetAccess : productDatasetDescriptionDTO.getDatasetAccesses()) {
                DataAccessWidget dataAccessWidget = new DataAccessWidget(datasetAccess, !productDatasetDescriptionDTO.isCommercial());
                dataAccessWidget.getElement().getStyle().setMarginTop(20, com.google.gwt.dom.client.Style.Unit.PX);
                dataAccessWidget.getElement().getStyle().setMarginBottom(20, com.google.gwt.dom.client.Style.Unit.PX);
                accessPanel.add(dataAccessWidget);
            }
            addTab(materialTab, tabsPanel, "Access to data", accessPanel, size);
        }
        // add terms and conditions tab panel
        HTMLPanel termsAndConditionsPanel = new HTMLPanel("<p class='" + style.subsection() + "'>No terms and conditions specified</p>");
        addTab(materialTab, tabsPanel, "Terms and Conditions", termsAndConditionsPanel, size);
        // now add the tabs panel
        details.add(tabsPanel);
        materialTab.selectTab("fullViewTab0");
        addColumnSection("You might also be interested in...");
        MaterialRow materialRow = new MaterialRow();
        details.add(materialRow);
        List<ProductDatasetDTO> suggestedDatasets = productDatasetDescriptionDTO.getSuggestedDatasets();
        if(suggestedDatasets == null || suggestedDatasets.size() == 0) {
            addColumnLine(new MaterialLabel("No suggestions..."));
        } else {
            for (ProductDatasetDTO productDatasetDTO : suggestedDatasets) {
                materialColumn = new MaterialColumn(12, 6, 3);
                ProductDatasetWidget productDatasetWidget = new ProductDatasetWidget(productDatasetDTO);
                materialColumn.add(productDatasetWidget);
                materialRow.add(materialColumn);
            }
        }
    }

    @Override
    public void displaySoftware(SoftwareDescriptionDTO softwareDescriptionDTO) {
        clearDetails();
        image.setUrl(softwareDescriptionDTO.getImageUrl());
        title.setText(softwareDescriptionDTO.getName());
        description.setText(softwareDescriptionDTO.getDescription());
        tags.clear();
        MaterialPanel badges = new MaterialPanel();
        badges.setPadding(10);
        {
            MaterialChip commercial = new MaterialChip();
            commercial.setText(softwareDescriptionDTO.isCommercial() ? "Commercial" : "Free");
            commercial.setBackgroundColor(softwareDescriptionDTO.isCommercial() ? "amber" : "green");
            commercial.setTextColor("white");
            commercial.setMarginRight(20);
            badges.add(commercial);
        }
        if(softwareDescriptionDTO.isOpenSource()) {
            MaterialChip commercial = new MaterialChip();
            commercial.setText("Open Source");
            commercial.setBackgroundColor("green");
            commercial.setTextColor("white");
            commercial.setMarginRight(20);
            badges.add(commercial);
        }
        {
            MaterialChip company = new MaterialChip();
            final CompanyDTO companyDTO = softwareDescriptionDTO.getCompanyDTO();
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
        }
        // add actions
        {
            MaterialPanel actionsPanel = new MaterialPanel();
            details.add(actionsPanel);
            {
                MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton("ASK QUESTION");
                materialAnchorButton.setHref("#" + PlaceHistoryHelper.convertPlace(
                        new ConversationPlace(
                                Utils.generateTokens(
                                        ConversationPlace.TOKENS.companyid.toString(), softwareDescriptionDTO.getCompanyDTO().getId() + "",
                                        ConversationPlace.TOKENS.topic.toString(), "Information on software solution '" + softwareDescriptionDTO.getName() + "'"))));
                actionsPanel.add(materialAnchorButton);
                materialAnchorButton.setMargin(20);
            }
        }
        tags.add(badges);
        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);
        addTab(materialTab, tabsPanel, "Description", new HTMLPanel(softwareDescriptionDTO.getFullDescription()), size);
        // TODO - add other information
        addTab(materialTab, tabsPanel, "Others", new HTMLPanel("TODO..."), size);
        // add products tab
        {
            MaterialPanel productsPanel = new MaterialPanel();
            MaterialRow materialRow = new MaterialRow();
            productsPanel.add(materialRow);
            List<ProductSoftwareDTO> productsCovered = softwareDescriptionDTO.getProducts();
            if (productsCovered == null || productsCovered.size() == 0) {
                addColumnLine(new MaterialLabel("No products..."));
            } else {
                for(ProductSoftwareDTO productSoftwareDTO : productsCovered) {
                    MaterialColumn materialColumn = new MaterialColumn(6, 4, 3);
                    ProductWidget productWidget = new ProductWidget(productSoftwareDTO.getProduct());
                    materialColumn.add(productWidget);
                    materialRow.add(materialColumn);
                    materialColumn = new MaterialColumn(6, 8, 9);
                    materialColumn.add(new HTML("<h4>Pitch</h4><p>" + productSoftwareDTO.getPitch() + "</p>"));
                    materialRow.add(materialColumn);
                }
            }
            addTab(materialTab, tabsPanel, "Products", productsPanel, size);
        }
        // add terms and conditions tab panel
        HTMLPanel termsAndConditionsPanel = new HTMLPanel("<p class='" + style.subsection() + "'>No terms and conditions specified</p>");
        addTab(materialTab, tabsPanel, "Terms and Conditions", termsAndConditionsPanel, size);
        // now add the tabs panel
        details.add(tabsPanel);
        materialTab.selectTab("fullViewTab0");
        addColumnSection("You might also be interested in...");
        MaterialRow materialRow = new MaterialRow();
        details.add(materialRow);
        addColumnLine(new MaterialLabel("TODO..."));
    }

    @Override
    public void displayProject(ProjectDescriptionDTO projectDescriptionDTO) {
        clearDetails();
        image.setUrl(projectDescriptionDTO.getImageUrl());
        title.setText(projectDescriptionDTO.getName());
        description.setText(projectDescriptionDTO.getDescription());
        tags.clear();
        MaterialPanel badges = new MaterialPanel();
        badges.setPadding(10);
        {
            MaterialChip company = new MaterialChip();
            final CompanyDTO companyDTO = projectDescriptionDTO.getCompanyDTO();
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
        }
        // add actions
        {
            MaterialPanel actionsPanel = new MaterialPanel();
            details.add(actionsPanel);
            {
                MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton("ASK QUESTION");
                materialAnchorButton.setHref("#" + PlaceHistoryHelper.convertPlace(
                        new ConversationPlace(
                                Utils.generateTokens(
                                        ConversationPlace.TOKENS.companyid.toString(), projectDescriptionDTO.getCompanyDTO().getId() + "",
                                        ConversationPlace.TOKENS.topic.toString(), "Information on project '" + projectDescriptionDTO.getName() + "'"))));
                actionsPanel.add(materialAnchorButton);
                materialAnchorButton.setMargin(20);
            }
        }
        tags.add(badges);
        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);
        addTab(materialTab, tabsPanel, "Description", new HTMLPanel(projectDescriptionDTO.getFullDescription()), size);
        // add products tab
        {
            MaterialPanel productsPanel = new MaterialPanel();
            MaterialRow materialRow = new MaterialRow();
            productsPanel.add(materialRow);
            List<ProductProjectDTO> productsCovered = projectDescriptionDTO.getProducts();
            if (productsCovered == null || productsCovered.size() == 0) {
                addColumnLine(new MaterialLabel("No products..."));
            } else {
                for (ProductProjectDTO productProjectDTO : productsCovered) {
                    MaterialColumn materialColumn = new MaterialColumn(6, 4, 3);
                    ProductWidget productWidget = new ProductWidget(productProjectDTO.getProduct());
                    materialColumn.add(productWidget);
                    materialRow.add(materialColumn);
                    materialColumn = new MaterialColumn(6, 8, 9);
                    materialColumn.add(new HTML(productProjectDTO.getPitch()));
                    materialRow.add(materialColumn);
                }
            }
            addTab(materialTab, tabsPanel, "Products", productsPanel, size);
        }
        // TODO - add other information?
        addTab(materialTab, tabsPanel, "Consortium", new HTMLPanel("TODO..."), size);
        // add terms and conditions tab panel
        HTMLPanel termsAndConditionsPanel = new HTMLPanel("<p class='" + style.subsection() + "'>No terms and conditions specified</p>");
        addTab(materialTab, tabsPanel, "Terms and Conditions", termsAndConditionsPanel, size);
        // now add the tabs panel
        details.add(tabsPanel);
        materialTab.selectTab("fullViewTab0");
        addColumnSection("You might also be interested in...");
        MaterialRow materialRow = new MaterialRow();
        details.add(materialRow);
        addColumnLine(new MaterialLabel("TODO..."));
    }

    @Override
    public void displayCompany(final CompanyDescriptionDTO companyDescriptionDTO) {
        clearDetails();
        image.setUrl(companyDescriptionDTO.getIconURL());
        title.setText(companyDescriptionDTO.getName());
        description.setText(companyDescriptionDTO.getDescription());
        tags.clear();
        MaterialPanel badges = new MaterialPanel();
        badges.setPadding(10);
        MaterialChip email = new MaterialChip();
        email.setText("Contact");
        email.setBackgroundColor("grey");
        email.setTextColor("white");
        email.setLetterBackgroundColor("blue");
        email.setLetterColor("white");
        email.setLetter("@");
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
        email.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
        badges.add(email);
        MaterialChip website = new MaterialChip();
        website.setText("Website");
        website.setBackgroundColor("grey");
        website.setTextColor("white");
        website.setLetterBackgroundColor("green");
        website.setLetterColor("white");
        website.setLetter("W");
        website.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
        website.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(companyDescriptionDTO.getWebsite(), "_blank", null);
            }
        });
        badges.add(website);
        tags.add(badges);
        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);
        HTMLPanel fullDescriptionPanel = new HTMLPanel(companyDescriptionDTO.getFullDescription());
        addTab(materialTab, tabsPanel, "Description", fullDescriptionPanel, size);
        // create tab panel for offers
        int offerCount = 0;
        MaterialPanel servicesPanel = new MaterialPanel();
        MaterialRow materialRow = new MaterialRow();
        servicesPanel.add(materialRow);
        if(companyDescriptionDTO.getProductServices().size() == 0) {
/*
            servicesPanel.add(createSubsection("This company does not provide on-demand services"));
*/
        } else {
            offerCount += companyDescriptionDTO.getProductServices().size();
/*
            servicesPanel.add(createSubsection("On-demand services provided"));
            MaterialRow materialRow = new MaterialRow();
            servicesPanel.add(materialRow);
*/
            for (ProductServiceDTO productServiceDTO : companyDescriptionDTO.getProductServices()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                materialColumn.add(new ProductServiceWidget(productServiceDTO));
                materialRow.add(materialColumn);
            }
        }
        if(companyDescriptionDTO.getProductDatasets().size() == 0) {
/*
            servicesPanel.add(createSubsection("This company does not provide off-the-shelf data"));
*/
        } else {
            offerCount += companyDescriptionDTO.getProductDatasets().size();
/*
            servicesPanel.add(createSubsection("Off-the-shelf data provided"));
            MaterialRow materialRow = new MaterialRow();
            servicesPanel.add(materialRow);
*/
/*
            MaterialLabel materialLabel = new MaterialLabel("Test");
            materialLabel.addStyleName(style.vertical());
            materialLabel.setBackgroundColor("green");
            materialLabel.setTextColor("white");
            MaterialColumn materialLabelColumn = new MaterialColumn(1, 1, 1);
            materialLabelColumn.add(materialLabel);
            materialRow.add(materialLabelColumn);
*/
            for(ProductDatasetDTO productDatasetDTO : companyDescriptionDTO.getProductDatasets()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                materialColumn.add(new ProductDatasetWidget(productDatasetDTO));
                materialRow.add(materialColumn);
            }
        }
        if(companyDescriptionDTO.getSoftware().size() == 0) {
/*
            servicesPanel.add(createSubsection("This company does not provide software solutions"));
*/
        } else {
            offerCount += companyDescriptionDTO.getSoftware().size();
/*
            servicesPanel.add(createSubsection("Software solutions provided"));
            MaterialRow materialRow = new MaterialRow();
            servicesPanel.add(materialRow);
*/
            for(SoftwareDTO softwareDTO : companyDescriptionDTO.getSoftware()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                materialColumn.add(new SoftwareWidget(softwareDTO));
                materialRow.add(materialColumn);
            }
        }
        if(companyDescriptionDTO.getProject().size() == 0) {
/*
            servicesPanel.add(createSubsection("This company does not manage any project"));
*/
        } else {
            offerCount += companyDescriptionDTO.getProject().size();
/*
            servicesPanel.add(createSubsection("Project managed"));
            MaterialRow materialRow = new MaterialRow();
            servicesPanel.add(materialRow);
*/
            for(ProjectDTO projectDTO : companyDescriptionDTO.getProject()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                materialColumn.add(new ProjectWidget(projectDTO));
                materialRow.add(materialColumn);
            }
        }
        addTab(materialTab, tabsPanel, "Offer (" + offerCount + ")", servicesPanel, size);
        addTab(materialTab, tabsPanel, "Others", new HTMLPanel(""), size);
        details.add(tabsPanel);
        materialTab.selectTab("fullViewTab0");
        addColumnSection("Other similar companies");
        addColumnLine(new MaterialLabel("TODO..."));
    }

    private void addColumnLine(MaterialWidget materialWidget) {
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        materialColumn.add(materialWidget);
        details.add(materialColumn);
    }

    private void addColumnSection(String message) {
        MaterialLabel label = new MaterialLabel(message);
        label.addStyleName(style.section());
        addColumnLine(label);
    }

    private void addTab(MaterialTab materialTab, MaterialPanel tabPanel, String name, Panel panel, int size) {
        String tabId = "fullViewTab" + materialTab.getWidgetCount();
        MaterialTabItem materialTabItem = new MaterialTabItem();
        materialTabItem.setWaves(WavesType.GREEN);
        materialTabItem.setGrid("s" + size + " m" + size + " l" + size);
        MaterialLink materialLink = new MaterialLink(name);
        materialLink.setHref("#" + tabId);
        materialTabItem.add(materialLink);
        materialTab.add(materialTabItem);
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        tabPanel.add(materialColumn);
        materialColumn.add(panel);
        materialColumn.setId(tabId);
        panel.addStyleName(style.tabPanel());
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    private MaterialPanel createTabsPanel() {
        MaterialPanel materialPanel = new MaterialPanel();
        return materialPanel;
    }

    private MaterialTab createTabs(MaterialPanel materialPanel) {
        MaterialTab materialTab = new MaterialTab();
        materialTab.setBackgroundColor("transparent");
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        materialPanel.add(materialColumn);
        materialColumn.add(materialTab);
        return materialTab;
    }

    private MaterialLabel createSubsection(String message) {
        MaterialLabel label = new MaterialLabel(message);
        label.addStyleName(style.subsection());
        return label;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}