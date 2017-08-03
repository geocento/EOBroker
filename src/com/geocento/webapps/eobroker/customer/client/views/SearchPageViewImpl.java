package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.CountryEditor;
import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLabelIcon;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.CSWBriefRecord;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.CSWGetRecordsResponse;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.FeedbackPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.widgets.*;
import com.geocento.webapps.eobroker.customer.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.addins.client.scrollfire.MaterialScrollfire;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.MaterialListValueBox;
import gwt.material.design.jquery.client.api.Functions;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.*;

/**
 * Created by thomas on 09/05/2016.
 */
public class SearchPageViewImpl extends Composite implements SearchPageView, ResizeHandler {

    interface SearchPageUiBinder extends UiBinder<Widget, SearchPageViewImpl> {
    }

    private static SearchPageUiBinder ourUiBinder = GWT.create(SearchPageUiBinder.class);

    static public interface Style extends CssResource {

        String productTitle();
        String productServicesTitle();
        String alternativesTitle();

        String option();

        String optionTitle();

        String subTitle();

        String subtext();

        String selected();
    }

    static HashMap<String, COMPANY_SIZE> companySize = new HashMap<String, COMPANY_SIZE>();
    static {
        companySize.put("Small", COMPANY_SIZE.small);
        companySize.put("Medium", COMPANY_SIZE.medium);
        companySize.put("Large", COMPANY_SIZE.large);
    }

    static HashMap<String, Integer> companyAge = new HashMap<String, Integer>();
    static {
        companyAge.put("No minimum", 0);
        companyAge.put("Min 2 years", 2);
        companyAge.put("Min 5 years", 5);
        companyAge.put("Min 10 years", 10);
        companyAge.put("Min 20 years", 20);
    }

    @UiField
    Style style;

    @UiField
    MaterialLink resultsTitle;
    @UiField
    MaterialCollapsible filtersContainer;
    @UiField
    HTMLPanel container;
    @UiField
    MapContainer mapContainer;
    @UiField
    MaterialCheckBox filterByAoI;
    @UiField
    LoadingWidget loading;
    @UiField
    HTMLPanel requirementsPanel;
    @UiField
    Anchor sendRequirements;
    @UiField
    MaterialRow filters;
    @UiField
    HTMLPanel timeFrame;
    @UiField
    HTMLPanel areaOfInterest;
    @UiField
    MaterialDatePicker start;
    @UiField
    MaterialDatePicker stop;
    @UiField
    MaterialCheckBox filterByTimeFrame;
    // possible filters
    // for products and product based offerings
    @UiField
    MaterialListValueBox<Sector> sectorFilter;
    @UiField
    MaterialListValueBox<Thematic> thematicFilter;
    // for companies
    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.material.MaterialListValueBox<COMPANY_SIZE> companySizeFilter;
    @UiField
    MaterialListValueBox<Integer> companyAgeFilter;
    @UiField
    CountryEditor companyCountryFilter;
    // for product datasets
    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.material.MaterialListValueBox<ServiceType> productCommercialFilter;
    // for software
    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.material.MaterialListValueBox<SoftwareType> softwareCommercialFilter;
    @UiField
    MaterialLink filterTitle;
    @UiField
    CategorySearchBox companyFilter;
    @UiField
    CategorySearchBox productFilter;
    @UiField
    MaterialLink showFilters;
    @UiField
    MaterialCollapsibleItem filtersPanel;

    private ProductDTO productDTO;
    private CompanyDTO companyDTO;

    private Presenter presenter;

    private ClientFactoryImpl clientFactory;

