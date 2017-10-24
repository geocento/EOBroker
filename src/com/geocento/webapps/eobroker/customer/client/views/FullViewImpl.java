package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.DataAccessUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.*;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.ExtentJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.WMSLayerInfoJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.WMSLayerJSNI;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.*;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.widgets.*;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class FullViewImpl extends Composite implements FullView {

    interface FullViewImplUiBinder extends UiBinder<Widget, FullViewImpl> {
    }

    private static FullViewImplUiBinder ourUiBinder = GWT.create(FullViewImplUiBinder.class);

    static interface Style extends CssResource {

        String section();

        String subsection();

        String sectionLabel();

        String tabPanel();

        String vertical();

        String offeringSection();

        String offeringSubSection();

        String comment();
    }

    static DateTimeFormat fmt = DateTimeFormat.getFormat("MMM-yyyy");

    @UiField Style style;

    @UiField
    MaterialPanel tabsContent;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialImage image;
    @UiField
    HTMLPanel tags;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialPanel colorPanel;
    @UiField
    MaterialPanel recommendationsPanel;
    @UiField
    MaterialNavBar navigation;
    @UiField
    MaterialPanel actions;
    @UiField
    MaterialBreadcrumb recommendationsLabel;

    private Presenter presenter;

    private MaterialTab materialTab;

    private ClientFactoryImpl clientFactory;

    public FullViewImpl(ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
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
    public void displayProduct(ProductDescriptionDTO productDescriptionDTO) {
        clearDetails();
        image.setUrl(Utils.getImageMaybe(productDescriptionDTO.getImageUrl()));
        title.setText(productDescriptionDTO.getName());
        description.setText(productDescriptionDTO.getShortDescription());
        setTabPanelColor(CategoryUtils.getColor(Category.products));
        // add tags
        {
            addTag(productDescriptionDTO.getThematic().getName(), Color.BLUE, Color.WHITE);
            addTag(productDescriptionDTO.getSector().getName(), Color.GREY, Color.WHITE);
            {
                ProductDTO productDTO = new ProductDTO();
                productDTO.setId(productDescriptionDTO.getId());
                productDTO.setFollowers(productDescriptionDTO.getFollowers());
                productDTO.setFollowing(productDescriptionDTO.isFollowing());
                ProductFollowWidget followWidget = new ProductFollowWidget(productDTO);
                followWidget.setName("product");
                followWidget.getElement().getStyle().setMarginLeft(10, com.google.gwt.dom.client.Style.Unit.PX);
                tags.add(followWidget);
            }
        }
        {
            addAction("REQUEST QUOTE", "#" + PlaceHistoryHelper.convertPlace(
                    new ProductFormPlace(
                            ProductFormPlace.TOKENS.id.toString() + "=" + productDescriptionDTO.getId())));
        }
        // add description
        MaterialPanel fullDescriptionPanel = createFullDescriptionPanel(productDescriptionDTO.getDescription());
        addSection("Description", fullDescriptionPanel);
        // create tab panel for offering
        MaterialPanel offeringPanel = new MaterialPanel();
        setMatchingDatasets(offeringPanel, "Off the shelf products for this product category", productDescriptionDTO.getProductDatasets(), "#" + PlaceHistoryHelper.convertPlace(new SearchPagePlace(Utils.generateTokens(
                SearchPagePlace.TOKENS.category.toString(), Category.productdatasets.toString(),
                SearchPagePlace.TOKENS.product.toString(), productDescriptionDTO.getId() + ""
                ))));
        addSection("Off the shelf products available", offeringPanel);

        offeringPanel = new MaterialPanel();
        setMatchingServices(offeringPanel, "Bespoke services available for this product category", productDescriptionDTO.getProductServices(), "#" + PlaceHistoryHelper.convertPlace(new SearchPagePlace(Utils.generateTokens(
                SearchPagePlace.TOKENS.category.toString(), Category.productservices.toString()))));
        addSection("Bespoke services available", offeringPanel);

        offeringPanel = new MaterialPanel();
        setMatchingSoftwares(offeringPanel, "Software solutions supporting the generation of this product category", productDescriptionDTO.getSoftwares(), "#" + PlaceHistoryHelper.convertPlace(new SearchPagePlace(Utils.generateTokens(
                SearchPagePlace.TOKENS.category.toString(), Category.software.toString()))));
        addSection("Software solutions available", offeringPanel);

        offeringPanel = new MaterialPanel();
        setMatchingProjects(offeringPanel, "R&D projects working on this product category", productDescriptionDTO.getProjects(), "#" + PlaceHistoryHelper.convertPlace(new SearchPagePlace(Utils.generateTokens(
                SearchPagePlace.TOKENS.category.toString(), Category.project.toString()))));
        addSection("R&D projects related", offeringPanel);

        // add suggestions
        recommendationsPanel.clear();
        recommendationsLabel.setText("Similar products...");
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

    private void addSection(String label, String comment, Widget widget) {
        tabsContent.add(createSection(label, comment, widget));
    }

    private void addSection(String label, Widget widget) {
        addSection(label, null, widget);
    }

    private ExpandPanel createSection(String label, String comment, Widget widget) {
        ExpandPanel expandPanel = new ExpandPanel();
        expandPanel.setLabel(label);
        expandPanel.setLabelStyle(style.sectionLabel());
        expandPanel.setLabelColor(Color.WHITE);
        //expandPanel.setContentMargin(20);
        MorePanel morePanel = new MorePanel(300);
        widget.getElement().getStyle().setMarginLeft(20, com.google.gwt.dom.client.Style.Unit.PX);
        if(comment != null && comment.length() > 0) {
            MaterialPanel materialPanel = new MaterialPanel();
            materialPanel.add(createComment(comment));
            materialPanel.add(widget);
            morePanel.setContent(materialPanel);
        } else {
            morePanel.setContent(widget);
        }
        expandPanel.setContent(morePanel);
        expandPanel.setOpen(true);
        return expandPanel;
    }

    private ExpandPanel createSection(String label, Widget widget) {
        return createSection(label, null, widget);
    }

    private void setTabPanelColor(Color color) {
        colorPanel.setBackgroundColor(color);
        navigation.setBackgroundColor(color);
    }

    @Override
    public void displayProductService(final ProductServiceDescriptionDTO productServiceDescriptionDTO) {
        clearDetails();
        // insert header with information on company and product category
        Color color = CategoryUtils.getColor(Category.productservices);
        setTabPanelColor(color);
        {
            addBreadcrumb(productServiceDescriptionDTO.getCompany(), Category.productservices);
            addBreadcrumb(productServiceDescriptionDTO.getProduct(), Category.productservices);
        }
        image.setUrl(Utils.getImageMaybe(productServiceDescriptionDTO.getServiceImage()));
        title.setText(productServiceDescriptionDTO.getName());
        description.setText(productServiceDescriptionDTO.getDescription());

        // now add the sections
        MaterialPanel fullDescriptionPanel = createFullDescriptionPanel(productServiceDescriptionDTO.getFullDescription());
        addSection("Description", fullDescriptionPanel);

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
                                ProductFormPlace.TOKENS.serviceid.toString() + "=" + productServiceDescriptionDTO.getId())));
            }
            {
                addAction("CONTACT", "#" + PlaceHistoryHelper.convertPlace(
                        new ConversationPlace(
                                Utils.generateTokens(
                                        ConversationPlace.TOKENS.companyid.toString(), productServiceDescriptionDTO.getCompany().getId() + "",
                                        ConversationPlace.TOKENS.topic.toString(), "Information on service '" + productServiceDescriptionDTO.getName() + "'"))));
            }
        }

        // create tab panel for services
        MaterialPanel featuresPanel = new MaterialPanel();
        MaterialRow featuresPanelRow = new MaterialRow();
        featuresPanel.add(featuresPanelRow);
        {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
            featuresPanelRow.add(materialColumn);
            // add geoinformation provided
            materialColumn.add(createGeoinformationPanel(productServiceDescriptionDTO.getGeoinformation(), productServiceDescriptionDTO.getGeoinformationComment()));
        }
        // add perfomances
        {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
            featuresPanelRow.add(materialColumn);
            materialColumn.add(createPerformancesPanel(productServiceDescriptionDTO.getPerformances(), productServiceDescriptionDTO.getPerformancesComments()));
        }
        // add panel for comparing if there are other services
        if(productServiceDescriptionDTO.getSuggestedServices().size() > 0) {
            MaterialPanel comparePanel = new MaterialPanel();
            comparePanel.setTextColor(Color.GREEN_DARKEN_3);
            featuresPanel.add(comparePanel);
            // add label with drop down to select the service
            MaterialLabel compareLabel = new MaterialLabel("Compare with other bespoke service:");
            MaterialLink compareLink = new MaterialLink("select service");
            compareLink.setDisplay(Display.INLINE_BLOCK);
            compareLink.setMarginLeft(10);
            compareLink.setIconType(IconType.ARROW_DROP_DOWN);
            compareLink.setIconSize(IconSize.SMALL);
            compareLink.setIconPosition(IconPosition.RIGHT);
            compareLink.setActivates("listServices");
            compareLabel.add(compareLink);
            // create the drop down for services
            MaterialDropDown listServices = new MaterialDropDown();
            listServices.setConstrainWidth(false);
            listServices.setBelowOrigin(true);
            listServices.setPadding(5);
            listServices.setActivator("listServices");
            comparePanel.add(listServices);
            comparePanel.add(compareLabel);
            // add placeholder for the information to load for services
            MaterialRow comparePanelRow = new MaterialRow();
            comparePanelRow.setMarginBottom(20);
            comparePanelRow.setTextColor(Color.GREEN_DARKEN_3);
            featuresPanel.add(comparePanelRow);
            // TODO - load when clicked
            for (ProductServiceDTO productServiceDTO : productServiceDescriptionDTO.getSuggestedServices()) {
                MaterialLink serviceLink = new MaterialLink(productServiceDTO.getName());
                serviceLink.setTruncate(true);
                serviceLink.getElement().getStyle().setProperty("minWidth", "200px");
                serviceLink.getElement().getStyle().setProperty("maxWidth", "400px");
                listServices.add(serviceLink);
                serviceLink.addClickHandler(event -> {
                    // make sure the panel is fully displayed
                    MorePanel morePanel = (MorePanel) WidgetUtil.findParent(comparePanelRow, widget -> widget instanceof MorePanel);
                    if (morePanel != null) {
                        morePanel.displayMoreContent(true);
                    }
                    try {
                        comparePanelRow.clear();
                        comparePanelRow.add(new LoadingWidget("Loading service description..."));
                        REST.withCallback(new MethodCallback<ProductServiceDescriptionDTO>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                compareLink.setText("select service");
                                comparePanelRow.clear();
                                MaterialMessage message = new MaterialMessage();
                                message.displayErrorMessage("Failed to load service description...");
                                comparePanelRow.add(message);
                            }

                            @Override
                            public void onSuccess(Method method, ProductServiceDescriptionDTO compareProductDescriptionDTO) {
                                compareLink.setText(compareProductDescriptionDTO.getName());
                                comparePanelRow.clear();
                                {
                                    MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
                                    comparePanelRow.add(materialColumn);
                                    // add geoinformation provided
                                    materialColumn.add(createGeoinformationPanel(compareProductDescriptionDTO.getGeoinformation(), compareProductDescriptionDTO.getGeoinformationComment()));
                                }
                                // add perfomances
                                {
                                    MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
                                    comparePanelRow.add(materialColumn);
                                    materialColumn.add(createPerformancesPanel(compareProductDescriptionDTO.getPerformances(), compareProductDescriptionDTO.getPerformancesComments()));
                                }
                                {
                                    MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
                                    // add information on service being compared
                                    comparePanelRow.add(materialColumn);
                                    MaterialLabelIcon materialLabelIcon = new MaterialLabelIcon();
                                    materialLabelIcon.setImageUrl(compareProductDescriptionDTO.getServiceImage());
                                    materialLabelIcon.setImageHeight("32px");
                                    materialLabelIcon.setSpacing(10);
                                    materialLabelIcon.setText(compareProductDescriptionDTO.getName() + " provided by company " + compareProductDescriptionDTO.getCompany().getName());
                                    materialLabelIcon.getElement().getStyle().setProperty("margin", "10px 0px");
                                    materialColumn.add(materialLabelIcon);
                                    // add link to view the service
                                    MaterialLink viewService = new MaterialLink(IconType.OPEN_IN_NEW);
                                    viewService.setHref("#" + PlaceHistoryHelper.convertPlace(
                                            new FullViewPlace(Utils.generateTokens(
                                                    FullViewPlace.TOKENS.productserviceid.toString(), compareProductDescriptionDTO.getId() + ""
                                            ))));
                                    viewService.setDisplay(Display.INLINE_BLOCK);
                                    viewService.setMarginLeft(10);
                                    viewService.getElement().getStyle().setVerticalAlign(com.google.gwt.dom.client.Style.VerticalAlign.MIDDLE);
                                    materialLabelIcon.add(viewService);
                                }
                            }
                        }).call(ServicesUtil.assetsService).getProductServiceDescription(productServiceDTO.getId());
                    } catch (Exception e) {

                    }
                });
            }
        }
        addSection("Geoinformation provided", "This is the list of geo information features nominally provided by the service and the expected performances", featuresPanel);

        boolean worldWide = productServiceDescriptionDTO.getExtent() == null;
        MaterialPanel extentPanel = new MaterialPanel();
        extentPanel.setPaddingRight(20);
        final MapContainer mapContainer = new MapContainer();
        mapContainer.setHeight("400px");
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
                if (worldWide) {
                    mapContainer.map.setZoom(1);
                } else {
                    mapContainer.displayAoI(AoIUtil.fromWKT(productServiceDescriptionDTO.getExtent()));
                    mapContainer.centerOnAoI();
                }
            }
        });
        extentPanel.add(mapContainer);
        extentPanel.setPaddingBottom(20);
        List<DatasetAccessOGC> coverageLayers = productServiceDescriptionDTO.getCoverageLayers();
        if(coverageLayers != null && coverageLayers.size() > 0) {
            extentPanel.add(createComment("Additional information is available for the coverage of this service, click on the stats layers icon in the map above"));
            addCoverageLayers(mapContainer, coverageLayers);
        }
        addSection("Extent of the service",
                worldWide ? "This bespoke service is available worldwide" : "This is the area that can be covered by the bespoke service"
                , extentPanel);

        // add time delivery information
        if(productServiceDescriptionDTO.getTimeToDelivery() != null) {
            MaterialPanel timeToDeliveryPanel = new MaterialPanel();
            MaterialLabel timeDeliveryLabel = new MaterialLabel();
            timeDeliveryLabel.setPadding(20);
            timeDeliveryLabel.setText(productServiceDescriptionDTO.getTimeToDelivery());
            timeToDeliveryPanel.add(timeDeliveryLabel);
            addSection("Time to delivery", "Time to delivery indicates the time it will take between the availability of data and the final product is delivered", timeToDeliveryPanel);
            //materialColumn.add(performancesPanel);
        }
        // add access panel
        MaterialPanel accessPanel = new MaterialPanel();
        List<DatasetAccess> availableMapData = new ArrayList<DatasetAccess>();
        {
            MaterialPanel disseminationPanel = new MaterialPanel();
            disseminationPanel.setMargin(10);
            accessPanel.add(disseminationPanel);
            List<AccessType> accessTypes = productServiceDescriptionDTO.getSelectedAccessTypes();
            for (AccessType accessType : AccessType.values()) {
                MaterialLink materialLink = new MaterialLink(accessType.getName());
                materialLink.setDisplay(Display.BLOCK);
                materialLink.setMargin(10);
                disseminationPanel.setMarginBottom(30);
                materialLink.setTextColor(Color.BLACK);
                if(accessTypes != null && accessTypes.contains(accessType)) {
                    materialLink.setIconType(IconType.CHECK);
                    materialLink.setIconColor(Color.AMBER);
                    disseminationPanel.add(materialLink);
                } else {
                    materialLink.setIconType(IconType.CHECK_BOX_OUTLINE_BLANK);
                    materialLink.setIconColor(Color.BLACK);
                }
            }
            addSection("Dissemination methods supported",
                    "This is the list of supported methods for accessing the final products when they are delivered",
                    disseminationPanel
            );
        }

        // add samples panel
        List<DatasetAccess> samples = productServiceDescriptionDTO.getSamples();
        if(samples != null && samples.size() > 0) {
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
            addSection("Provided product samples",
                    "Here are a few product samples for this service, click on icon to access the data.",
                    materialRow
            );
        } else {
            addSection("Provided product samples",
                    "No product samples are available for this service",
                    createSubsection("")
            );
        }

        // add terms and conditions for the service
        if(productServiceDescriptionDTO.getTermsAndConditions() != null) {
            addSection("Terms and conditions",
                    null,
                    new HTML(productServiceDescriptionDTO.getTermsAndConditions()));
        } else {
            addSection("Terms and conditions",
                    "No terms and conditions specified",
                    createSubsection(""));
        }

        recommendationsPanel.clear();
        recommendationsLabel.setText("You might also be interested in...");
        List<ProductServiceDTO> suggestedServices = productServiceDescriptionDTO.getSuggestedServices();
        if(suggestedServices == null || suggestedServices.size() == 0) {
            MaterialLabel materialLabel = new MaterialLabel("No suggestions...");
            materialLabel.setPadding(20);
            recommendationsPanel.add(materialLabel);
        } else {
            MaterialRow materialRow = new MaterialRow();
            recommendationsPanel.add(materialRow);
            for(ProductServiceDTO productServiceDTO : productServiceDescriptionDTO.getSuggestedServices()) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                ProductServiceWidget productServiceWidget = new ProductServiceWidget(productServiceDTO);
                materialColumn.add(productServiceWidget);
                materialRow.add(materialColumn);
            }
        }
    }

    private void addCoverageLayers(MapContainer mapContainer, List<DatasetAccessOGC> coverageLayers) {
        MaterialLoadingButton layerName = new MaterialLoadingButton();
        layerName.getElement().getStyle().setProperty("maxWidth", "30%");
        layerName.setTruncate(true);
        layerName.setActivates("layersList");
        layerName.setBackgroundColor(Color.WHITE);
        layerName.setText("No layer selected");
        layerName.setTextColor(Color.BLACK);
        layerName.setIconType(IconType.ARROW_DROP_DOWN);
        layerName.setIconSize(IconSize.SMALL);
        layerName.setIconPosition(IconPosition.RIGHT);
        layerName.setIconColor(Color.BLACK);
        mapContainer.addControl(layerName, Position.TOP, Position.RIGHT);
        MaterialDropDown layersList = new MaterialDropDown();
        layersList.setPadding(5);
        layersList.setBackgroundColor(Color.WHITE);
        layersList.setActivator("layersList");
        for(DatasetAccessOGC datasetAccessOGC : coverageLayers) {
            MaterialLink layerItem = new MaterialLink();
            layerItem.setText(datasetAccessOGC.getTitle());
            if(datasetAccessOGC.getPitch() != null) {
                MaterialTooltip materialTooltip = new MaterialTooltip(layerItem);
                materialTooltip.setText(datasetAccessOGC.getPitch());
            }
            layerItem.addClickHandler(new ClickHandler() {

                public WMSLayerJSNI layer;

                @Override
                public void onClick(ClickEvent event) {
                    if(layer != null) {
                        mapContainer.map.removeWMSLayer(layer);
                    }
                    if(datasetAccessOGC != null) {
                        layerName.setLoading(true);
                        layerName.setText("Loading layer...");
                        try {
                            REST.withCallback(new MethodCallback<LayerInfoDTO>() {

                                @Override
                                public void onFailure(Method method, Throwable exception) {
                                    layerName.setLoading(false);
                                    layerName.setText("No selected layer...");
                                    MaterialToast.fireToast("Could not load layer...");
                                }

                                @Override
                                public void onSuccess(Method method, LayerInfoDTO layerInfoDTO) {
                                    layerName.setLoading(false);
                                    layerName.setText(datasetAccessOGC.getTitle());
                                    Extent extent = layerInfoDTO.getExtent();
                                    ExtentJSNI extentJSNI = MapJSNI.createExtent(extent.getWest(), extent.getSouth(), extent.getEast(), extent.getNorth());
                                    layer = mapContainer.map.addWMSLayer(layerInfoDTO.getServerUrl(),
                                            WMSLayerInfoJSNI.createInfo(layerInfoDTO.getLayerName(), layerInfoDTO.getLayerName()),
                                            extentJSNI, layerInfoDTO.getStyleName());
                                    mapContainer.map.setExtent(extentJSNI);
                                }
                            }).call(ServicesUtil.assetsService).getLayerInfo(datasetAccessOGC.getId());
                        } catch (RequestException e) {
                        }
                    }
                }
            });
            layersList.add(layerItem);
        }
        mapContainer.addWidget(layersList);
    }

    private Widget createComment(String comment) {
        MaterialLabel commentLabel = new MaterialLabel(comment);
        commentLabel.addStyleName(style.comment());
        return commentLabel;
    }

    private Widget createPerformancesPanel(List<PerformanceValue> performances, String comment) {
        MaterialPanel performancesPanel = new MaterialPanel();
        performancesPanel.add(createSubsection("Precision and accuracy"));
        MaterialRow materialRow = new MaterialRow();
        materialRow.setMarginTop(20);
        performancesPanel.add(materialRow);
        if(performances.size() == 0) {
            MaterialColumn materialColumn = new MaterialColumn();
            materialRow.add(materialColumn);
            materialColumn.add(new MaterialLabel("No information provided..."));
        } else {
            for (PerformanceValue performance : performances) {
                MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
                materialRow.add(materialColumn);
                MaterialLink materialLink = new MaterialLink(performance.getPerformanceDescription().getName() +
                        " " + performance.getMinValue() +
                        (performance.getMaxValue() != null ? " - " + performance.getMaxValue() : "") +
                        " " + performance.getPerformanceDescription().getUnit());
                materialLink.setIconType(IconType.CHECK);
                materialLink.setIconColor(Color.AMBER);
                MaterialTooltip materialTooltip = new MaterialTooltip(materialLink, performance.getPerformanceDescription().getDescription());
                materialColumn.add(materialTooltip);
            }
        }
        if(comment != null && comment.length() > 0) {
            MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
            materialRow.add(materialColumn);
            MaterialLabel commentLabel = new MaterialLabel("Comment: " + comment);
            commentLabel.addStyleName(style.comment());
            //commentLabel.getElement().getStyle().setMarginLeft(10, com.google.gwt.dom.client.Style.Unit.PX);
            materialColumn.add(commentLabel);
        }
        return performancesPanel;
    }

    private MaterialPanel createGeoinformationPanel(List<FeatureDescription> geoinformation, String comment) {
        MaterialPanel geoinformationPanel = new MaterialPanel();
        geoinformationPanel.add(createSubsection("Geoinformation provided"));
        MaterialRow geoinformationRow = new MaterialRow();
        geoinformationRow.setMarginTop(20);
        geoinformationPanel.add(geoinformationRow);
        if(geoinformation.size() == 0) {
            MaterialColumn materialColumn = new MaterialColumn();
            geoinformationRow.add(materialColumn);
            materialColumn.add(new MaterialLabel("No geoinformation provided..."));
        } else {
            for (FeatureDescription featureDescription : geoinformation) {
                MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
                geoinformationRow.add(materialColumn);
                MaterialLink materialLink = new MaterialLink(featureDescription.getName());
                materialLink.setIconType(IconType.CHECK);
                materialLink.setIconColor(Color.AMBER);
                MaterialTooltip materialTooltip = new MaterialTooltip(materialLink, featureDescription.getDescription());
                materialColumn.add(materialTooltip);
            }
        }
        if(comment != null && comment.length() > 0) {
            MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
            geoinformationRow.add(materialColumn);
            MaterialLabel commentLabel = new MaterialLabel(comment);
            commentLabel.getElement().setInnerText("Comment: " + comment);
            commentLabel.addStyleName(style.comment());
            //commentLabel.getElement().getStyle().setMarginLeft(10, com.google.gwt.dom.client.Style.Unit.PX);
            materialColumn.add(commentLabel);
        }
        return geoinformationPanel;
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
            addTag(IconType.MONETIZATION_ON, productDatasetDescriptionDTO.isCommercial() ? "Commercial" : "Free", Color.AMBER, Color.WHITE);
        }

        // add actions
        if(productDatasetDescriptionDTO.getCatalogueStandard() != null) {
            addAction("EXPLORE", "#" + PlaceHistoryHelper.convertPlace(
                    new CatalogueSearchPlace(
                            Utils.generateTokens(
                                    CatalogueSearchPlace.TOKENS.productId.toString(), productDatasetDescriptionDTO.getId() + ""))));
        }
        addAction("CONTACT", "#" + PlaceHistoryHelper.convertPlace(
                new ConversationPlace(
                        Utils.generateTokens(
                                ConversationPlace.TOKENS.companyid.toString(), productDatasetDescriptionDTO.getCompany().getId() + "",
                                ConversationPlace.TOKENS.topic.toString(), "Information on off the shelf product '" + productDatasetDescriptionDTO.getName() + "'"))));
        // add description
        MaterialPanel fullDescriptionPanel = createFullDescriptionPanel(productDatasetDescriptionDTO.getFullDescription());
        addSection("Description", fullDescriptionPanel);

        // create tab panel for services
        MaterialPanel featuresPanel = new MaterialPanel();
        {
            MaterialRow featuresPanelRow = new MaterialRow();
            featuresPanel.add(featuresPanelRow);
            {
                // add geoinformation provided
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
                featuresPanelRow.add(materialColumn);
                // add geoinformation provided
                materialColumn.add(createGeoinformationPanel(productDatasetDescriptionDTO.getGeoinformation(), productDatasetDescriptionDTO.getGeoinformationComment()));
            }
            {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
                featuresPanelRow.add(materialColumn);
                // add perfomances
                materialColumn.add(createPerformancesPanel(productDatasetDescriptionDTO.getPerformances(), productDatasetDescriptionDTO.getPerformancesComments()));
            }
        }
        // add panel for comparing if there are other off the shelf products
        if(productDatasetDescriptionDTO.getSuggestedDatasets().size() > 0) {
            MaterialPanel comparePanel = new MaterialPanel();
            comparePanel.setTextColor(Color.GREEN_DARKEN_3);
            featuresPanel.add(comparePanel);
            // add label with drop down to select the off the shelf product
            MaterialLabel compareLabel = new MaterialLabel("Compare with other off the shelf products:");
            MaterialLink compareLink = new MaterialLink("select off the shelf product");
            compareLink.setDisplay(Display.INLINE_BLOCK);
            compareLink.setMarginLeft(10);
            compareLink.setIconType(IconType.ARROW_DROP_DOWN);
            compareLink.setIconSize(IconSize.SMALL);
            compareLink.setIconPosition(IconPosition.RIGHT);
            compareLink.setActivates("listDatasets");
            compareLabel.add(compareLink);
            // create the drop down for off the shelf products
            MaterialDropDown listDatasets = new MaterialDropDown();
            listDatasets.setConstrainWidth(false);
            listDatasets.setBelowOrigin(true);
            listDatasets.setPadding(5);
            listDatasets.setActivator("listDatasets");
            comparePanel.add(listDatasets);
            comparePanel.add(compareLabel);
            // add placeholder for the information to load for the off the shelf product
            MaterialRow comparePanelRow = new MaterialRow();
            comparePanelRow.setMarginBottom(20);
            comparePanelRow.setTextColor(Color.GREEN_DARKEN_3);
            featuresPanel.add(comparePanelRow);
            // TODO - load when clicked
            for (ProductDatasetDTO productDatasetDTO : productDatasetDescriptionDTO.getSuggestedDatasets()) {
                MaterialLink datasetLink = new MaterialLink(productDatasetDTO.getName());
                datasetLink.getElement().getStyle().setProperty("minWidth", "200px");
                datasetLink.getElement().getStyle().setProperty("maxWidth", "400px");
                listDatasets.add(datasetLink);
                datasetLink.addClickHandler(event -> {
                    // make sure the panel is fully displayed
                    MorePanel morePanel = (MorePanel) WidgetUtil.findParent(comparePanelRow, widget -> widget instanceof MorePanel);
                    if (morePanel != null) {
                        morePanel.displayMoreContent(true);
                    }
                    try {
                        comparePanelRow.clear();
                        comparePanelRow.add(new LoadingWidget("Loading off the shelf product description..."));
                        REST.withCallback(new MethodCallback<ProductDatasetDescriptionDTO>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                compareLink.setText("select off the shelf product");
                                comparePanelRow.clear();
                                MaterialMessage message = new MaterialMessage();
                                message.displayErrorMessage("Failed to load off the shelf product description...");
                                comparePanelRow.add(message);
                            }

                            @Override
                            public void onSuccess(Method method, ProductDatasetDescriptionDTO compareProductDescriptionDTO) {
                                compareLink.setText(compareProductDescriptionDTO.getName());
                                comparePanelRow.clear();
                                {
                                    MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
                                    comparePanelRow.add(materialColumn);
                                    // add geoinformation provided
                                    materialColumn.add(createGeoinformationPanel(compareProductDescriptionDTO.getGeoinformation(), compareProductDescriptionDTO.getGeoinformationComment()));
                                }
                                // add perfomances
                                {
                                    MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
                                    comparePanelRow.add(materialColumn);
                                    materialColumn.add(createPerformancesPanel(compareProductDescriptionDTO.getPerformances(), compareProductDescriptionDTO.getPerformancesComments()));
                                }
                                {
                                    MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
                                    // add information on off the shelf product being compared
                                    comparePanelRow.add(materialColumn);
                                    MaterialLabelIcon materialLabelIcon = new MaterialLabelIcon();
                                    materialLabelIcon.setImageUrl(compareProductDescriptionDTO.getImageUrl());
                                    materialLabelIcon.setImageHeight("32px");
                                    materialLabelIcon.setSpacing(10);
                                    materialLabelIcon.setText(compareProductDescriptionDTO.getName() + " provided by company " + compareProductDescriptionDTO.getCompany().getName());
                                    materialLabelIcon.getElement().getStyle().setProperty("margin", "10px 0px");
                                    materialColumn.add(materialLabelIcon);
                                    // add link to view the off the shelf product
                                    MaterialLink viewDataset = new MaterialLink(IconType.OPEN_IN_NEW);
                                    viewDataset.setHref("#" + PlaceHistoryHelper.convertPlace(
                                            new FullViewPlace(Utils.generateTokens(
                                                    FullViewPlace.TOKENS.productdatasetid.toString(), compareProductDescriptionDTO.getId() + ""
                                            ))));
                                    viewDataset.setDisplay(Display.INLINE_BLOCK);
                                    viewDataset.setMarginLeft(10);
                                    viewDataset.getElement().getStyle().setVerticalAlign(com.google.gwt.dom.client.Style.VerticalAlign.MIDDLE);
                                    materialLabelIcon.add(viewDataset);
                                }
                            }
                        }).call(ServicesUtil.assetsService).getProductDatasetDescription(productDatasetDTO.getId());
                    } catch (Exception e) {

                    }
                });
            }
        }
        addSection("Geoinformation provided", "This is the list of geo information features provided by this off the shelf product and the nominal performances", featuresPanel);
        // add map with extent of data
        {
            MaterialPanel extentPanel = new MaterialPanel();
            extentPanel.setPaddingRight(20);
            boolean worldWide = productDatasetDescriptionDTO.getExtent() == null;
            final MapContainer mapContainer = new MapContainer();
            mapContainer.setHeight("300px");
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
                    if (worldWide) {
                        mapContainer.map.setZoom(1);
                    } else {
                        mapContainer.displayAoI(AoIUtil.fromWKT(productDatasetDescriptionDTO.getExtent()));
                        mapContainer.centerOnAoI();
                    }
                }
            });
            extentPanel.add(mapContainer);
            extentPanel.setPaddingBottom(20);
            List<DatasetAccessOGC> coverageLayers = productDatasetDescriptionDTO.getCoverageLayers();
            if(coverageLayers != null && coverageLayers.size() > 0) {
                extentPanel.add(createComment("Additional information is available for the coverage of this off the shelf data product, select a stats layer in the map above"));
                addCoverageLayers(mapContainer, coverageLayers);
            }
            addSection("Extent of the off the shelf product",
                    worldWide ? "This off the shelf data product is available worldwide" : "This is the area that is covered by the off the shelf data product"
                    , extentPanel);
        }
        // add access panel
        List<DatasetAccess> availableMapData = new ArrayList<DatasetAccess>();
        List<DatasetAccess> dataAccesses = productDatasetDescriptionDTO.getDatasetAccesses();
        if(dataAccesses != null && dataAccesses.size() > 0) {
            MaterialRow materialRow = new MaterialRow();
            materialRow.setMargin(10);
            materialRow.setMarginBottom(30);
            for (DatasetAccess datasetAccess : dataAccesses) {
                MaterialColumn materialColumnAccess = new MaterialColumn(12, 12, 6);
                materialRow.add(materialColumnAccess);
                boolean freeAvailable = !productDatasetDescriptionDTO.isCommercial();
                DataAccessWidget dataAccessWidget = createDataAccessWidgetProductDataset(productDatasetDescriptionDTO, datasetAccess, freeAvailable);
                materialColumnAccess.add(dataAccessWidget);
                if (datasetAccess instanceof DatasetAccessOGC && freeAvailable) {
                    availableMapData.add(datasetAccess);
                }
            }
            addSection("Methods to access the data",
                    "The data for this off the shelf product can be accessed using the following methods:",
                    materialRow);
        } else {
            addSection("Methods to access the data",
                    "No access method has been provided",
                    createSubsection(""));
        }
