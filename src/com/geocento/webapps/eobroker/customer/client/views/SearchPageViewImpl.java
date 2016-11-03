package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialLabelIcon;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.Sector;
import com.geocento.webapps.eobroker.common.shared.entities.Thematic;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.CSWBriefRecord;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.CSWGetRecordsResponse;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.widgets.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

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
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialLink currentSearch;
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

    private Presenter presenter;

    public SearchPageViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        // add categories controls
        for(Category category : Category.values()) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox();
            materialCheckBox.setText(category.getName());
            materialCheckBox.setObject(category);
            materialCheckBox.addStyleName(style.option());
            categories.add(materialCheckBox);
            materialCheckBox.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // TODO - update display
                }
            });
        }

        filtersPanel.show();

        onResize(null);
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
    public void setCurrentSearch(String search) {
        template.setSearchText(search);
    }

    @Override
    public void setSearchResults(String message) {
        currentSearch.setText(message);
    }

    @Override
    public void displayAoI(AoI aoi) {
        mapContainer.displayAoI(aoi);
    }

    @Override
    public void displayLoadingResults(String message) {
        template.displayLoading();
    }

    @Override
    public void hideLoadingResults() {
        template.hideLoading();
    }

    @Override
    public void clearResults() {
        container.clear();
    }

    @Override
    public void setProductSelection(ProductDTO productDTO, List<ProductServiceDTO> services, List<ProductDTO> others) {
        clearResults();
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "Selected product", style.productTitle());
        MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
        productRow.add(materialColumn);
        materialColumn.add(new ProductWidget(productDTO));
        addTitle(productRow, "EO Broker services offering this product", style.productServicesTitle());
        for(ProductServiceDTO productServiceDTO : services) {
            MaterialColumn serviceColumn = new MaterialColumn(12, 6, 3);
            productRow.add(serviceColumn);
            serviceColumn.add(new ProductServiceWidget(productServiceDTO));
        }
    }

    private void addTitle(MaterialRow productRow, String message, String style) {
        HTMLPanel htmlPanel = new HTMLPanel("<span class='flow-text'>" + message + "</span>");
        htmlPanel.addStyleName(style);
        MaterialColumn titleMaterialColumn = new MaterialColumn(12, 12, 12);
        productRow.add(titleMaterialColumn);
        titleMaterialColumn.add(htmlPanel);
    }

    @Override
    public void setCategories(List<Category> categories) {
        for(Widget widget : this.categories) {
            if(widget instanceof MaterialCheckBox) {
                MaterialCheckBox checkBox = ((MaterialCheckBox) widget);
                checkBox.setValue(categories.contains((Category) checkBox.getObject()));
            }
        }
    }

    @Override
    public HasClickHandlers getChangeSearch() {
        return currentSearch;
    }

    @Override
    public void setTitleText(String title) {
        template.setTitleText(title);
    }

    @Override
    public void setMatchingProducts(List<ProductDTO> suggestedProducts) {
        categories.setVisible(true);
        settings.setVisible(false);
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, suggestedProducts == null || suggestedProducts.size() == 0 ? "No product matching your request" :
                                        "Products matching your request",
                style.productTitle());
        for(ProductDTO productDTO : suggestedProducts) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
            materialColumn.add(new ProductWidget(productDTO));
            productRow.add(materialColumn);
        }
    }

    @Override
    public void setMatchingServices(List<ProductServiceDTO> productServices) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "EO Broker services matching your request", style.productServicesTitle());
        if(productServices.size() > 0) {
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
    public void displayProductsList(List<ProductDTO> products, int start, int limit, String text) {
        categories.setVisible(false);
        settings.setVisible(true);
        settings.clear();
        MaterialLabel materialLabel = new MaterialLabel("Sector");
        materialLabel.addStyleName(style.optionTitle());
        settings.add(materialLabel);
        MaterialListBox sectorSelection = new MaterialListBox();
        sectorSelection.addStyleName(style.option());
        sectorSelection.addItem("All");
        for(Sector option : Sector.values()) {
            sectorSelection.addItem(option.toString());
        }
        settings.add(sectorSelection);
        materialLabel = new MaterialLabel("Thematic");
        materialLabel.addStyleName(style.optionTitle());
        settings.add(materialLabel);
        for(Thematic option : Thematic.values()) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox();
            materialCheckBox.setText(option.toString());
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
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        for(ProductDTO productDTO : products) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
            productRow.add(materialColumn);
            materialColumn.add(new ProductWidget(productDTO));
        }
    }

    @Override
    public void displayOffer(List<Offer> offers, int start, int limit, String text) {
        categories.setVisible(false);
        settings.setVisible(true);
        settings.clear();
        MaterialLabel materialLabel = new MaterialLabel("Sector");
        materialLabel.addStyleName(style.optionTitle());
        settings.add(materialLabel);
        MaterialListBox sectorSelection = new MaterialListBox();
        sectorSelection.addStyleName(style.option());
        sectorSelection.addItem("All");
        for(Sector option : Sector.values()) {
            sectorSelection.addItem(option.toString());
        }
        settings.add(sectorSelection);
        materialLabel = new MaterialLabel("Thematic");
        materialLabel.addStyleName(style.optionTitle());
        settings.add(materialLabel);
        for(Thematic option : Thematic.values()) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox();
            materialCheckBox.setText(option.toString());
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
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        for(Offer offer : offers) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
            productRow.add(materialColumn);
            if(offer instanceof ProductDTO) {
                materialColumn.add(new ProductWidget((ProductDTO) offer));
            }
        }
    }

    @Override
    public void setMatchingImagery(String text) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "Search or request imagery for '" + text + "'", style.productServicesTitle());
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
    public void displayCompaniesList(List<CompanyDTO> companyDTOs, int start, int limit, String text) {
        categories.setVisible(false);
        settings.setVisible(true);
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
        MaterialRow materialRow = new MaterialRow();
        container.add(materialRow);
        for(CompanyDTO companyDTO : companyDTOs) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 3);
            materialRow.add(materialColumn);
            materialColumn.add(new CompanyWidget(companyDTO));
        }
    }

    @Override
    public void setDatasetProviders(List<DatasetProviderDTO> datasetProviderDTOs, final String text, AoI aoi) {
        MaterialRow datasetsRow = new MaterialRow();
        container.add(datasetsRow);
        addTitle(datasetsRow, "Matching datasets", style.productServicesTitle());
        for(final DatasetProviderDTO datasetProviderDTO : datasetProviderDTOs) {
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
/*
                    CSWUtils.getRecordsResponse(uri, text, AoIUtil.getExtent(aoi), new AsyncCallback<CSWGetRecordsResponse>() {
                        @Override
                        public void onFailure(Throwable caught) {
                            Window.alert(caught.getMessage());
                        }

                        @Override
                        public void onSuccess(CSWGetRecordsResponse result) {
                            datasetsRow.remove(loadingWidget);
                            datasetsRow.add(new MaterialLabel("Datasets for " + datasetProviderDTO.getName()));
                            for(CSWBriefRecord cswBriefRecord : result.getRecords()) {
                                MaterialColumn datasetColumn = new MaterialColumn(12, 6, 4);
                                datasetsRow.add(datasetColumn);
                                datasetColumn.add(new DatasetWidget(cswBriefRecord));
                            }
                            if(result.getNextRecord() != 0) {
                                datasetsRow.add(new MaterialLabel("View all datasets (" + result.getNumberOfRecordsMatched() + " found)"));
                            }
                        }
                    });
*/
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
    public Widget asWidget() {
        return this;
    }

    @Override
    public void onResize(ResizeEvent event) {
        template.setPanelStyleName(style.navOpened(), filtersPanel.isVisible());
    }

}