    public SearchPageViewImpl(ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        // hide the loading results widget
        hideLoadingResults();

        sendRequirements.setHref("#" +
                PlaceHistoryHelper.convertPlace(
                        new FeedbackPlace(FeedbackPlace.TOKENS.topic.toString() + "=Feedback on search")));
        // hide the send requirements panel
        displaySendRequirements(false);

        // TODO - move to activity or to a generic map container for customer
        // save AoI
        mapContainer.setPresenter(new MapContainer.Presenter() {
            @Override
            public void aoiChanged(AoIDTO aoi) {
                presenter.aoiChanged(aoi);
            }
        });

        // set filter values
        // sort by name with all first in the list
        {
            List<Sector> sortedValues = ListUtil.toList(Sector.values());
            Collections.sort(sortedValues, (o1, o2) -> o1 == Sector.all ? -1 :
                    o1.getName().compareTo(o2.getName()));
            for (Sector sector : sortedValues) {
                sectorFilter.addItem(sector, sector.getName());
            }
            sectorFilter.addValueChangeHandler(event -> presenter.filtersChanged());
        }
        {
            List<Thematic> sortedValues = ListUtil.toList(Thematic.values());
            Collections.sort(sortedValues, (o1, o2) -> o1 == Thematic.all ? -1 :
                    o1.getName().compareTo(o2.getName()));
            for (Thematic thematic : sortedValues) {
                thematicFilter.addItem(thematic, thematic.getName());
            }
            thematicFilter.addValueChangeHandler(event -> presenter.filtersChanged());
        }
        // company filters
        {
            companySizeFilter.addNullItem("All");
            for (String name : companySize.keySet()) {
                companySizeFilter.addTypedItem(companySize.get(name), name);
            }
            companySizeFilter.addValueChangeHandler(event -> {presenter.filtersChanged();});
        }
        {
            for (String name : companyAge.keySet()) {
                companyAgeFilter.addItem(companyAge.get(name), name);
            }
            companyAgeFilter.addValueChangeHandler(event -> {
                presenter.filtersChanged();
            });
        }
        {
            companyCountryFilter.insertItem("", "All", 0);
            companyCountryFilter.setSelectedIndex(0);
            companyCountryFilter.addValueChangeHandler(event -> {presenter.filtersChanged();});
        }
        // product datasets filters
        productCommercialFilter.addNullItem("All");
        for (ServiceType name : ServiceType.values()) {
            productCommercialFilter.addTypedItem(name, name.getName());
        }
        productCommercialFilter.addClickHandler(event -> presenter.filtersChanged());
        // software filters
        softwareCommercialFilter.addNullItem("All");
        for (SoftwareType name : SoftwareType.values()) {
            softwareCommercialFilter.addTypedItem(name, name.getName());
        }
        softwareCommercialFilter.addValueChangeHandler(event -> {presenter.filtersChanged();});

        // add handlers on filters
        filterByAoI.addValueChangeHandler(event -> presenter.filtersChanged());
        filterByTimeFrame.addValueChangeHandler(event -> presenter.filtersChanged());
        start.addValueChangeHandler(event -> {if(filterByTimeFrame.getValue()) presenter.filtersChanged();});
        stop.addValueChangeHandler(event -> {if(filterByTimeFrame.getValue()) presenter.filtersChanged();});
        productFilter.setPresenter(suggestion -> {
            boolean changed = false;
            if(suggestion == null) {
                changed = productDTO != null;
                productDTO = null;
            } else {
                // create shallow product
                ProductDTO productDTO = new ProductDTO();
                productDTO.setName(suggestion.getName());
                productDTO.setId(Long.parseLong(suggestion.getUri().split("::")[1]));
                changed = SearchPageViewImpl.this.productDTO == null || !SearchPageViewImpl.this.productDTO.getId().equals(productDTO.getId());
                SearchPageViewImpl.this.productDTO = productDTO;
            }
            if(changed) {
                presenter.filtersChanged();
            }
        });
        companyFilter.setPresenter(suggestion -> {
            // create shallow company
            boolean changed = false;
            if(suggestion == null) {
                changed = companyDTO != null;
                companyDTO = null;
            } else {
                CompanyDTO companyDTO = new CompanyDTO();
                companyDTO.setName(suggestion.getName());
                companyDTO.setId(Long.parseLong(suggestion.getUri().split("::")[1]));
                changed = SearchPageViewImpl.this.companyDTO == null || !SearchPageViewImpl.this.companyDTO.getId().equals(companyDTO.getId());
                SearchPageViewImpl.this.companyDTO = companyDTO;
            }
            // make sure values have changed
            if(changed) {
                presenter.filtersChanged();
            }
        });

        // TODO - not the right method to detect a change in active maybe replace by another widget
        filtersContainer.addClearActiveHandler(event -> updateShowFilter());
        updateShowFilter();

        onResize(null);
    }

    private void updateShowFilter() {
        boolean isActive = filtersPanel.isActive();
        showFilters.setText(isActive ? "Hide filters" : "Show filters");
        showFilters.setIconType(isActive ? IconType.ARROW_DROP_UP : IconType.ARROW_DROP_DOWN);
    }

