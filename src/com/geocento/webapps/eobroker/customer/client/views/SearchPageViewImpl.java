package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLabelIcon;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.Sector;
import com.geocento.webapps.eobroker.common.shared.entities.Thematic;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.CSWBriefRecord;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.CSWGetRecordsResponse;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.widgets.*;
import com.geocento.webapps.eobroker.customer.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.scrollfire.MaterialScrollfire;
import gwt.material.design.client.base.HasHref;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.html.Option;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

        String navOpened();

        String option();

        String optionTitle();

        String subTitle();

        String subtext();

        String selected();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialLink resultsTitle;
    @UiField
    HTMLPanel categories;
    @UiField
    MaterialSideNav filtersPanel;
    @UiField
    HTMLPanel container;
    @UiField
    MapContainer mapContainer;
    @UiField
    HTMLPanel settings;
    @UiField
    MaterialCheckBox filterByAoI;
    @UiField
    MaterialLink productsCategory;
    @UiField
    MaterialLink productServicesCategory;
    @UiField
    MaterialLink productDatasetsCategory;
    @UiField
    MaterialLink softwareCategory;
    @UiField
    MaterialLink projectsCategory;
    @UiField
    LoadingWidget loading;
    @UiField
    HTMLPanel requirementsPanel;

    // possible filters
    private MaterialListBox sectorSelection;
    private MaterialListBox thematicSelection;

    private Presenter presenter;

    public SearchPageViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        filtersPanel.show();

        // update icons
        productsCategory.setIconType(CategoryUtils.getIconType(Category.products));
        productServicesCategory.setIconType(CategoryUtils.getIconType(Category.productservices));
        productDatasetsCategory.setIconType(CategoryUtils.getIconType(Category.productdatasets));
        softwareCategory.setIconType(CategoryUtils.getIconType(Category.software));
        projectsCategory.setIconType(CategoryUtils.getIconType(Category.project));

        // hide the loading results widget
        hideLoadingResults();

        // hide the send requirements panel
        displaySendRequirements(false);

        // TODO - move to activity or to a generic map container for customer
        // save AoI
        mapContainer.setPresenter(new MapContainer.Presenter() {
            @Override
            public void aoiChanged(AoIDTO aoi) {
                presenter.aoiChanged(aoi);
            }

            @Override
            public void aoiSelected(AoIDTO aoi) {
                presenter.aoiSelected(aoi);
            }
        });

        // add handlers on filters
        filterByAoI.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                presenter.filtersChanged();
            }
        });

        onResize(null);
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
    public void setSearchText(String search) {
        template.setSearchText(search);
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
        template.displayLoading();
        loading.setVisible(true);
        loading.setText(message);
    }

    @Override
    public void hideLoadingResults() {
        template.hideLoading();
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
            moreLink.setTextColor("blue");
            moreLink.setFloat(com.google.gwt.dom.client.Style.Float.RIGHT);
            materialPanel.add(moreLink);
            moreLink.setHref(moreUrl);
        }
        MaterialLabel title = new MaterialLabel(message);
        title.setTextColor("black");
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
    public void setTitleText(String title) {
        template.setTitleText(title);
    }

    @Override
    public void setMatchingProducts(List<ProductDTO> suggestedProducts, String moreUrl) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "Products", style.productTitle(), moreUrl);
        if(suggestedProducts != null && suggestedProducts.size() > 0) {
            for (ProductDTO productDTO : suggestedProducts) {
                MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
                materialColumn.add(new ProductWidget(productDTO));
                productRow.add(materialColumn);
            }
        } else {
            MaterialLabel label = new MaterialLabel("No suitable products found...");
            label.addStyleName(style.subtext());
            productRow.add(label);
        }
    }

    @Override
    public void setMatchingServices(List<ProductServiceDTO> productServices, String moreUrl) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "On-demand services", style.productServicesTitle(), moreUrl);
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
    public TemplateView getTemplateView() {
        return template;
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
    public HasHref getProductsCategory() {
        return productsCategory;
    }

    @Override
    public HasHref getProductServicesCategory() {
        return productServicesCategory;
    }

    @Override
    public HasHref getProductDatasetsCategory() {
        return productDatasetsCategory;
    }

    @Override
    public HasHref getSoftwareCategory() {
        return softwareCategory;
    }

    @Override
    public HasHref getProjectsCategory() {
        return projectsCategory;
    }

    @Override
    public void displayCategories(boolean display) {
        categories.setVisible(true);
    }

    @Override
    public void selectCategory(Category category) {
        for(Widget widget : categories) {
            widget.removeStyleName(style.selected());
        }
        if(category == null) {
            return;
        }
        switch (category) {
            case products:
                productsCategory.addStyleName(style.selected());
                break;
            case productservices:
                productServicesCategory.addStyleName(style.selected());
                break;
            case productdatasets:
                productDatasetsCategory.addStyleName(style.selected());
                break;
            case software:
                softwareCategory.addStyleName(style.selected());
                break;
            case project:
                projectsCategory.addStyleName(style.selected());
                break;
        }
    }

    @Override
    public void displayFilters(Category category) {
        settings.clear();
        if(category == null) {
        } else {
            switch (category) {
                case products:
                    displayProductFilters();
                    break;
                case productservices:
                    break;
                case productdatasets:
                    break;
                case software:
                    break;
                case project:
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
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Runnable() {
                    @Override
                    public void run() {
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
                MaterialLabel label = new MaterialLabel("No on-demand services found for your request...");
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
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Runnable() {
                    @Override
                    public void run() {
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
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Runnable() {
                    @Override
                    public void run() {
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
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Runnable() {
                    @Override
                    public void run() {
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
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Runnable() {
                    @Override
                    public void run() {
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
                MaterialScrollfire.apply(productRow.getWidget(productRow.getWidgetCount() - 1).getElement(), new Runnable() {
                    @Override
                    public void run() {
                        presenter.loadMoreProducts();
                    }
                });
            }
        }
        displaySendRequirements(!hasMore);
    }

    private void displayProductFilters() {
        settings.clear();
        // add sector selection
        {
            MaterialLabel materialLabel = new MaterialLabel("Sector");
            materialLabel.addStyleName(style.optionTitle());
            settings.add(materialLabel);
            sectorSelection = new MaterialListBox();
            sectorSelection.addStyleName(style.option());
            List<Sector> sortedValues = ListUtil.toList(Sector.values());
            Collections.sort(sortedValues, new Comparator<Sector>() {
                @Override
                public int compare(Sector o1, Sector o2) {
                    return o1 == Sector.all ? -1 :
                            o1.getName().compareTo(o2.getName());
                }
            });
            for (Sector option : sortedValues) {
                Option optionWidget = new Option();
                optionWidget.setText(option.getName());
                optionWidget.setValue(option.toString());
                sectorSelection.add(optionWidget);
            }
            sectorSelection.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    presenter.filtersChanged();
                }
            });
            settings.add(sectorSelection);
        }
        // add thematic selection
        {
            MaterialLabel materialLabel = new MaterialLabel("Thematic");
            materialLabel.addStyleName(style.optionTitle());
            settings.add(materialLabel);
            thematicSelection = new MaterialListBox();
            thematicSelection.addStyleName(style.option());
            List<Thematic> sortedValues = ListUtil.toList(Thematic.values());
            Collections.sort(sortedValues, new Comparator<Thematic>() {
                @Override
                public int compare(Thematic o1, Thematic o2) {
                    return o1 == Thematic.all ? -1 :
                            o1.getName().compareTo(o2.getName());
                }
            });
            for (Thematic option : sortedValues) {
                Option optionWidget = new Option();
                optionWidget.setText(option.getName());
                optionWidget.setValue(option.toString());
                thematicSelection.add(optionWidget);
            }
            thematicSelection.addValueChangeHandler(new ValueChangeHandler<String>() {
                @Override
                public void onValueChange(ValueChangeEvent<String> event) {
                    presenter.filtersChanged();
                }
            });
            settings.add(thematicSelection);
        }
    }

    private void displayCompaniesFilters() {
        settings.clear();
        MaterialLabel materialLabel = new MaterialLabel("Company size");
        materialLabel.addStyleName(style.optionTitle());
        settings.add(materialLabel);
        for(String option : new String[] {"Large corporation > 4000", "Large < 4000", "Medium-sized < 250", "Small < 50", "Micro < 10"}) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox();
            materialCheckBox.setText(option);
            materialCheckBox.setObject(option);
            materialCheckBox.addStyleName(style.option());
            settings.add(materialCheckBox);
            materialCheckBox.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // TODO - update display
                }
            });
        }
        materialLabel = new MaterialLabel("Company certifications");
        materialLabel.addStyleName(style.optionTitle());
        settings.add(materialLabel);
        for(String option : new String[] {"Certification 1", "Certification 2", "Certification 3"}) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox();
            materialCheckBox.setText(option);
            materialCheckBox.setObject(option);
            materialCheckBox.addStyleName(style.option());
            settings.add(materialCheckBox);
            materialCheckBox.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // TODO - update display
                }
            });
        }
    }

    @Override
    public HasValue<Boolean> getFilterByAoI() {
        return filterByAoI;
    }

    @Override
    public Sector getSectorFilter() {
        return sectorSelection.getSelectedValue() == null ? null : Sector.valueOf(sectorSelection.getSelectedValue());
    }

    @Override
    public Thematic getThematicFilter() {
        return thematicSelection.getSelectedValue() == null ? null : Thematic.valueOf(thematicSelection.getSelectedValue());
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void onResize(ResizeEvent event) {
        template.setPanelStyleName(style.navOpened(), filtersPanel.isVisible());
    }

}