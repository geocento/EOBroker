package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.CountryEditor;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.*;
import com.geocento.webapps.eobroker.customer.client.widgets.*;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;

import java.util.ArrayList;
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
    MaterialRow tabsContent;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialImage image;
    @UiField
    HTMLPanel tags;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialPanel tabsPanel;
    @UiField
    MaterialPanel colorPanel;
    @UiField
    MaterialPanel recommendationsPanel;
    @UiField
    MaterialNavBar navigation;
    @UiField
    MaterialPanel actions;

    public FullViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

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
        navigation.clear();
        navigation.setVisible(false);
        actions.clear();
        tags.clear();
        tabsContent.clear();
    }

    @Override
    public void displayTitle(String title) {
        template.setTitleText(title);
    }

    @Override
    public void displayProduct(ProductDescriptionDTO productDescriptionDTO) {
        clearDetails();
        image.setUrl(Utils.getImageMaybe(productDescriptionDTO.getImageUrl()));
        title.setText(productDescriptionDTO.getName());
        description.setText(productDescriptionDTO.getShortDescription());
        setTabPanelColor(CategoryUtils.getColor(Category.products));
        // add tags
        MaterialChip thematic = new MaterialChip();
        thematic.setText(productDescriptionDTO.getThematic().toString());
        thematic.setBackgroundColor(Color.GREY);
        thematic.setTextColor(Color.WHITE);
        thematic.setLetterBackgroundColor(Color.BLUE);
        thematic.setLetterColor(Color.WHITE);
        thematic.setLetter("T");
        thematic.setMarginRight(20);
        tags.add(thematic);
        MaterialChip sector = new MaterialChip();
        sector.setText(productDescriptionDTO.getSector().getName());
        sector.setBackgroundColor(Color.GREY);
        sector.setTextColor(Color.WHITE);
        sector.setLetterBackgroundColor(Color.AMBER);
        sector.setLetterColor(Color.WHITE);
        sector.setLetter("S");
        tags.add(sector);
        // add actions
        {
            addAction("FOLLOW", event -> Window.alert("TODO..."));
        }
        // add the tabs now
        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);
        // add description
        MaterialPanel fullDescriptionPanel = new MaterialPanel();
        HTML fullDescription = new HTML(productDescriptionDTO.getDescription());
        fullDescription.getElement().getStyle().setProperty("minHeight", "6em");
        fullDescriptionPanel.add(fullDescription);
        fullDescriptionPanel.setPadding(10);
        addTab(materialTab, tabsPanel, "Description", fullDescriptionPanel, size);
        // create tab panel for services
        MaterialPanel servicesPanel = new MaterialPanel();
        if(productDescriptionDTO.getProductServices().size() == 0) {
            servicesPanel.add(createSubsection("No on-demand services are available for this product"));
            servicesPanel.add(new HTML("<p>We are sorry but we do not have any supplier currently supporting on-demand generation of this product as a service</p>"));
        } else {
            {
                MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton();
                materialAnchorButton.setText("Compare");
                materialAnchorButton.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        Window.alert("TODO...");
                    }
                });
                materialAnchorButton.setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
                servicesPanel.add(materialAnchorButton);
            }
            {
                MaterialAnchorButton materialAnchorButton = new MaterialAnchorButton();
                materialAnchorButton.setText("Request quote");
                materialAnchorButton.setHref("#" + PlaceHistoryHelper.convertPlace(new ProductFormPlace(productDescriptionDTO.getId())));
                materialAnchorButton.setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
                materialAnchorButton.setMarginRight(20);
                servicesPanel.add(materialAnchorButton);
            }
            servicesPanel.add(createSubsection("This product can be provided by the following on-demand services"));
            MaterialRow materialRow = new MaterialRow();
            materialRow.setMarginTop(20);
            servicesPanel.add(materialRow);
            for (ProductServiceDTO productServiceDTO : productDescriptionDTO.getProductServices()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                ProductServiceWidget productServiceWidget = new ProductServiceWidget(productServiceDTO);
                materialColumn.add(productServiceWidget);
                materialRow.add(materialColumn);
            }
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
        // add software solutions
        {
            othersRow.add(createSubsection("Software solutions supporting generation of this product"));
            List<SoftwareDTO> softwares = productDescriptionDTO.getSoftwares();
            if(softwares == null || softwares.size() == 0) {
                MaterialLabel materialLabel = new MaterialLabel("No software solutions for this product");
                materialLabel.setPadding(20);
                othersRow.add(materialLabel);
            } else {
                othersCount += softwares.size();
                MaterialRow materialRow = new MaterialRow();
                othersRow.add(materialRow);
                for (SoftwareDTO softwareDTO : softwares) {
                    MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                    materialColumn.add(new SoftwareWidget(softwareDTO));
                    materialRow.add(materialColumn);
                }
            }
        }
        // add projects
        {
            othersRow.add(createSubsection("Projects working on the generation of this product"));
            List<ProjectDTO> projects = productDescriptionDTO.getProjects();
            if(projects == null || projects.size() == 0) {
                MaterialLabel materialLabel = new MaterialLabel("No projects working on this product");
                materialLabel.setPadding(20);
                othersRow.add(materialLabel);
            } else {
                othersCount += projects.size();
                MaterialRow materialRow = new MaterialRow();
                othersRow.add(materialRow);
                for (ProjectDTO projectDTO : projects) {
                    MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                    materialColumn.add(new ProjectWidget(projectDTO));
                    materialRow.add(materialColumn);
                }
            }
        }
        if(productDescriptionDTO.hasImageRule()) {
            othersCount += 2;
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
        tabsContent.add(tabsPanel);

        // TODO - change?
        this.tabsPanel.clear();
        this.tabsPanel.add(materialTab);

        materialTab.selectTab("fullViewTab0");
        // add suggestions
        recommendationsPanel.clear();
        recommendationsPanel.add(createSubsection("Similar products..."));
        List<ProductDTO> suggestedProducts = productDescriptionDTO.getSuggestedProducts();
        if(suggestedProducts == null || suggestedProducts.size() == 0) {
            MaterialLabel materialLabel = new MaterialLabel("No suggestions...");
            materialLabel.setPadding(20);
            recommendationsPanel.add(materialLabel);
        } else {
            MaterialRow materialRow = new MaterialRow();
            recommendationsPanel.add(materialRow);
            for(ProductDTO productDTO : suggestedProducts) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                ProductWidget productWidget = new ProductWidget(productDTO);
                materialColumn.add(productWidget);
                materialRow.add(materialColumn);
            }
        }
    }

    private void setTabPanelColor(Color color) {
        tabsPanel.setBackgroundColor(color);
        colorPanel.setBackgroundColor(color);
    }

    @Override
    public void displayProductService(final ProductServiceDescriptionDTO productServiceDescriptionDTO) {
        clearDetails();
        // insert header with information on company and product category
        {
            addBreadcrumb(productServiceDescriptionDTO.getCompany(), Category.productservices);
            addBreadcrumb(productServiceDescriptionDTO.getProduct(), Category.productservices);
        }
        image.setUrl(Utils.getImageMaybe(productServiceDescriptionDTO.getServiceImage()));
        title.setText(productServiceDescriptionDTO.getName());
        description.setText(productServiceDescriptionDTO.getDescription());
        setTabPanelColor(CategoryUtils.getColor(Category.productservices));
        // add tags
        {
/*
            MaterialChip product = new MaterialChip();
            tags.add(product);
*/
        }
        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);

        // add actions
        {
            if (productServiceDescriptionDTO.isHasFeasibility()) {
                addAction("CHECK FEASIBILITY", "#" + PlaceHistoryHelper.convertPlace(
                        new ProductFeasibilityPlace(
                                ProductFeasibilityPlace.TOKENS.productservice.toString() + "=" + productServiceDescriptionDTO.getId())));
            }
            {
                addAction("REQUEST QUOTE", "#" + PlaceHistoryHelper.convertPlace(
                        new ProductFormPlace(
                                ProductFormPlace.TOKENS.id.toString() + "=" + productServiceDescriptionDTO.getProduct().getId())));
            }
            {
                addAction("ASK QUESTION", "#" + PlaceHistoryHelper.convertPlace(
                        new ConversationPlace(
                                Utils.generateTokens(
                                        ConversationPlace.TOKENS.companyid.toString(), productServiceDescriptionDTO.getCompany().getId() + "",
                                        ConversationPlace.TOKENS.topic.toString(), "Information on service '" + productServiceDescriptionDTO.getName() + "'"))));
            }
        }

        // create full description panel
        MaterialPanel fullDescriptionPanel = createFullDescriptionPanel(productServiceDescriptionDTO.getFullDescription());
        addTab(materialTab, tabsPanel, "Description", fullDescriptionPanel, size);

        // create tab panel for services
        MaterialRow featuresPanel = new MaterialRow();
        MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
        featuresPanel.add(materialColumn);
        // add geoinformation provided
        MaterialPanel geoinformationPanel = new MaterialPanel();
        materialColumn.add(geoinformationPanel);
        {
            geoinformationPanel.add(createSubsection("Geoinformation provided"));
            MaterialRow geoinformationRow = new MaterialRow();
            geoinformationRow.setMarginTop(20);
            geoinformationPanel.add(geoinformationRow);
            for(FeatureDescription featureDescription : productServiceDescriptionDTO.getGeoinformation()) {
                materialColumn = new MaterialColumn(12, 12, 12);
                geoinformationRow.add(materialColumn);
                MaterialLink materialLink = new MaterialLink(featureDescription.getName());
                materialLink.setIconType(IconType.CHECK);
                materialLink.setIconColor(Color.AMBER);
                MaterialTooltip materialTooltip = new MaterialTooltip(materialLink, featureDescription.getDescription());
                materialColumn.add(materialTooltip);
            }
        }
        // add map with extent of data
        materialColumn = new MaterialColumn(12, 6, 6);
        featuresPanel.add(materialColumn);
        materialColumn.add(createSubsection("Extent of service"));
        final MapContainer mapContainer = new MapContainer();
        mapContainer.setHeight("200px");
        mapContainer.getElement().getStyle().setMarginTop(20, com.google.gwt.dom.client.Style.Unit.PX);
        mapContainer.setEditable(false);
        mapContainer.setBasemapVisible(false);
        mapContainer.setLayer(false);
        mapContainer.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {

            }

            @Override
            public void onSuccess(Void result) {
                mapContainer.displayAoI(AoIUtil.fromWKT(productServiceDescriptionDTO.getExtent()));
                mapContainer.centerOnAoI();
            }
        });
        materialColumn.add(mapContainer);
        // add perfomances
        materialColumn = new MaterialColumn(12, 6, 6);
        featuresPanel.add(materialColumn);
        // add geoinformation provided
        MaterialPanel performancesPanel = new MaterialPanel();
        materialColumn.add(performancesPanel);
        {
            performancesPanel.add(createSubsection("Accuracy"));
            MaterialRow materialRow = new MaterialRow();
            materialRow.setMarginTop(20);
            performancesPanel.add(materialRow);
            {
                materialColumn = new MaterialColumn(12, 12, 12);
                materialRow.add(materialColumn);
                materialColumn.add(new MaterialLabel("TODO - add list of performance indicators"));
            }
        }
        addTab(materialTab, tabsPanel, "Performances", featuresPanel, size);
        // create tab for data access information
        // add access panel
        // add access panel
        MaterialPanel accessPanel = new MaterialPanel();
        List<DatasetAccess> availableMapData = new ArrayList<DatasetAccess>();
        {
            accessPanel.add(createSubsection("Dissemination methods supported"));
            MaterialPanel materialPanel = new MaterialPanel();
            materialPanel.setMargin(10);
            accessPanel.add(materialPanel);
            List<AccessType> accessTypes = productServiceDescriptionDTO.getSelectedAccessTypes();
            for (AccessType accessType : AccessType.values()) {
                MaterialLink materialLink = new MaterialLink(accessType.getName());
                materialLink.setDisplay(Display.BLOCK);
                materialLink.setMargin(10);
                materialPanel.setMarginBottom(30);
                materialLink.setTextColor(Color.BLACK);
                if(accessTypes != null && accessTypes.contains(accessType)) {
                    materialLink.setIconType(IconType.CHECK);
                    materialLink.setIconColor(Color.AMBER);
                    materialPanel.add(materialLink);
                } else {
                    materialLink.setIconType(IconType.CHECK_BOX_OUTLINE_BLANK);
                    materialLink.setIconColor(Color.BLACK);
                }
            }
        }
        List<DatasetAccess> samples = productServiceDescriptionDTO.getSamples();
        if(samples != null && samples.size() > 0) {
            accessPanel.add(createSubsection("Sample data access"));
            MaterialRow materialRow = new MaterialRow();
            materialRow.setMargin(10);
            materialRow.setMarginBottom(30);
            accessPanel.add(materialRow);
            for(DatasetAccess datasetAccess : samples) {
                MaterialColumn materialColumnSample = new MaterialColumn(12, 12, 6);
                materialColumnSample.add(createDataAccessWidgetProductService(productServiceDescriptionDTO, datasetAccess, true));
                materialRow.add(materialColumnSample);
                if(datasetAccess instanceof DatasetAccessOGC) {
                    availableMapData.add(datasetAccess);
                }
            }
        }
        addTab(materialTab, tabsPanel, "Access to data", accessPanel, size);
        // create tab panel for services
        HTMLPanel termsAndConditionsPanel = new HTMLPanel("<p class='" + style.subsection() + "'>No terms and conditions specified</p>");
        addTab(materialTab, tabsPanel, "Terms and Conditions", termsAndConditionsPanel, size);
        materialTab.selectTab("fullViewTab0");
        tabsContent.add(tabsPanel);

        // TODO - change?
        this.tabsPanel.clear();
        this.tabsPanel.add(materialTab);

        recommendationsPanel.clear();
        recommendationsPanel.add(createSubsection("You might also be interested in..."));
        List<ProductServiceDTO> suggestedServices = productServiceDescriptionDTO.getSuggestedServices();
        if(suggestedServices == null || suggestedServices.size() == 0) {
            MaterialLabel materialLabel = new MaterialLabel("No suggestions...");
            materialLabel.setPadding(20);
            recommendationsPanel.add(materialLabel);
        } else {
            MaterialRow materialRow = new MaterialRow();
            recommendationsPanel.add(materialRow);
            for(ProductServiceDTO productServiceDTO : productServiceDescriptionDTO.getSuggestedServices()) {
                materialColumn = new MaterialColumn(12, 6, 3);
                ProductServiceWidget productServiceWidget = new ProductServiceWidget(productServiceDTO);
                materialColumn.add(productServiceWidget);
                materialRow.add(materialColumn);
            }
        }
    }

    @Override
    public void displayProductDataset(final ProductDatasetDescriptionDTO productDatasetDescriptionDTO) {
        clearDetails();
        image.setUrl(Utils.getImageMaybe(productDatasetDescriptionDTO.getImageUrl()));
        title.setText(productDatasetDescriptionDTO.getName());
        description.setText(productDatasetDescriptionDTO.getDescription());
        setTabPanelColor(CategoryUtils.getColor(Category.productdatasets));
        // add breadcrumbs
        {
            addBreadcrumb(productDatasetDescriptionDTO.getCompany(), Category.productservices);
            addBreadcrumb(productDatasetDescriptionDTO.getProduct(), Category.productservices);
        }
        // add tags
        {
            MaterialChip commercial = new MaterialChip();
            commercial.setText(productDatasetDescriptionDTO.isCommercial() ? "Commercial" : "Free");
            commercial.setBackgroundColor(productDatasetDescriptionDTO.isCommercial() ? Color.AMBER : Color.AMBER);
            commercial.setTextColor(Color.WHITE);
            commercial.setMarginRight(20);
            tags.add(commercial);
        }

        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);

        // add actions
        addAction("ASK QUESTION", "#" + PlaceHistoryHelper.convertPlace(
                new ConversationPlace(
                        Utils.generateTokens(
                                ConversationPlace.TOKENS.companyid.toString(), productDatasetDescriptionDTO.getCompany().getId() + "",
                                ConversationPlace.TOKENS.topic.toString(), "Information on off the shelf product '" + productDatasetDescriptionDTO.getName() + "'"))));
        // add description
        MaterialPanel fullDescriptionPanel = createFullDescriptionPanel(productDatasetDescriptionDTO.getFullDescription());
        addTab(materialTab, tabsPanel, "Description", fullDescriptionPanel, size);

        // create tab panel for services
        MaterialRow featuresPanel = new MaterialRow();
        // add geoinformation provided
        MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
        featuresPanel.add(materialColumn);
        MaterialPanel geoinformationPanel = new MaterialPanel();
        materialColumn.add(geoinformationPanel);
        {
            geoinformationPanel.add(createSubsection("Geoinformation provided"));
            MaterialRow geoinformationRow = new MaterialRow();
            geoinformationRow.setMarginTop(20);
            geoinformationPanel.add(geoinformationRow);
            for(FeatureDescription featureDescription : productDatasetDescriptionDTO.getGeoinformation()) {
                materialColumn = new MaterialColumn(12, 12, 12);
                geoinformationRow.add(materialColumn);
                MaterialLink materialLink = new MaterialLink(featureDescription.getName());
                materialLink.setIconType(IconType.CHECK);
                materialLink.setIconColor(Color.AMBER);
                MaterialTooltip materialTooltip = new MaterialTooltip(materialLink, featureDescription.getDescription());
                materialColumn.add(materialTooltip);
            }
        }
        // add map with extent of data
        materialColumn = new MaterialColumn(12, 6, 6);
        featuresPanel.add(materialColumn);
        materialColumn.add(createSubsection("Extent of data"));
        final MapContainer mapContainer = new MapContainer();
        mapContainer.setHeight("200px");
        mapContainer.getElement().getStyle().setMarginTop(20, com.google.gwt.dom.client.Style.Unit.PX);
        mapContainer.setEditable(false);
        mapContainer.setBasemapVisible(false);
        mapContainer.setLayer(false);
        mapContainer.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {

            }

            @Override
            public void onSuccess(Void result) {
                mapContainer.displayAoI(AoIUtil.fromWKT(productDatasetDescriptionDTO.getExtent()));
                mapContainer.centerOnAoI();
            }
        });
        materialColumn.add(mapContainer);
        // add perfomances
        materialColumn = new MaterialColumn(12, 6, 6);
        featuresPanel.add(materialColumn);
        materialColumn.add(createSubsection("Performances"));
        materialColumn.add(new MaterialLabel("TODO - add list of performance indicators like spatial resolution, etc..."));
        addTab(materialTab, tabsPanel, "Characteristics", featuresPanel, size);
        // add access panel
        MaterialPanel accessPanel = new MaterialPanel();
        List<DatasetAccess> availableMapData = new ArrayList<DatasetAccess>();
        List<DatasetAccess> dataAccesses = productDatasetDescriptionDTO.getDatasetAccesses();
        if(dataAccesses != null && dataAccesses.size() > 0) {
            accessPanel.add(createSubsection("Methods to access the data"));
            for (DatasetAccess datasetAccess : dataAccesses) {
                boolean freeAvailable = !productDatasetDescriptionDTO.isCommercial();
                DataAccessWidget dataAccessWidget = createDataAccessWidgetProductDataset(productDatasetDescriptionDTO, datasetAccess, freeAvailable);
                accessPanel.add(dataAccessWidget);
                if (datasetAccess instanceof DatasetAccessOGC && freeAvailable) {
                    availableMapData.add(datasetAccess);
                }
            }
        }
        List<DatasetAccess> samples = productDatasetDescriptionDTO.getSamples();
        if(samples != null && samples.size() > 0) {
            accessPanel.add(createSubsection("Sample data"));
            for(DatasetAccess datasetAccess : samples) {
                accessPanel.add(createDataAccessWidgetProductDataset(productDatasetDescriptionDTO, datasetAccess, true));
                if(datasetAccess instanceof DatasetAccessOGC) {
                    availableMapData.add(datasetAccess);
                }
            }
        }
        addTab(materialTab, tabsPanel, "Access to data", accessPanel, size);
        // add terms and conditions tab panel
        HTMLPanel termsAndConditionsPanel = new HTMLPanel("<p class='" + style.subsection() + "'>No terms and conditions specified</p>");
        addTab(materialTab, tabsPanel, "Terms and Conditions", termsAndConditionsPanel, size);
        materialTab.selectTab("fullViewTab0");
        // now add the tabs panel
        tabsContent.add(tabsPanel);

        // TODO - change?
        this.tabsPanel.clear();
        this.tabsPanel.add(materialTab);

        // add recommendations
        recommendationsPanel.clear();
        recommendationsPanel.add(createSubsection("You might also be interested in..."));
        List<ProductDatasetDTO> suggestedDatasets = productDatasetDescriptionDTO.getSuggestedDatasets();
        if(suggestedDatasets == null || suggestedDatasets.size() == 0) {
            MaterialLabel materialLabel = new MaterialLabel("No suggestions...");
            materialLabel.setPadding(20);
            recommendationsPanel.add(materialLabel);
        } else {
            MaterialRow materialRow = new MaterialRow();
            recommendationsPanel.add(materialRow);
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
        image.setUrl(Utils.getImageMaybe(softwareDescriptionDTO.getImageUrl()));
        title.setText(softwareDescriptionDTO.getName());
        description.setText(softwareDescriptionDTO.getDescription());
        setTabPanelColor(CategoryUtils.getColor(Category.software));
        // add tags
        {
            MaterialChip commercial = new MaterialChip();
            commercial.setText(softwareDescriptionDTO.isCommercial() ? "Commercial" : "Free");
            commercial.setBackgroundColor(softwareDescriptionDTO.isCommercial() ? Color.AMBER : Color.GREEN);
            commercial.setTextColor(Color.WHITE);
            commercial.setMarginRight(20);
            tags.add(commercial);
        }
        if(softwareDescriptionDTO.isOpenSource()) {
            MaterialChip commercial = new MaterialChip();
            commercial.setText("Open Source");
            commercial.setBackgroundColor(Color.AMBER);
            commercial.setTextColor(Color.WHITE);
            commercial.setMarginRight(20);
            tags.add(commercial);
        }
/*
        {
            MaterialChip company = new MaterialChip();
            final CompanyDTO companyDTO = softwareDescriptionDTO.getCompanyDTO();
            company.setText(companyDTO.getName());
            company.setBackgroundColor(Color.GREY);
            company.setTextColor(Color.WHITE);
            company.setUrl(Utils.getImageMaybe(companyDTO.getIconURL()));
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
        tags.add(badges);
*/
        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);

        // add actions
        addAction("ASK QUESTION", "#" + PlaceHistoryHelper.convertPlace(
                    new ConversationPlace(
                            Utils.generateTokens(
                                    ConversationPlace.TOKENS.companyid.toString(), softwareDescriptionDTO.getCompanyDTO().getId() + "",
                                    ConversationPlace.TOKENS.topic.toString(), "Information on software solution '" + softwareDescriptionDTO.getName() + "'"))));
        // create full description panel
        MaterialPanel fullDescriptionPanel = createFullDescriptionPanel(softwareDescriptionDTO.getFullDescription());
        addTab(materialTab, tabsPanel, "Description", fullDescriptionPanel, size);

        // add products tab
        {
            MaterialPanel productsPanel = new MaterialPanel();
            MaterialLabel materialLabel = createSubsection("Products covered by this software solution:");
            materialLabel.setMarginBottom(20);
            productsPanel.add(materialLabel);
            List<ProductSoftwareDTO> productsCovered = softwareDescriptionDTO.getProducts();
            if (productsCovered == null || productsCovered.size() == 0) {
                addColumnLine(new MaterialLabel("No products..."));
            } else {
                for(ProductSoftwareDTO productSoftwareDTO : productsCovered) {
                    MaterialRow materialRow = new MaterialRow();
                    productsPanel.add(materialRow);
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

        // add other tab
        addTab(materialTab, tabsPanel, "Other", new HTMLPanel("TODO..."), size);

        // now add the tabs panel
        materialTab.selectTab("fullViewTab0");
        tabsContent.add(tabsPanel);

        // TODO - change?
        this.tabsPanel.clear();
        this.tabsPanel.add(materialTab);

        // add recommendations
        recommendationsPanel.clear();
        recommendationsPanel.add(createSubsection("You might also be interested in..."));
        List<SoftwareDTO> suggestedSoftware = softwareDescriptionDTO.getSuggestedSoftware();
        if(suggestedSoftware == null || suggestedSoftware.size() == 0) {
            MaterialLabel materialLabel = new MaterialLabel("No suggestions...");
            materialLabel.setPadding(20);
            recommendationsPanel.add(materialLabel);
        } else {
            MaterialRow materialRow = new MaterialRow();
            recommendationsPanel.add(materialRow);
            for (SoftwareDTO softwareDTO : suggestedSoftware) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                SoftwareWidget softwareWidget = new SoftwareWidget(softwareDTO);
                materialColumn.add(softwareWidget);
                materialRow.add(materialColumn);
            }
        }
    }

    @Override
    public void displayProject(ProjectDescriptionDTO projectDescriptionDTO) {
        clearDetails();
        image.setUrl(Utils.getImageMaybe(projectDescriptionDTO.getImageUrl()));
        title.setText(projectDescriptionDTO.getName());
        description.setText(projectDescriptionDTO.getDescription());
        setTabPanelColor(CategoryUtils.getColor(Category.project));
        // add tags
        {
            MaterialChip company = new MaterialChip();
            final CompanyDTO companyDTO = projectDescriptionDTO.getCompanyDTO();
            company.setText(companyDTO.getName());
            company.setBackgroundColor(Color.GREY);
            company.setTextColor(Color.WHITE);
            company.setUrl(Utils.getImageMaybe(companyDTO.getIconURL()));
            company.setMarginRight(20);
            company.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    template.getClientFactory().getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + companyDTO.getId()));
                }
            });
            company.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
            tags.add(company);
        }

        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);

        // add actions
        {
            addAction("ASK QUESTION", "#" + PlaceHistoryHelper.convertPlace(
                    new ConversationPlace(
                            Utils.generateTokens(
                                    ConversationPlace.TOKENS.companyid.toString(), projectDescriptionDTO.getCompanyDTO().getId() + "",
                                    ConversationPlace.TOKENS.topic.toString(), "Information on project '" + projectDescriptionDTO.getName() + "'"))));
        }
        // create full description panel
        MaterialPanel fullDescriptionPanel = createFullDescriptionPanel(projectDescriptionDTO.getFullDescription());
        addTab(materialTab, tabsPanel, "Description", fullDescriptionPanel, size);

        // add products tab
        {
            MaterialPanel productsPanel = new MaterialPanel();
            MaterialLabel materialLabel = createSubsection("Products covered within this project:");
            materialLabel.setMarginBottom(20);
            productsPanel.add(materialLabel);
            List<ProductProjectDTO> productsCovered = projectDescriptionDTO.getProducts();
            if (productsCovered == null || productsCovered.size() == 0) {
                addColumnLine(new MaterialLabel("No products..."));
            } else {
                for (ProductProjectDTO productProjectDTO : productsCovered) {
                    MaterialRow materialRow = new MaterialRow();
                    materialRow.setWidth("100%");
                    productsPanel.add(materialRow);
                    MaterialColumn materialColumn = new MaterialColumn(6, 4, 3);
                    ProductWidget productWidget = new ProductWidget(productProjectDTO.getProduct());
                    materialColumn.add(productWidget);
                    materialRow.add(materialColumn);
                    materialColumn = new MaterialColumn(6, 8, 9);
                    materialColumn.add(new HTML("<h5>Pitch</h5>" + productProjectDTO.getPitch()));
                    materialRow.add(materialColumn);
                }
            }
            addTab(materialTab, tabsPanel, "Products", productsPanel, size);
        }

        // add consortium information
        {
            MaterialPanel consortiumPanel = new MaterialPanel();
            MaterialLabel materialLabel = createSubsection("Companies involved in this project:");
            materialLabel.setMarginBottom(20);
            consortiumPanel.add(materialLabel);
            List<CompanyRoleDTO> consortium = projectDescriptionDTO.getConsortium();
            for(CompanyRoleDTO companyRoleDTO : consortium) {
                MaterialRow materialRow = new MaterialRow();
                materialRow.setWidth("100%");
                consortiumPanel.add(materialRow);
                MaterialColumn materialColumn = new MaterialColumn(6, 4, 3);
                CompanyWidget companyWidget = new CompanyWidget(companyRoleDTO.getCompanyDTO());
                materialColumn.add(companyWidget);
                materialRow.add(materialColumn);
                materialColumn = new MaterialColumn(6, 8, 9);
                materialColumn.add(new HTML("<h5>Role in project</h5>" + companyRoleDTO.getRole()));
                materialRow.add(materialColumn);
            }
/*
            // add lead first
            {
                MaterialColumn materialColumn = new MaterialColumn(6, 4, 3);
                CompanyWidget companyWidget = new CompanyWidget(projectDescriptionDTO.getCompanyDTO());
                materialColumn.add(companyWidget);
                materialRow.add(materialColumn);
            }
            // now add all others
            {
                List<CompanyDTO> companies = projectDescriptionDTO.getConsortium();
                if(companies != null && companies.size() > 0) {
                    for (CompanyDTO companyDTO : companies) {
                        MaterialColumn materialColumn = new MaterialColumn(6, 4, 3);
                        CompanyWidget companyWidget = new CompanyWidget(companyDTO);
                        materialColumn.add(companyWidget);
                        materialRow.add(materialColumn);
                    }
                }
            }
*/
            addTab(materialTab, tabsPanel, "Consortium", consortiumPanel, size);
        }
        // add terms and conditions tab panel
        HTMLPanel termsAndConditionsPanel = new HTMLPanel("<p class='" + style.subsection() + "'>No other information provided</p>");
        addTab(materialTab, tabsPanel, "Others", termsAndConditionsPanel, size);
        materialTab.selectTab("fullViewTab0");
        // now add the tabs panel
        tabsContent.add(tabsPanel);

        // TODO - change?
        this.tabsPanel.clear();
        this.tabsPanel.add(materialTab);

        // add recommendations
        recommendationsPanel.clear();
        recommendationsPanel.add(createSubsection("You might also be interested in..."));
        List<ProjectDTO> suggestedProjects = projectDescriptionDTO.getSuggestedProjects();
        if(suggestedProjects == null || suggestedProjects.size() == 0) {
            MaterialLabel materialLabel = new MaterialLabel("No suggestions...");
            materialLabel.setPadding(20);
            recommendationsPanel.add(materialLabel);
        } else {
            MaterialRow materialRow = new MaterialRow();
            recommendationsPanel.add(materialRow);
            for (ProjectDTO projectDTO : suggestedProjects) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                ProjectWidget projectWidget = new ProjectWidget(projectDTO);
                materialColumn.add(projectWidget);
                materialRow.add(materialColumn);
            }
        }
    }

    @Override
    public void displayCompany(final CompanyDescriptionDTO companyDescriptionDTO) {
        clearDetails();
        image.setUrl(Utils.getImageMaybe(companyDescriptionDTO.getIconURL()));
        title.setText(companyDescriptionDTO.getName());
        description.setText(companyDescriptionDTO.getDescription());
        setTabPanelColor(CategoryUtils.getColor(Category.companies));
        // add tags
        if(companyDescriptionDTO.getCompanySize() != null) {
            MaterialChip companySize = new MaterialChip();
            companySize.setText(companyDescriptionDTO.getCompanySize().toString());
            companySize.setBackgroundColor(Color.BLUE);
            companySize.setTextColor(Color.WHITE);
            companySize.setLetterBackgroundColor(Color.AMBER);
            companySize.setLetterColor(Color.WHITE);
            companySize.setIconType(IconType.FORMAT_SIZE);
            companySize.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
            companySize.setTooltip("The company size using the European Commission definition");
            tags.add(companySize);
        }
        if(companyDescriptionDTO.getCountryCode() != null) {
            MaterialChip country = new MaterialChip();
            country.setText(CountryEditor.getDisplayName(companyDescriptionDTO.getCountryCode()));
            country.setBackgroundColor(Color.GREY);
            country.setTextColor(Color.WHITE);
            country.setLetterBackgroundColor(Color.AMBER);
            country.setLetterColor(Color.WHITE);
            country.setIconType(IconType.LOCATION_CITY);
            country.setIconPosition(IconPosition.LEFT);
            country.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
            tags.add(country);
        }

        MaterialPanel tabsPanel = createTabsPanel();
        MaterialTab materialTab = createTabs(tabsPanel);
        int numTabs = 4;
        int size = (int) Math.floor(12 / numTabs);

        // add actions
        {
            addAction("WEBSITE", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.open(companyDescriptionDTO.getWebsite(), "_blank;", null);
                }
            });
            addAction("FOLLOW", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.alert("TODO...");
                }
            });
            addAction("ASK QUESTION", "#" + PlaceHistoryHelper.convertPlace(
                    new ConversationPlace(
                            ConversationPlace.TOKENS.companyid.toString() + "=" + companyDescriptionDTO.getId() +
                                    "&" + ConversationPlace.TOKENS.topic.toString() + "=Request for information"
                    )));
        }
        // create full description panel
        MaterialPanel fullDescriptionPanel = createFullDescriptionPanel(companyDescriptionDTO.getFullDescription());
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
            for(ProductDatasetDTO productDatasetDTO : companyDescriptionDTO.getProductDatasets()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                materialColumn.add(new ProductDatasetWidget(productDatasetDTO));
                materialRow.add(materialColumn);
            }
        }
        if(companyDescriptionDTO.getSoftware().size() == 0) {
        } else {
            offerCount += companyDescriptionDTO.getSoftware().size();
            for(SoftwareDTO softwareDTO : companyDescriptionDTO.getSoftware()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                materialColumn.add(new SoftwareWidget(softwareDTO));
                materialRow.add(materialColumn);
            }
        }
        if(companyDescriptionDTO.getProject().size() == 0) {
        } else {
            offerCount += companyDescriptionDTO.getProject().size();
            for(ProjectDTO projectDTO : companyDescriptionDTO.getProject()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                materialColumn.add(new ProjectWidget(projectDTO));
                materialRow.add(materialColumn);
            }
        }
        addTab(materialTab, tabsPanel, "Offer (" + offerCount + ")", servicesPanel, size);
        MaterialRow credentialsPanel = new MaterialRow();
        {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
            materialColumn.add(createColumnSection("Testimonials received"));
            List<TestimonialDTO> testimonials = companyDescriptionDTO.getTestimonials();
            boolean hasTestimonials = testimonials != null && testimonials.size() > 0;
            if (hasTestimonials) {
                materialColumn.add(new MaterialLabel("This company has received " + testimonials.size() + " testimonials"));
                for (TestimonialDTO testimonialDTO : testimonials) {
                    materialColumn.add(new TestimonialWidget(testimonialDTO));
                }
            } else {
                materialColumn.add(new MaterialLabel("This company has not received any testimonials yet"));
            }
            MaterialPanel materialPanel = new MaterialPanel();
            MaterialLabel label = new MaterialLabel("Have you worked with " + companyDescriptionDTO.getName() + "? ");
            label.setDisplay(Display.INLINE_BLOCK);
            materialPanel.add(label);
            MaterialLink addTestimonial = new MaterialLink("Add your own testimonial");
            addTestimonial.setPaddingLeft(10);
            addTestimonial.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.alert("Not implemented yet");
                }
            });
            materialPanel.add(addTestimonial);
            materialPanel.setMarginTop(20);
            materialColumn.add(materialPanel);
            credentialsPanel.add(materialColumn);
        }
        {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
            materialColumn.add(createColumnSection("Awards received"));
            List<String> awards = companyDescriptionDTO.getAwards();
            boolean hasAwards = awards != null && awards.size() > 0;
            if(hasAwards) {
                materialColumn.add(new MaterialLabel("This company has received " + awards.size() + " awards"));
                MaterialPanel materialPanel = new MaterialPanel();
                materialPanel.setPaddingLeft(20);
                for (String award : awards) {
                    MaterialLink awardLabel = new MaterialLink(award);
                    awardLabel.setIconType(IconType.STAR);
                    awardLabel.setDisplay(Display.BLOCK);
                    awardLabel.setMarginTop(20);
                    materialPanel.add(awardLabel);
                }
                materialColumn.add(materialPanel);
            } else {
                materialColumn.add(new MaterialLabel("This company has not received any awards yet"));
            }
            credentialsPanel.add(materialColumn);
        }
        addTab(materialTab, tabsPanel, "Credentials", credentialsPanel, size);
        materialTab.selectTab("fullViewTab0");
        tabsContent.add(tabsPanel);

        // TODO - change?
        this.tabsPanel.clear();
        this.tabsPanel.add(materialTab);

        // add recommendations
        recommendationsPanel.clear();
        recommendationsPanel.add(createSubsection("Other similar companies..."));
        List<CompanyDTO> suggestedCompanies = companyDescriptionDTO.getSuggestedCompanies();
        if(suggestedCompanies == null || suggestedCompanies.size() == 0) {
            MaterialLabel materialLabel = new MaterialLabel("No suggestions...");
            materialLabel.setPadding(20);
            recommendationsPanel.add(materialLabel);
        } else {
            materialRow = new MaterialRow();
            recommendationsPanel.add(materialRow);
            for (CompanyDTO companyDTO : suggestedCompanies) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                CompanyWidget companyWidget = new CompanyWidget(companyDTO);
                materialColumn.add(companyWidget);
                materialRow.add(materialColumn);
            }
        }
    }

    private MaterialPanel createFullDescriptionPanel(String fullDescription) {
        MaterialPanel fullDescriptionPanel = new MaterialPanel();
        HTML fullDescriptionHTML = new HTML(fullDescription);
        fullDescriptionHTML.getElement().getStyle().setProperty("minHeight", "6em");
        fullDescriptionPanel.add(fullDescriptionHTML);
        fullDescriptionPanel.setPadding(10);
        return fullDescriptionPanel;
    }

    private void addBreadcrumb(Object dto, Category category) {
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

    private DataAccessWidget createDataAccessWidgetProductDataset(final ProductDatasetDescriptionDTO productDatasetDescriptionDTO, final DatasetAccess datasetAccess, final boolean freeAvailable) {
        DataAccessWidget dataAccessWidget = createDataAccessWidget(datasetAccess, freeAvailable);
        dataAccessWidget.getAction().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(datasetAccess instanceof DatasetAccessFile) {
                    String fileUri = datasetAccess.getUri();
                    if(fileUri.startsWith("./")) {
                        fileUri = GWT.getHostPageBaseURL() + "uploaded/" + fileUri;
                    }
                    Window.open(fileUri, "_blank", null);
                } else if(datasetAccess instanceof DatasetAccessAPP) {
                    Window.open(datasetAccess.getUri(), "_blank", null);
                } else if(datasetAccess instanceof DatasetAccessOGC) {
                    if(freeAvailable) {
                        template.getClientFactory().getPlaceController().goTo(new VisualisationPlace(
                                Utils.generateTokens(
                                        VisualisationPlace.TOKENS.productDatasetId.toString(), productDatasetDescriptionDTO.getId() + "",
                                        VisualisationPlace.TOKENS.dataAccessId.toString(), datasetAccess.getId() + ""
                                )));
                    } else {
                        // just open the service web page
                        Window.open(datasetAccess.getUri(), "_blank", null);
                    }
                } else if(datasetAccess instanceof DatasetAccessAPI) {
                    Window.alert("TODO - show API end point and redirect to API help page? eg " + datasetAccess.getUri());
                }
            }
        });
        return dataAccessWidget;
    }

    private DataAccessWidget createDataAccessWidgetProductService(final ProductServiceDescriptionDTO productServiceDescriptionDTO, final DatasetAccess datasetAccess, final boolean freeAvailable) {
        DataAccessWidget dataAccessWidget = createDataAccessWidget(datasetAccess, freeAvailable);
        dataAccessWidget.getAction().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(datasetAccess instanceof DatasetAccessFile) {
                    String fileUri = datasetAccess.getUri();
                    if(fileUri.startsWith("./")) {
                        fileUri = GWT.getHostPageBaseURL() + "uploaded/" + fileUri;
                    }
                    Window.open(fileUri, "_blank", null);
                } else if(datasetAccess instanceof DatasetAccessAPP) {
                    Window.open(datasetAccess.getUri(), "_blank", null);
                } else if(datasetAccess instanceof DatasetAccessOGC) {
                    if(freeAvailable) {
                        template.getClientFactory().getPlaceController().goTo(new VisualisationPlace(
                                Utils.generateTokens(
                                        VisualisationPlace.TOKENS.productServiceId.toString(), productServiceDescriptionDTO.getId() + "",
                                        VisualisationPlace.TOKENS.dataAccessId.toString(), datasetAccess.getId() + ""
                                )));
                    }
                } else if(datasetAccess instanceof DatasetAccessAPI) {
                    Window.alert("TODO - show API end point and redirect to API help page? eg " + datasetAccess.getUri());
                }
            }
        });
        return dataAccessWidget;
    }

    private DataAccessWidget createDataAccessWidget(final DatasetAccess datasetAccess, final boolean freeAvailable) {
        DataAccessWidget dataAccessWidget = new DataAccessWidget(datasetAccess, freeAvailable);
        dataAccessWidget.getElement().getStyle().setMarginTop(20, com.google.gwt.dom.client.Style.Unit.PX);
        dataAccessWidget.getElement().getStyle().setMarginBottom(20, com.google.gwt.dom.client.Style.Unit.PX);
        return dataAccessWidget;
    }

    private void addColumnLine(MaterialWidget materialWidget) {
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        materialColumn.add(materialWidget);
        tabsContent.add(materialColumn);
    }

    private void addColumnSection(String message) {
        addColumnLine(createColumnSection(message));
    }

    private MaterialLabel createColumnSection(String message) {
        MaterialLabel label = new MaterialLabel(message);
        label.addStyleName(style.section());
        return label;
    }

    private void addTab(MaterialTab materialTab, MaterialPanel tabPanel, String name, Panel panel, int size) {
        String tabId = "fullViewTab" + materialTab.getWidgetCount();
        MaterialTabItem materialTabItem = new MaterialTabItem();
        materialTabItem.setWaves(WavesType.GREEN);
        materialTabItem.setGrid("s" + size + " m" + size + " l" + size);
        MaterialLink materialLink = new MaterialLink(name);
        materialLink.setHref("#" + tabId);
        materialLink.setTextColor(Color.WHITE);
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
        materialTab.setBackgroundColor(Color.TRANSPARENT);
        materialTab.setIndicatorColor(Color.WHITE);
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