    private void addCategoryTooltip(MaterialLink materialLink, String message) {
/*
        MaterialTooltip materialTooltip = new MaterialTooltip();
        materialTooltip.setWidget(materialLink);
        materialTooltip.setPosition(Position.RIGHT);
        materialTooltip.setText(message);
        categories.add(materialTooltip);
*/
    }

    private void displaySendRequirements(boolean display) {
        requirementsPanel.setVisible(display);
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        mapContainer.setMapLoadedHandler(mapLoadedHandler);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setResultsTitle(String message) {
        resultsTitle.setText(message);
        resultsTitle.setVisible(message != null && message.length() > 0);
    }

    @Override
    public void displayAoI(AoIDTO aoi) {
        mapContainer.displayAoI(aoi);
    }

    @Override
    public void displayLoadingResults(String message) {
        loading.setVisible(true);
        loading.setText(message);
    }

    @Override
    public void hideLoadingResults() {
        loading.setVisible(false);
    }

    @Override
    public void clearResults() {
        container.clear();
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
/*
        HTMLPanel htmlPanel = new HTMLPanel("<span class='flow-text'>" + message + "</span>");
        htmlPanel.addStyleName(style);
        MaterialColumn titleMaterialColumn = new MaterialColumn(12, 12, 12);
        productRow.add(titleMaterialColumn);
        titleMaterialColumn.add(htmlPanel);
*/
    }

    @Override
    public void setMatchingProducts(List<ProductDTO> productDTOs, String moreUrl) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "Products", style.productTitle(), moreUrl);
        if(productDTOs != null && productDTOs.size() > 0) {
            for (ProductDTO productDTO : productDTOs) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                materialColumn.add(new ProductWidget(productDTO));
                productRow.add(materialColumn);
            }
        } else {
            MaterialLabel label = new MaterialLabel("No matching products found...");
            label.addStyleName(style.subtext());
            productRow.add(label);
        }
    }

    @Override
    public void setMatchingServices(List<ProductServiceDTO> productServices, String moreUrl) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "Bespoke services", style.productServicesTitle(), moreUrl);
        if(productServices != null && productServices.size() > 0) {
            for (ProductServiceDTO productServiceDTO : productServices) {
                MaterialColumn serviceColumn = new MaterialColumn(12, 6, 3);
                productRow.add(serviceColumn);
                serviceColumn.add(new ProductServiceWidget(productServiceDTO));
            }
        } else {
            MaterialLabel label = new MaterialLabel("No services found...");
            label.addStyleName(style.subtext());
            productRow.add(label);
        }
    }

    @Override
    public void setMatchingDatasets(List<ProductDatasetDTO> productDatasets, String moreUrl) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "Off-the-shelf data", style.productServicesTitle(), moreUrl);
        if(productDatasets != null && productDatasets.size() > 0) {
            for (ProductDatasetDTO productDatasetDTO : productDatasets) {
                MaterialColumn serviceColumn = new MaterialColumn(12, 6, 3);
                productRow.add(serviceColumn);
                serviceColumn.add(new ProductDatasetWidget(productDatasetDTO));
            }
        } else {
            MaterialLabel label = new MaterialLabel("No off-the-shelf data found...");
            label.addStyleName(style.subtext());
            productRow.add(label);
        }
    }

    @Override
    public void setMatchingSoftwares(List<SoftwareDTO> softwares, String moreUrl) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "Software solutions", style.productServicesTitle(), moreUrl);
        if(softwares != null && softwares.size() > 0) {
            for (SoftwareDTO softwareDTO : softwares) {
                MaterialColumn serviceColumn = new MaterialColumn(12, 6, 3);
                productRow.add(serviceColumn);
                serviceColumn.add(new SoftwareWidget(softwareDTO));
            }
        } else {
            MaterialLabel label = new MaterialLabel("No software solutions found...");
            label.addStyleName(style.subtext());
            productRow.add(label);
        }
    }

    @Override
    public void setMatchingProjects(List<ProjectDTO> projects, String moreUrl) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        boolean more = projects != null && projects.size() > 4;
        addTitle(productRow, "Projects", style.productServicesTitle(), moreUrl);
        if(projects != null && projects.size() > 0) {
            if(more) {
                projects = projects.subList(0, 4);
            }
            for (ProjectDTO projectDTO : projects) {
                MaterialColumn serviceColumn = new MaterialColumn(12, 6, 3);
                productRow.add(serviceColumn);
                serviceColumn.add(new ProjectWidget(projectDTO));
            }
        } else {
            MaterialLabel label = new MaterialLabel("No projects found...");
            label.addStyleName(style.subtext());
            productRow.add(label);
        }
    }

    @Override
    public void setMatchingImagery(String text) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "Search or request imagery for '" + text + "'", style.productServicesTitle(), null);
        MaterialColumn serviceColumn = new MaterialColumn(12, 6, 4);
        productRow.add(serviceColumn);
        serviceColumn.add(new ImageSearchWidget(text));
        serviceColumn = new MaterialColumn(12, 6, 4);
        productRow.add(serviceColumn);
        serviceColumn.add(new ImageRequestWidget(text));
    }

    @Override
    public void setDatasetProviders(List<DatasetProviderDTO> datasetProviderDTOs, final String text, AoIDTO aoi) {
        MaterialRow datasetsRow = new MaterialRow();
        container.add(datasetsRow);
        boolean more = datasetProviderDTOs != null && datasetProviderDTOs.size() > 4;
        addTitle(datasetsRow, "Matching datasets", style.productServicesTitle(), null);
        for(final DatasetProviderDTO datasetProviderDTO : datasetProviderDTOs) {
            if(more) {
                datasetProviderDTOs = datasetProviderDTOs.subList(0, 4);
            }
            final MaterialRow datasetRow = new MaterialRow();
            container.add(datasetRow);
            MaterialLabelIcon labelIcon = new MaterialLabelIcon(datasetProviderDTO.getIconURL(), datasetProviderDTO.getName());
            labelIcon.setImageHeight("35px");
            labelIcon.addStyleName(style.subTitle());
            datasetRow.add(labelIcon);
            final LoadingWidget loadingWidget = new LoadingWidget("Loading datasets...");
            loadingWidget.addStyleName(style.subTitle());
            datasetRow.add(loadingWidget);
            // get the protocol part
            String uri = datasetProviderDTO.getUri();
            String protocol = uri.substring(0, uri.indexOf(":")).toLowerCase();
            uri = uri.substring(uri.indexOf(":") + 1);
            switch(protocol) {
                case "csw":
                    try {
                        CSWGetRecordsRequestDTO request = new CSWGetRecordsRequestDTO(uri, text, AoIUtil.getExtent(aoi));
                        REST.withCallback(new MethodCallback<CSWGetRecordsResponse>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                datasetRow.remove(loadingWidget);
                                MaterialLabel label = new MaterialLabel("Failed to load datasets for " + datasetProviderDTO.getName());
                                label.addStyleName(style.subtext());
                                datasetRow.add(label);
                            }

                            @Override
                            public void onSuccess(Method method, CSWGetRecordsResponse response) {
                                datasetRow.remove(loadingWidget);
                                if(response.getRecords().size() > 0) {
                                    MaterialLabel label = new MaterialLabel("Found " + response.getNumberOfRecordsMatched() + " relevant datasets");
                                    label.addStyleName(style.subtext());
                                    datasetRow.add(label);
                                    for (CSWBriefRecord cswBriefRecord : response.getRecords()) {
                                        MaterialColumn datasetColumn = new MaterialColumn(6, 4, 3);
                                        datasetRow.add(datasetColumn);
                                        datasetColumn.add(new DatasetWidget(cswBriefRecord));
                                    }
                                    if (response.getNextRecord() != 0) {
                                        MaterialLink viewMore = new MaterialLink("View more...");
                                        viewMore.addStyleName(style.subtext());
                                        datasetRow.add(viewMore);
                                    }
                                } else {
                                    MaterialLabel label = new MaterialLabel("No datasets found");
                                    label.addStyleName(style.subtext());
                                    datasetRow.add(label);
                                }
                            }
                        }).call(ServicesUtil.searchService).getRecordsResponse(request);
                    } catch (RequestException e) {
                    }
                    break;
            }
        }
    }

    @Override
    public void displayFilters(Category category) {
        filters.clear();
        if(category == null) {
        } else {
            switch (category) {
                case products:
                    displayProductFilters();
                    break;
                case productservices:
                    displayProductServicesFilters();
                    break;
                case productdatasets:
                    displayProductDatasetsFilters();
                    break;
                case software:
                    displaySoftwareFilters();
                    break;
                case project:
                    displayProjectFilters();
                    break;
                case companies:
                    displayCompaniesFilters();
                    break;
            }
        }
    }

    @Override
    public void addProducts(List<ProductDTO> products, int start, boolean hasMore, String text) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        if(products == null || products.size() == 0) {
            if(start == 0) {
                MaterialLabel label = new MaterialLabel("No products found for your request...");
                label.addStyleName(style.subtext());
                productRow.add(label);
            }
        } else {
            for (ProductDTO productDTO : products) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                productRow.add(materialColumn);
                materialColumn.add(new ProductWidget(productDTO));
            }
            if(hasMore) {
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Functions.Func() {
                    @Override
                    public void call() {
                        presenter.loadMoreProducts();
                    }
                });
            }
        }
        displaySendRequirements(!hasMore);
    }

    @Override
    public void addProductServices(List<ProductServiceDTO> productServiceDTOs, int start, boolean hasMore, String text) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        if(productServiceDTOs == null || productServiceDTOs.size() == 0) {
            if(start == 0) {
                MaterialLabel label = new MaterialLabel("No bespoke services found for your request...");
                label.addStyleName(style.subtext());
                productRow.add(label);
            }
        } else {
            for(ProductServiceDTO productServiceDTO : productServiceDTOs) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                productRow.add(materialColumn);
                materialColumn.add(new ProductServiceWidget(productServiceDTO));
            }
            if(hasMore) {
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Functions.Func() {
                        @Override
                        public void call() {
                            presenter.loadMoreProductServices();
                        }
                });
            }
        }
        displaySendRequirements(!hasMore);
    }

    @Override
    public void addProductDatasets(List<ProductDatasetDTO> productDatasetDTOs, int start, boolean hasMore, String text) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        if(productDatasetDTOs == null || productDatasetDTOs.size() == 0) {
            if(start == 0) {
                MaterialLabel label = new MaterialLabel("No off-the-shelf data found for your request...");
                label.addStyleName(style.subtext());
                productRow.add(label);
            }
        } else {
            for(ProductDatasetDTO productDatasetDTO : productDatasetDTOs) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                productRow.add(materialColumn);
                materialColumn.add(new ProductDatasetWidget(productDatasetDTO));
            }
            if(hasMore) {
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Functions.Func() {
                    @Override
                    public void call() {
                        presenter.loadMoreProductDatasets();
                    }
                });
            }
        }
        displaySendRequirements(!hasMore);
    }

    @Override
    public void addSoftware(List<SoftwareDTO> softwareDTOs, int start, boolean hasMore, String text) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        if(softwareDTOs == null || softwareDTOs.size() == 0) {
            if(start == 0) {
                MaterialLabel label = new MaterialLabel("No software found for your request...");
                label.addStyleName(style.subtext());
                productRow.add(label);
            }
        } else {
            for(SoftwareDTO softwareDTO : softwareDTOs) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                productRow.add(materialColumn);
                materialColumn.add(new SoftwareWidget(softwareDTO));
            }
            if(hasMore) {
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Functions.Func() {
                    @Override
                    public void call() {
                        presenter.loadMoreSofware();
                    }
                });
            }
        }
        displaySendRequirements(!hasMore);
    }

    @Override
    public void addProjects(List<ProjectDTO> projectDTOs, int start, boolean hasMore, String text) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        if(projectDTOs == null || projectDTOs.size() == 0) {
            if(start == 0) {
                MaterialLabel label = new MaterialLabel("No project found for your request...");
                label.addStyleName(style.subtext());
                productRow.add(label);
            }
        } else {
            for(ProjectDTO projectDTO : projectDTOs) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                productRow.add(materialColumn);
                materialColumn.add(new ProjectWidget(projectDTO));
            }
            if(hasMore) {
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Functions.Func() {
                    @Override
                    public void call() {
                        presenter.loadMoreProjects();
                    }
                });
            }
        }
        displaySendRequirements(!hasMore);
    }

    @Override
    public void addCompanies(List<CompanyDTO> companyDTOs, int start, boolean hasMore, String text) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        if(companyDTOs == null || companyDTOs.size() == 0) {
            if(start == 0) {
                MaterialLabel label = new MaterialLabel("No companies found for your request...");
                label.addStyleName(style.subtext());
                productRow.add(label);
            }
        } else {
            for(CompanyDTO companyDTO : companyDTOs) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                productRow.add(materialColumn);
                materialColumn.add(new CompanyWidget(companyDTO));
            }
            if(hasMore) {
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Functions.Func() {
                    @Override
                    public void call() {
                        presenter.loadMoreProducts();
                    }
                });
            }
        }
        displaySendRequirements(!hasMore);
    }

    private void addFilter(Widget widget, String gridValue) {
        MaterialColumn materialColumn = new MaterialColumn();
        materialColumn.setGrid(gridValue);
        materialColumn.add(widget);
        filters.add(materialColumn);
    }

    private void displayProductFilters() {
        // add sector selection
        addFilter(sectorFilter, "s12 m6 l4");
        // add thematic selection
        addFilter(thematicFilter, "s12 m6 l4");
    }

    private void displayProductDatasetsFilters() {
        addFilter(areaOfInterest, "s12 m12 l6");
        MaterialPanel materialPanel = new MaterialPanel();
        addFilter(materialPanel, "s12 m12 l6");
        materialPanel.add(timeFrame);
        materialPanel.add(productCommercialFilter);
        materialPanel.add(productFilter);
        materialPanel.add(companyFilter);
    }

    private void displayProductServicesFilters() {
        addFilter(areaOfInterest, "s12 m12 l6");
        MaterialPanel materialPanel = new MaterialPanel();
        addFilter(materialPanel, "s12 m12 l6");
        //materialPanel.add(timeFrame);
        materialPanel.add(productFilter);
        materialPanel.add(companyFilter);
    }

    private void displaySoftwareFilters() {
        addFilter(softwareCommercialFilter, "s12 m6 l4");
    }

    private void displayProjectFilters() {
        addFilter(timeFrame, "s12 m6 l4");
    }

    private void displayCompaniesFilters() {
        // add company size filter
        addFilter(companySizeFilter, "s12 m6 l4");
        // add years in office filter
        addFilter(companyAgeFilter, "s12 m6 l4");
        // add country filter
        addFilter(companyCountryFilter, "s12 m6 l4");
    }

    @Override
    public HasValue<Boolean> getFilterByAoI() {
        return filterByAoI;
    }

    @Override
    public ServiceType getProductServiceType() {
        return productCommercialFilter.getTypedValue(ServiceType.class);
    }

    @Override
    public HasValue<Boolean> getTimeFrameFilterActivated() {
        return filterByTimeFrame;
    }

    @Override
    public HasValue<Date> getStartTimeFrameFilter() {
        return start;
    }

    @Override
    public HasValue<Date> getStopTimeFrameFilter() {
        return stop;
    }

    @Override
    public Sector getSectorFilter() {
        return sectorFilter.getValue();
    }

    @Override
    public Thematic getThematicFilter() {
        return thematicFilter.getValue();
    }

    @Override
    public void centerOnAoI() {
        mapContainer.centerOnAoI();
    }

    @Override
    public COMPANY_SIZE getCompanySizeFilter() {
        return companySizeFilter.getTypedValue(COMPANY_SIZE.class);
    }

    @Override
    public int getCompanyAgeFilter() {
        return companyAgeFilter.getSelectedValue();
    }

    @Override
    public String getCompanyCountryFilter() {
        return companyCountryFilter.getCountry().length() == 0 ? null : companyCountryFilter.getCountry();
    }

    @Override
    public SoftwareType getSoftwareType() {
        return softwareCommercialFilter.getTypedValue(SoftwareType.class);
    }

    @Override
    public void showFilters(boolean display) {
        filtersContainer.setVisible(display);
    }

    @Override
    public void setFilterTitle(String filterText) {
        filterTitle.setText(filterText);
    }

    @Override
    public ProductDTO getProductSelection() {
        return productDTO;
    }

    @Override
    public void setProductSelection(ProductDTO productDTO) {
        this.productDTO = productDTO;
        productFilter.setValue(productDTO == null ? "" : productDTO.getName());
    }

    @Override
    public CompanyDTO getCompanySelection() {
        return companyDTO;
    }

    @Override
    public void setCompanySelection(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
        companyFilter.setValue(companyDTO == null ? "" : companyDTO.getName());
    }

    @Override
    public void enableAoiFilter(boolean enable) {
        filterByAoI.setValue(enable);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void onResize(ResizeEvent event) {
    }

}