/*
        // check for catalogue browsing
        if(productDatasetDescriptionDTO.getCatalogueStandard() != null) {
            MaterialRow materialRow = new MaterialRow();
            materialRow.setMargin(10);
            materialRow.setMarginBottom(30);
            accessPanel.add(materialRow);
            MaterialLabel label = new MaterialLabel("This off the shelf data contains sub products which can be browsed ");
            MaterialLink materialLink = new MaterialLink();
*/
/*
            materialLink.setIconPosition(IconPosition.RIGHT);
*//*

            materialLink.setText("here");
            materialLink.setMarginLeft(0);
            materialLink.setHref("#" + PlaceHistoryHelper.convertPlace(new CatalogueSearchPlace(
                    Utils.generateTokens(CatalogueSearchPlace.TOKENS.productId.toString(), productDatasetDescriptionDTO.getId() + ""))));
            label.add(materialLink);
            label.setFontSize(1.2, com.google.gwt.dom.client.Style.Unit.EM);
            MaterialColumn materialColumnAccess = new MaterialColumn(12, 12, 12);
            materialRow.add(materialColumnAccess);
            materialColumnAccess.add(label);
        }
*/
        List<DatasetAccess> samples = productDatasetDescriptionDTO.getSamples();
        if(samples != null && samples.size() > 0) {
            MaterialRow materialRow = new MaterialRow();
            materialRow.setMargin(10);
            materialRow.setMarginBottom(30);
            for(DatasetAccess datasetAccess : samples) {
                MaterialColumn materialColumnSample = new MaterialColumn(12, 12, 6);
                materialRow.add(materialColumnSample);
                materialColumnSample.add(createDataAccessWidgetProductDataset(productDatasetDescriptionDTO, datasetAccess, true));
                if(datasetAccess instanceof DatasetAccessOGC) {
                    availableMapData.add(datasetAccess);
                }
            }
            addSection("Sample data",
                    "Sample data from this off the shelf product has been provided. Click on the icon to access the data.",
                    materialRow);
        } else {
            addSection("Sample data",
                    "No sample data has been provided",
                    createSubsection(""));
        }
        // add terms and conditions tab panel
        if(productDatasetDescriptionDTO.getTermsAndConditions() != null) {
            addSection("Terms and conditions",
                    null,
                    new HTML(productDatasetDescriptionDTO.getTermsAndConditions()));
        } else {
            addSection("Terms and conditions",
                    "No terms and conditions specified",
                    createSubsection(""));
        }

        // add recommendations
        recommendationsPanel.clear();
        recommendationsLabel.setText("You might also be interested in...");
        List<ProductDatasetDTO> suggestedDatasets = productDatasetDescriptionDTO.getSuggestedDatasets();
        if(suggestedDatasets == null || suggestedDatasets.size() == 0) {
            MaterialLabel materialLabel = new MaterialLabel("No suggestions...");
            materialLabel.setPadding(20);
            recommendationsPanel.add(materialLabel);
        } else {
            MaterialRow materialRow = new MaterialRow();
            recommendationsPanel.add(materialRow);
            for (ProductDatasetDTO productDatasetDTO : suggestedDatasets) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
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

        addBreadcrumb(softwareDescriptionDTO.getCompanyDTO(), Category.software);

        setTabPanelColor(CategoryUtils.getColor(Category.software));

        // add tags
        if(softwareDescriptionDTO.getSoftwareType() != null) {
            addTag(IconType.MONETIZATION_ON, softwareDescriptionDTO.getSoftwareType().getName(), Color.AMBER, Color.WHITE);
        }

        // add actions
        addAction("CONTACT", "#" + PlaceHistoryHelper.convertPlace(
                    new ConversationPlace(
                            Utils.generateTokens(
                                    ConversationPlace.TOKENS.companyid.toString(), softwareDescriptionDTO.getCompanyDTO().getId() + "",
                                    ConversationPlace.TOKENS.topic.toString(), "Information on software solution '" + softwareDescriptionDTO.getName() + "'"))));

        // create full description panel
        MaterialPanel fullDescriptionPanel = createFullDescriptionPanel(softwareDescriptionDTO.getFullDescription());
        addSection("Description", fullDescriptionPanel);

        // add products tab
        {
            MaterialPanel productsPanel = new MaterialPanel();
            List<ProductSoftwareDTO> productsCovered = softwareDescriptionDTO.getProducts();
            if (productsCovered == null || productsCovered.size() == 0) {
                addColumnLine(new MaterialLabel("No products..."));
            } else {
                MaterialRow materialRow = new MaterialRow();
                productsPanel.add(materialRow);
                for(ProductSoftwareDTO productSoftwareDTO : productsCovered) {
                    MaterialColumn materialColumn = new MaterialColumn(12, 12, 6);
                    ProductSoftwareWidget productSoftwareWidget = new ProductSoftwareWidget(productSoftwareDTO);
                    materialColumn.add(productSoftwareWidget);
                    materialRow.add(materialColumn);
                }
            }
            addSection("Product categories supported",
                    "This software solution helps in generating the following products",
                    productsPanel);
        }

        // add terms and conditions tab panel
        if(softwareDescriptionDTO.getTermsAndConditions() != null) {
            addSection("Terms and conditions",
                    null,
                    new HTML(softwareDescriptionDTO.getTermsAndConditions()));
        } else {
            addSection("Terms and conditions",
                    "No terms and conditions specified",
                    createSubsection(""));
        }

        // add recommendations
        recommendationsPanel.clear();
        recommendationsLabel.setText("You might also be interested in...");
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

        // add company
        addBreadcrumb(projectDescriptionDTO.getCompanyDTO(), Category.project);

        // add tags
        {
            if(projectDescriptionDTO.getStartDate() != null) {
                String timeFrame = fmt.format(projectDescriptionDTO.getStartDate()) + " - " +
                        projectDescriptionDTO.getStopDate() == null ? " on-going" : fmt.format(projectDescriptionDTO.getStopDate());
                addTag(IconType.TIMELINE, timeFrame, Color.AMBER, Color.WHITE);
            }
        }

        // add actions
        {
            addAction("CONTACT", "#" + PlaceHistoryHelper.convertPlace(
                    new ConversationPlace(
                            Utils.generateTokens(
                                    ConversationPlace.TOKENS.companyid.toString(), projectDescriptionDTO.getCompanyDTO().getId() + "",
                                    ConversationPlace.TOKENS.topic.toString(), "Information on project '" + projectDescriptionDTO.getName() + "'"))));
        }
        // create full description panel
        MaterialPanel fullDescriptionPanel = createFullDescriptionPanel(projectDescriptionDTO.getFullDescription());
        addSection("Description", fullDescriptionPanel);

        // add products tab
        {
            MaterialPanel productsPanel = new MaterialPanel();
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
            addSection("Product categories covered",
                    "This R&D pproject works on the following product categories",
                    productsPanel);
        }

        // add consortium information
        {
            MaterialPanel consortiumPanel = new MaterialPanel();
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
            addSection("Consortium",
                    "The following companies are also involved in this project",
                    consortiumPanel);
        }

        // add recommendations
        recommendationsPanel.clear();
        recommendationsLabel.setText("You might also be interested in...");
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
    public void selectSection(String tabName) {
        // TODO - find and scroll to the selected section
    }

    private MaterialChip addTag(String text, Color backgroundColor) {
        return addTag(null, text, backgroundColor, Color.WHITE);
    }

    private MaterialChip addTag(String text, Color backgroundColor, Color textColor) {
        return addTag(null, text, backgroundColor, textColor);
    }

    private MaterialChip addTag(IconType iconType, String text, Color backgroundColor, Color textColor) {
        return addTag(iconType, text, backgroundColor, textColor, null, null);
    }

    private MaterialChip addTag(IconType iconType, String text, Color backgroundColor, Color textColor, String tooltip) {
        return addTag(iconType, text, backgroundColor, textColor, tooltip, null);
    }

    private MaterialChip addTag(IconType iconType, String text, Color backgroundColor, Color textColor, String tooltip, ClickHandler clickHandler) {
        MaterialChip materialChip = new MaterialChip();
        materialChip.setText(text);
        materialChip.setBackgroundColor(backgroundColor);
        materialChip.setTextColor(textColor);
        materialChip.setMarginLeft(20);
        if(iconType != null) {
            materialChip.setIconPosition(IconPosition.LEFT);
            materialChip.setIconType(iconType);
        }
        if(clickHandler != null) {
            materialChip.addClickHandler(clickHandler);
            materialChip.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
        }
        if(tooltip != null) {
            materialChip.setTooltip(tooltip);
        }
        tags.add(materialChip);
        return materialChip;
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
            addTag(IconType.FORMAT_SIZE, companyDescriptionDTO.getCompanySize().toString(), Color.BLUE, Color.WHITE, "The company size using the European Commission definition");
        }
        if(companyDescriptionDTO.getCountryCode() != null) {
            addTag(IconType.MY_LOCATION, CountryEditor.getDisplayName(companyDescriptionDTO.getCountryCode()), Color.GREY, Color.WHITE);
        }
        {
            CompanyDTO companyDTO = new CompanyDTO();
            companyDTO.setId(companyDescriptionDTO.getId());
            companyDTO.setFollowers(companyDescriptionDTO.getFollowers());
            companyDTO.setFollowing(companyDescriptionDTO.isFollowing());
            CompanyFollowWidget followWidget = new CompanyFollowWidget(companyDTO);
            followWidget.setName("company");
            followWidget.getElement().getStyle().setMarginLeft(10, com.google.gwt.dom.client.Style.Unit.PX);
            tags.add(followWidget);
        }

        // add actions
        {
            addAction("WEBSITE", new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.open(companyDescriptionDTO.getWebsite(), "_blank;", null);
                }
            });
            addAction("CONTACT", "#" + PlaceHistoryHelper.convertPlace(
                    new ConversationPlace(
                            ConversationPlace.TOKENS.companyid.toString() + "=" + companyDescriptionDTO.getId() +
                                    "&" + ConversationPlace.TOKENS.topic.toString() + "=Request for information"
                    )));
        }
        // create full description panel
        MaterialPanel fullDescriptionPanel = createFullDescriptionPanel(companyDescriptionDTO.getFullDescription());
        addSection("Description", fullDescriptionPanel);

        // create tab panel for offers
        MaterialPanel offeringPanel = new MaterialPanel();
        setMatchingDatasets(offeringPanel, "Off the shelf products provided by this company", companyDescriptionDTO.getProductDatasets(),
                "#" + PlaceHistoryHelper.convertPlace(new SearchPagePlace(Utils.generateTokens(
                        SearchPagePlace.TOKENS.category.toString(), Category.productdatasets.toString(),
                        SearchPagePlace.TOKENS.company.toString(), companyDescriptionDTO.getId() + ""
                ))));
        addSection("Off the shelf data products", offeringPanel);

        offeringPanel = new MaterialPanel();
        setMatchingServices(offeringPanel, "Bespoke services provided by this company", companyDescriptionDTO.getProductServices(), "#" + PlaceHistoryHelper.convertPlace(new SearchPagePlace(Utils.generateTokens(
                SearchPagePlace.TOKENS.category.toString(), Category.productservices.toString(),
                SearchPagePlace.TOKENS.company.toString(), companyDescriptionDTO.getId() + ""
                ))));
        addSection("Bespoke services", offeringPanel);

        offeringPanel = new MaterialPanel();
        setMatchingSoftwares(offeringPanel, "Software solutions provided by this company", companyDescriptionDTO.getSoftware(), "#" + PlaceHistoryHelper.convertPlace(new SearchPagePlace(Utils.generateTokens(
                SearchPagePlace.TOKENS.category.toString(), Category.software.toString()))));
        addSection("Software solutions", offeringPanel);

        offeringPanel = new MaterialPanel();
        setMatchingProjects(offeringPanel, "R&D projects lead by this company", companyDescriptionDTO.getProject(), "#" + PlaceHistoryHelper.convertPlace(new SearchPagePlace(Utils.generateTokens(
                SearchPagePlace.TOKENS.category.toString(), Category.project.toString()))));
        addSection("R&D Projects", offeringPanel);

        MaterialRow credentialsPanel = new MaterialRow();
        {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
            //materialColumn.add(createColumnSection("Testimonials received"));
            List<TestimonialDTO> testimonials = companyDescriptionDTO.getTestimonials();
            boolean hasTestimonials = testimonials != null && testimonials.size() > 0;
            if (hasTestimonials) {
                materialColumn.add(createSubsection("This company has received " + testimonials.size() + " testimonials"));
                MaterialRow materialRow = new MaterialRow();
                materialColumn.add(materialRow);
                for (TestimonialDTO testimonialDTO : testimonials) {
                    MaterialColumn testimonialColumn = new MaterialColumn(12, 12, 12);
                    TestimonialWidget testimonialWidget = new TestimonialWidget(testimonialDTO);
                    testimonialWidget.displayTopic(false);
                    testimonialColumn.add(testimonialWidget);
                    materialRow.add(testimonialColumn);
                }
            } else {
                materialColumn.add(createSubsection("This company has not received any testimonials yet"));
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
                    clientFactory.getPlaceController().goTo(new TestimonialPlace(Utils.generateTokens(
                            TestimonialPlace.TOKENS.category.toString(), Category.companies.toString(),
                            TestimonialPlace.TOKENS.id.toString(), companyDescriptionDTO.getId() + "")));
                }
            });
            materialPanel.add(addTestimonial);
            materialPanel.setMarginTop(20);
            materialColumn.add(materialPanel);
            credentialsPanel.add(materialColumn);
        }
        {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 6);
            //materialColumn.add(createColumnSection("Awards received"));
            List<String> awards = companyDescriptionDTO.getAwards();
            boolean hasAwards = awards != null && awards.size() > 0;
            if(hasAwards) {
                materialColumn.add(createSubsection("This company has received " + awards.size() + " awards"));
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
                materialColumn.add(createSubsection("This company has not received any awards yet"));
            }
            credentialsPanel.add(materialColumn);
        }
        addSection("Testimonials and awards", credentialsPanel);

        // add recommendations
        recommendationsPanel.clear();
        recommendationsLabel.setText("Other similar companies...");
        List<CompanyDTO> suggestedCompanies = companyDescriptionDTO.getSuggestedCompanies();
        if(suggestedCompanies == null || suggestedCompanies.size() == 0) {
            MaterialLabel materialLabel = new MaterialLabel("No suggestions...");
            materialLabel.setPadding(20);
            recommendationsPanel.add(materialLabel);
        } else {
            MaterialRow materialRow = new MaterialRow();
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
        fullDescriptionPanel.setPaddingTop(30);
        fullDescriptionPanel.setPaddingBottom(20);
        HTML fullDescriptionHTML = new HTML(fullDescription);
        fullDescriptionHTML.getElement().getStyle().setProperty("minHeight", "6em");
        fullDescriptionPanel.add(fullDescriptionHTML);
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
                if (datasetAccess instanceof DatasetAccessFile) {
                    Window.open(DataAccessUtils.getDownloadUrl((DatasetAccessFile) datasetAccess), "_blank", null);
                } else if (datasetAccess instanceof DatasetAccessAPP) {
                    Window.open(datasetAccess.getUri(), "_blank", null);
                } else if (datasetAccess instanceof DatasetAccessOGC) {
                    if (freeAvailable) {
                        clientFactory.getPlaceController().goTo(new VisualisationPlace(
                                Utils.generateTokens(
                                        VisualisationPlace.TOKENS.productDatasetId.toString(), productDatasetDescriptionDTO.getId() + "",
                                        VisualisationPlace.TOKENS.dataAccessId.toString(), datasetAccess.getId() + ""
                                )));
                    } else {
                        // just open the service web page
                        Window.open(datasetAccess.getUri(), "_blank", null);
                    }
                } else if (datasetAccess instanceof DatasetAccessAPI) {
                    Window.open(datasetAccess.getUri(), "_blank", null);
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
                if (datasetAccess instanceof DatasetAccessFile) {
                    Window.open(DataAccessUtils.getDownloadUrl((DatasetAccessFile) datasetAccess), "_blank", null);
                } else if (datasetAccess instanceof DatasetAccessAPP) {
                    Window.open(datasetAccess.getUri(), "_blank", null);
                } else if (datasetAccess instanceof DatasetAccessOGC) {
                    if (freeAvailable) {
                        clientFactory.getPlaceController().goTo(new VisualisationPlace(
                                Utils.generateTokens(
                                        VisualisationPlace.TOKENS.productServiceId.toString(), productServiceDescriptionDTO.getId() + "",
                                        VisualisationPlace.TOKENS.dataAccessId.toString(), datasetAccess.getId() + ""
                                )));
                    }
                } else if (datasetAccess instanceof DatasetAccessAPI) {
                    Window.open(datasetAccess.getUri(), "_blank", null);
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

    private void addTab(MaterialTab materialTab, MaterialPanel tabPanel, String name, Panel panel, int size, String tabId) {
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

    private MaterialLabel createSubsection(String message) {
        MaterialLabel label = new MaterialLabel(message);
        label.addStyleName(style.offeringSubSection());
        return label;
    }

    private void addTitle(MaterialRow productRow, String message, String style, String moreUrl) {
        MaterialPanel materialPanel = new MaterialPanel();
        if(moreUrl != null) {
            MaterialLink moreLink = new MaterialLink("More");
            moreLink.setTextColor(Color.BLUE);
            moreLink.setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
            materialPanel.add(moreLink);
            moreLink.setHref(moreUrl);
        }
        MaterialLabel title = new MaterialLabel(message);
        title.setTextColor(Color.BLACK);
        materialPanel.add(title);
        materialPanel.addStyleName(style);
        productRow.add(materialPanel);
    }

    public void setMatchingServices(MaterialPanel container, String comment, List<ProductServiceDTO> productServices, String moreUrl) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        boolean hasMore = productServices != null && productServices.size() > 4;
        addTitle(productRow, comment, style.offeringSection(), hasMore ? moreUrl : null);
        if(productServices != null && productServices.size() > 0) {
            if(hasMore) {
                productServices = productServices.subList(0, 4);
            }
            for (ProductServiceDTO productServiceDTO : productServices) {
                MaterialColumn serviceColumn = new MaterialColumn(12, 6, 3);
                productRow.add(serviceColumn);
                serviceColumn.add(new ProductServiceWidget(productServiceDTO));
            }
        } else {
            MaterialLabel label = new MaterialLabel("No services found...");
            label.addStyleName(style.offeringSubSection());
            productRow.add(label);
        }

        container.add(createComment("If off the shelf products are not available or not suitable for your request, you may ask for a bespoke service. " +
                "When requesting a bespoke service, you will be asked to fill in a form with your requirements. " +
                "The service providers you selected will provide you with a proposal with their best offer."));
    }

    public void setMatchingDatasets(MaterialPanel container, String comment, List<ProductDatasetDTO> productDatasets, String moreUrl) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        boolean hasMore = productDatasets != null && productDatasets.size() > 4;
        addTitle(productRow, comment, style.offeringSection(), hasMore ? moreUrl : null);
        if(productDatasets != null && productDatasets.size() > 0) {
            if(hasMore) {
                productDatasets = productDatasets.subList(0, 4);
            }
            for (ProductDatasetDTO productDatasetDTO : productDatasets) {
                MaterialColumn serviceColumn = new MaterialColumn(12, 6, 3);
                productRow.add(serviceColumn);
                serviceColumn.add(new ProductDatasetWidget(productDatasetDTO));
            }
/*
            if(hasMore) {
                MaterialLoadingLink loadMore = new MaterialLoadingLink();
                loadMore.setText("Load more...");
                MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
                productRow.add(materialColumn);
                materialColumn.add(loadMore);
                loadMore.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {

                    }
                });
            }
*/
        } else {
            MaterialLabel label = new MaterialLabel("No off-the-shelf data found...");
            label.addStyleName(style.offeringSubSection());
            productRow.add(label);
        }

        container.add(createComment("Off the shelf products are products which have already been generated and can be downloaded or purchased. " +
                "If you would rather get a proposal from suppliers, please use a bespoke service instead."));
    }

    public void setMatchingSoftwares(MaterialPanel container, String comment, List<SoftwareDTO> softwares, String moreUrl) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        boolean hasMore = softwares != null && softwares.size() > 4;
        addTitle(productRow, comment, style.offeringSection(), hasMore ? moreUrl : null);
        if(softwares != null && softwares.size() > 0) {
            if(hasMore) {
                softwares = softwares.subList(0, 4);
            }
            for (SoftwareDTO softwareDTO : softwares) {
                MaterialColumn serviceColumn = new MaterialColumn(12, 6, 3);
                productRow.add(serviceColumn);
                serviceColumn.add(new SoftwareWidget(softwareDTO));
            }
        } else {
            MaterialLabel label = new MaterialLabel("No software solutions found...");
            label.addStyleName(style.offeringSubSection());
            productRow.add(label);
        }
    }

    public void setMatchingProjects(MaterialPanel container, String comment, List<ProjectDTO> projects, String moreUrl) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        boolean hasMore = projects != null && projects.size() > 4;
        addTitle(productRow, comment, style.offeringSection(), hasMore ? moreUrl : null);
        if(projects != null && projects.size() > 0) {
            if(hasMore) {
                projects = projects.subList(0, 4);
            }
            for (ProjectDTO projectDTO : projects) {
                MaterialColumn serviceColumn = new MaterialColumn(12, 6, 3);
                productRow.add(serviceColumn);
                serviceColumn.add(new ProjectWidget(projectDTO));
            }
        } else {
            MaterialLabel label = new MaterialLabel("No projects found...");
            label.addStyleName(style.offeringSubSection());
            productRow.add(label);
        }
    }

    public void setMatchingImagery(MaterialPanel container, String text) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "Search or request imagery for '" + text + "'", style.section(), null);
        MaterialColumn serviceColumn = new MaterialColumn(12, 6, 4);
        productRow.add(serviceColumn);
        serviceColumn.add(new ImageSearchWidget(text));
        serviceColumn = new MaterialColumn(12, 6, 4);
        productRow.add(serviceColumn);
        serviceColumn.add(new ImageRequestWidget(text));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}