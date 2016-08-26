package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.forms.ElementEditor;
import com.geocento.webapps.eobroker.common.client.widgets.forms.FormHelper;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.ArcGISMap;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.ArcgisMapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.DrawEventJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.DrawJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.feasibility.Feature;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceFeasibilityDTO;
import com.geocento.webapps.eobroker.customer.shared.SupplierAPIResponse;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductFeasibilityViewImpl extends Composite implements ProductFeasibilityView, ResizeHandler {

    interface ProductFeasibilityViewUiBinder extends UiBinder<Widget, ProductFeasibilityViewImpl> {
    }

    private static ProductFeasibilityViewUiBinder ourUiBinder = GWT.create(ProductFeasibilityViewUiBinder.class);

    @UiField(provided = true)
    TemplateView template;

    @UiField
    MaterialDropDown serviceDropdown;
    @UiField
    MaterialLink servicesLink;
    @UiField
    ArcGISMap mapContainer;
    @UiField
    MaterialAnchorButton drawPolygon;
    @UiField
    MaterialAnchorButton clearAoIs;
    @UiField
    MaterialTab tab;
    @UiField
    MaterialDatePicker startDate;
    @UiField
    MaterialDatePicker stopDate;
    @UiField
    SimplePanel resultsPanel;
    @UiField
    MaterialRow queryPanel;
    @UiField
    MaterialButton update;
    @UiField
    MaterialSideNav searchBar;
    @UiField
    MaterialRow parameters;
    @UiField
    HTMLPanel mapPanel;
    @UiField
    HTMLPanel results;

    private Presenter presenter;

    private Callback<Void, Exception> mapLoadedHandler = null;

    private MapJSNI map;

    private boolean mapLoaded = false;

    private CellTable<Product> resultsTable;

    private final ProvidesKey<Product> KEY_PROVIDER = new ProvidesKey<Product>() {
        @Override
        public Object getKey(Product item) {
            return item.getProductId();
        }
    };
    private final SelectionModel<Product> selectionModel = new MultiSelectionModel<Product>(KEY_PROVIDER);

    public ProductFeasibilityViewImpl(ClientFactoryImpl clientFactory) {
        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));
        onResize(null);
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
                        ProductFeasibilityViewImpl.this.map = mapJSNI;
                        final DrawJSNI drawJSNI = arcgisMap.createDraw(mapJSNI);
                        drawJSNI.onDrawEnd(new com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback<DrawEventJSNI>() {

                            @Override
                            public void callback(DrawEventJSNI result) {
                                drawJSNI.deactivate();
                                AoI aoi = AoIUtil.createAoI(arcgisMap.convertsToGeographic(result.getGeometry()));
                                displayAoI(aoi);
                                presenter.aoiChanged(aoi);
                            }
                        });
                        drawPolygon.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                mapJSNI.getGraphics().clear();
                                drawJSNI.activate("polygon");
                            }
                        });
                        clearAoIs.addClickHandler(new ClickHandler() {
                            @Override
                            public void onClick(ClickEvent event) {
                                mapJSNI.getGraphics().clear();
                                presenter.aoiChanged(null);
                            }
                        });
                        mapLoaded();
                    }
                });
            }
        });
        tab.setBackgroundColor("teal lighten-2");
        //tab.setIndicatorColor("yellow");

        startDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                presenter.onStartDateChanged(event.getValue());
            }
        });
        stopDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
                presenter.onStopDateChanged(event.getValue());
            }
        });
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
    public void displayAoI(AoI aoi) {
        map.getGraphics().clear();
        if(aoi != null) {
            map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoi), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }
    }

    @Override
    public void setText(String text) {

    }

    @Override
    public void displayLoadingResults(String message) {
        template.setLoading(message);
        searchBar.hide();
    }

    @Override
    public void hideLoadingResults() {
        template.hideLoading();
        searchBar.show();
        tab.selectTab("results");
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasClickHandlers getUpdateButton() {
        return update;
    }

    @Override
    public void enableUpdate(boolean enable) {
        update.setEnabled(enable);
    }

    @Override
    public void clearMap() {
        map.getGraphics().clear();
    }

    @Override
    public void displayLoading(String message) {
        template.setLoading(message);
    }

    @Override
    public void setServices(List<ProductServiceFeasibilityDTO> productServices) {
        serviceDropdown.clear();
        for(final ProductServiceFeasibilityDTO productServiceFeasibilityDTO : productServices) {
            MaterialLink materialLink = new MaterialLink(ButtonType.RAISED, productServiceFeasibilityDTO.getName(), new MaterialIcon(IconType.ADD_A_PHOTO));
            materialLink.setBackgroundColor("white");
            materialLink.setTextColor("black");
            materialLink.setTooltip("Service provided by " + productServiceFeasibilityDTO.getCompanyName());
            materialLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    presenter.onServiceChanged(productServiceFeasibilityDTO);
                }
            });
            serviceDropdown.add(materialLink);
        }
    }

    @Override
    public void setFormElements(List<FormElement> apiFormElements) {
        // remove previous elements
        parameters.clear();
        for(FormElement formElement : apiFormElements) {
            MaterialColumn materialColumn = new MaterialColumn(12, 12, 6);
            ElementEditor editor = FormHelper.createEditor(formElement);
            materialColumn.add(editor);
            parameters.add(materialColumn);
        }
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
    public void selectService(ProductServiceFeasibilityDTO productServiceFeasibilityDTO) {
        servicesLink.setText(productServiceFeasibilityDTO.getName());
    }

    @Override
    public void setStart(Date start) {
        startDate.setDate(start);
    }

    @Override
    public void setStop(Date stop) {
        stopDate.setDate(stop);
    }

    @Override
    public void setFormElementValues(List<FormElementValue> formElementValues) {
        for(int index = 0; index < parameters.getWidgetCount(); index++) {
            MaterialColumn materialColumn = (MaterialColumn) parameters.getWidget(index);
            if(materialColumn.getWidget(0) instanceof ElementEditor) {
                ElementEditor elementEditor = (ElementEditor) materialColumn.getWidget(0);
                // look for matching form element value
                for(FormElementValue formElementValue : formElementValues) {
                    if(formElementValue.getFormid().contentEquals(elementEditor.getFormElement().getFormid())) {
                        elementEditor.setFormElementValue(formElementValue.getValue());
                    }
                }
            }
        }
    }

    @Override
    public void displayResultsError(String message) {
        results.add(new MaterialLabel(message));
    }

    @Override
    public void displayResponse(SupplierAPIResponse response) {
        if(!response.isFeasible()) {
            results.add(new MaterialLabel(response.getMessage()));
            return;
        }
        results.add(new MaterialLabel(response.getMessage()));
        String features = "<h4>Available features:</h4>" +
                "<dl>";
        for(Feature feature : response.getFeatures()) {
            features += "<dt>" + feature.getName() + "</dt>" +
                    "<dd>" + feature.getDescription() + "</dd>";
        }
        features += "</dl>";
        results.add(new HTML(features));
    }

    @Override
    public void clearResults() {
        results.clear();
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void onResize(ResizeEvent event) {
        mapPanel.setHeight((Window.getClientHeight() - 64) + "px");
    }

}