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
import com.geocento.webapps.eobroker.customer.shared.ProductServiceDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductFormViewImpl extends Composite implements ProductFormView {

    interface ProductFormUiBinder extends UiBinder<Widget, ProductFormViewImpl> {
    }

    private static ProductFormUiBinder ourUiBinder = GWT.create(ProductFormUiBinder.class);

    static public interface Style extends CssResource {

        String editor();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    HTMLPanel formContainer;
    @UiField
    HTMLPanel productServices;
    @UiField
    MaterialImage image;
    @UiField
    MaterialLabel name;
    @UiField
    MaterialLink information;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialButton submit;
    @UiField
    HTMLPanel mapPanel;
    @UiField
    ArcGISMap mapContainer;
    @UiField
    MaterialAnchorButton drawPolygon;
    @UiField
    MaterialAnchorButton clearAoIs;

    private Presenter presenter;

    private Callback<Void, Exception> mapLoadedHandler = null;

    public MapJSNI map;

    private boolean mapLoaded = false;

    public ProductFormViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        template.setTitleText("Product form");

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
                        final DrawJSNI drawJSNI = arcgisMap.createDraw(mapJSNI);
                        drawJSNI.onDrawEnd(new com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback<DrawEventJSNI>() {

                            @Override
                            public void callback(DrawEventJSNI result) {
                                drawJSNI.deactivate();
                                AoI aoi = AoIUtil.createAoI(arcgisMap.convertsToGeographic(result.getGeometry()));
                                displayAoI(aoi);
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
                            }
                        });
                        ProductFormViewImpl.this.map = mapJSNI;
                        map.setZoom(3);
                        mapLoaded();
                    }
                });
            }
        });
    }

    @Override
    public void displayAoI(AoI aoi) {
        map.getGraphics().clear();
        if(aoi != null) {
            map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoi), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        this.mapLoadedHandler = mapLoadedHandler;
        if(mapLoaded) {
            mapLoadedHandler.onSuccess(null);
        }
    }

    private void mapLoaded() {
        mapLoaded = true;
        if(mapLoadedHandler != null) {
            mapLoadedHandler.onSuccess(null);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void clearForm() {
        formContainer.clear();
    }

    @Override
    public void addFormElement(FormElement formElement) {
        ElementEditor editor = createEditor(formElement);
        editor.addStyleName(style.editor());
        formContainer.add(editor);
    }

    private ElementEditor createEditor(final FormElement formElement) {
        return FormHelper.createEditor(formElement);
    }

    @Override
    public void clearSuppliers() {
        productServices.clear();
    }

    @Override
    public void addProductService(ProductServiceDTO productServiceDTO) {
        MaterialCheckBox materialCheckBox =
                new MaterialCheckBox("<span style='display: inline;'><b>" + productServiceDTO.getName() + "</b> " +
                        "by <img style='max-height: 24px; vertical-align: middle;' src='" + productServiceDTO.getCompanyLogo() + "'/> <b>" + productServiceDTO.getCompanyName() + "</b></span>", true);
        materialCheckBox.setObject(productServiceDTO);
        productServices.add(materialCheckBox);
    }

    @Override
    public void setProductImage(String imageUrl) {
        image.setUrl(imageUrl);
    }

    @Override
    public void setProductName(String name) {
        this.name.setText(name);
    }

    @Override
    public void setProductDescription(String description) {
        this.description.setText(description);
    }

    @Override
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public void setInformationUrl(String url) {
        information.setHref(url);
    }

    @Override
    public void displayLoading(String message) {
        template.setLoading(message);
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
    public List<FormElementValue> getFormElementValues() throws Exception {
        List<FormElementValue> formElementValues = new ArrayList<FormElementValue>();
        for(int index = 0; index < formContainer.getWidgetCount(); index++) {
            Widget widget = formContainer.getWidget(index);
            if(widget instanceof ElementEditor) {
                formElementValues.add(((ElementEditor) widget).getFormElementValue());
            }
        }
        return formElementValues;
    }

    @Override
    public void displayFormValidationError(String message) {
        Window.alert("Problem with your form " + message);
    }

    @Override
    public List<ProductServiceDTO> getSelectedServices() {
        List<ProductServiceDTO> productServiceDTOs = new ArrayList<ProductServiceDTO>();
        for(int index = 0; index < productServices.getWidgetCount(); index++) {
            Widget widget = productServices.getWidget(index);
            if(widget instanceof MaterialCheckBox) {
                MaterialCheckBox materialCheckBox = (MaterialCheckBox) widget;
                if(((MaterialCheckBox) widget).getValue()) {
                    productServiceDTOs.add((ProductServiceDTO) materialCheckBox.getObject());
                }
            }
        }
        return productServiceDTOs;
    }

    @Override
    public void displaySubmittedSuccess(String message) {
        displaySuccess(message);
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}