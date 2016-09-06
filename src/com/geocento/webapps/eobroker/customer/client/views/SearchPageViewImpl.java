package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.ArcGISMap;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.LoginPagePlace;
import com.geocento.webapps.eobroker.customer.client.widgets.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.ProgressType;
import gwt.material.design.client.ui.*;

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
    MaterialCollapsibleItem categoriesPanel;
    @UiField
    MaterialSideNav filtersPanel;
    @UiField
    HTMLPanel container;
    @UiField
    ArcGISMap mapContainer;
    @UiField
    MaterialCollapsibleItem options;
    @UiField
    HTMLPanel settings;

    private Callback<Void, Exception> mapLoadedHandler = null;

    private boolean mapLoaded = false;

    private MapJSNI map;

    private GraphicJSNI aoiRendering;

    private Presenter presenter;

    public SearchPageViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        // add categories controls
        categories.clear();
        for(Category category : Category.values()) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox();
            materialCheckBox.setText(category.getName());
            materialCheckBox.setObject(category);
            categories.add(materialCheckBox);
            materialCheckBox.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // TODO - update display
                }
            });
        }
        categoriesPanel.expand();

        mapContainer.loadArcGISMap(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {

            }

            @Override
            public void onSuccess(Void result) {
                mapContainer.createMap("streets", new LatLng(40.0, -4.0), 3, new com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback<MapJSNI>() {

                    @Override
                    public void callback(final MapJSNI mapJSNI) {
                        final ArcgisMapJSNI arcgisMap = mapContainer.arcgisMap;
                        map = mapJSNI;
                        mapLoaded();
                    }
                });
            }
        });

        filtersPanel.show();

        onResize(null);
    }

    private void mapLoaded() {
        mapLoaded = true;
        if(mapLoadedHandler != null) {
            mapLoadedHandler.onSuccess(null);
        }
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        this.mapLoadedHandler = mapLoadedHandler;
        if(mapLoaded) {
            mapLoadedHandler.onSuccess(null);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setCurrentSearch(String search) {
        currentSearch.setText(search);
    }

    @Override
    public void displayAoI(AoI aoi) {
        if(aoiRendering != null) {
            map.getGraphics().remove(aoiRendering);
        }
        if(aoi != null) {
            aoiRendering = map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoi), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }
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
        MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
        productRow.add(materialColumn);
        materialColumn.add(new ProductWidget(productDTO));
        addTitle(productRow, "EO Broker services offering this product", style.productServicesTitle());
        for(ProductServiceDTO productServiceDTO : services) {
            MaterialColumn serviceColumn = new MaterialColumn(12, 12, 6);
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
        categoriesPanel.setVisible(true);
        options.setVisible(false);
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, suggestedProducts == null || suggestedProducts.size() == 0 ? "No product matching your request" :
                                        "Products matching your request",
                style.productTitle());
        for(ProductDTO productDTO : suggestedProducts) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
            materialColumn.add(new ProductWidget(productDTO));
            productRow.add(materialColumn);
        }
    }

    @Override
    public void setMatchingServices(List<ProductServiceDTO> productServices) {
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        addTitle(productRow, "EO Broker services matching your request", style.productServicesTitle());
        for(ProductServiceDTO productServiceDTO : productServices) {
            MaterialColumn serviceColumn = new MaterialColumn(12, 6, 4);
            productRow.add(serviceColumn);
            serviceColumn.add(new ProductServiceWidget(productServiceDTO));
        }
    }

    @Override
    public void displayProductsList(List<ProductDTO> products, int start, int limit, String text) {
        categoriesPanel.setVisible(false);
        options.setVisible(true);
        settings.clear();
        settings.add(new MaterialLabel("Sector"));
        MaterialListBox sectorSelection = new MaterialListBox();
        sectorSelection.addItem("All");
        for(Sector option : Sector.values()) {
            sectorSelection.addItem(option.toString());
        }
        settings.add(sectorSelection);
        settings.add(new MaterialLabel("Thematic"));
        for(Thematic option : Thematic.values()) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox();
            materialCheckBox.setText(option.toString());
            materialCheckBox.setObject(option);
            settings.add(materialCheckBox);
            materialCheckBox.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // TODO - update display
                }
            });
        }
        options.expand();
        MaterialRow productRow = new MaterialRow();
        container.add(productRow);
        for(ProductDTO productDTO : products) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
            productRow.add(materialColumn);
            materialColumn.add(new ProductWidget(productDTO));
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
        categoriesPanel.setVisible(false);
        options.setVisible(true);
        settings.clear();
        settings.add(new MaterialLabel("Company size"));
        for(String option : new String[] {"Large corporation > 4000", "Large < 4000", "Medium-sized < 250", "Small < 50", "Micro < 10"}) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox();
            materialCheckBox.setText(option);
            materialCheckBox.setObject(option);
            settings.add(materialCheckBox);
            materialCheckBox.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // TODO - update display
                }
            });
        }
        settings.add(new MaterialLabel("Company certifications"));
        for(String option : new String[] {"Certification 1", "Certification 2", "Certification 3"}) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox();
            materialCheckBox.setText(option);
            materialCheckBox.setObject(option);
            settings.add(materialCheckBox);
            materialCheckBox.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // TODO - update display
                }
            });
        }
        options.expand();
        MaterialRow materialRow = new MaterialRow();
        container.add(materialRow);
        for(CompanyDTO companyDTO : companyDTOs) {
            MaterialColumn materialColumn = new MaterialColumn(12, 6, 4);
            materialRow.add(materialColumn);
            materialColumn.add(new CompanyWidget(companyDTO));
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