package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.styles.StyleResources;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLabelIcon;
import com.geocento.webapps.eobroker.common.client.widgets.MoreWidget;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.events.GetFeatureInfo;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.shared.LayerInfoDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDatasetVisualisationDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceVisualisationDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class VisualisationViewImpl extends Composite implements VisualisationView, ResizeHandler {

    interface VisualisationUiBinder extends UiBinder<Widget, VisualisationViewImpl> {
    }

    private static VisualisationUiBinder ourUiBinder = GWT.create(VisualisationUiBinder.class);

    static public interface Style extends CssResource {

        String dataAccess();
    }

    @UiField
    Style style;

    @UiField
    MapContainer mapContainer;
    @UiField
    HTMLPanel displayPanel;
    @UiField
    MaterialDropDown dataAccessList;
    @UiField
    MaterialImage image;
    @UiField
    MaterialLabel name;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLink addToFavourites;
    @UiField
    MaterialLabel selectedDataset;
    @UiField
    MaterialChip supplier;
    @UiField
    MaterialChip resource;
    @UiField
    LoadingWidget loading;
    @UiField
    MaterialPanel dataAccessDetails;
    @UiField
    MaterialRange opacity;
    @UiField
    MaterialLink serverUrl;
    @UiField
    MaterialLink layerName;
    @UiField
    MaterialLabel errorMessage;
    @UiField
    MaterialLink moreDatasets;
    @UiField
    HeaderPanel imagePanel;
    @UiField
    MaterialSwitch getFeature;
    @UiField
    DockLayoutPanel panel;
    @UiField
    MaterialPanel featureInfo;

    private Presenter presenter;

    private WMSLayerJSNI wmsLayer;

    private boolean queryable;

    private final ClientFactoryImpl clientFactory;

    public VisualisationViewImpl(ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        opacity.addChangeHandler(event -> setOpacity(opacity.getValue()));
        setOpacity(100);

        imagePanel.getElement().getParentElement().addClassName("z-depth-1");
        imagePanel.getElement().getParentElement().getStyle().setZIndex(10);

        setQueryable(false);
        getFeature.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setQueryable(event.getValue());
            }
        });

        Scheduler.get().scheduleDeferred(() -> {
            onResize(null);
        });
    }

    private void setOpacity(int value) {
        opacity.setValue(value);
        updateOpacity();
    }

    private void updateOpacity() {
        if(wmsLayer != null) {
            wmsLayer.setOpacity(opacity.getValue() / 100.0);
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        mapContainer.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                mapLoadedHandler.onFailure(reason);
            }

            @Override
            public void onSuccess(Void result) {
                // add click handler to map
                mapContainer.map.onEvent("click", new com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback<JavaScriptObject>() {

                    int latest = 0;

                    @Override
                    public void callback(JavaScriptObject result) {
                        if (queryable) {
                            GetFeatureInfo getFeatureInfo = new GetFeatureInfo();
                            PointJSNI point = ((MapMouseEventJSNI) result).getMapPoint();
                            PointJSNI screenPoint = ((MapMouseEventJSNI) result).getScreenPoint();
                            int size = 1;
                            int width = 2 * size;
                            int height = 2 * size;
                            getFeatureInfo.setWidth(width);
                            getFeatureInfo.setHeight(height);
                            getFeatureInfo.setPoint(new int[]{size, size});
                            getFeatureInfo.setMapPoint(point);
                            // calculate extent based on point on map and width and height
                            PointJSNI firstPoint = mapContainer.map.toMap(new double[]{screenPoint.getX() - width / 2, screenPoint.getY() - height / 2});
                            PointJSNI secondPoint = mapContainer.map.toMap(new double[]{screenPoint.getX() + width / 2, screenPoint.getY() + height / 2});
                            firstPoint = (PointJSNI) mapContainer.getArcgisMap().convertsToGeographic(firstPoint);
                            secondPoint = (PointJSNI) mapContainer.getArcgisMap().convertsToGeographic(secondPoint);
                            Extent extent = new Extent();
                            extent.setSouth(Math.min(firstPoint.getY(), secondPoint.getY()));
                            extent.setWest(Math.min(firstPoint.getX(), secondPoint.getX()));
                            extent.setNorth(Math.max(firstPoint.getY(), secondPoint.getY()));
                            extent.setEast(Math.max(firstPoint.getX(), secondPoint.getX()));
                            getFeatureInfo.setExtent(extent);
                            clientFactory.getEventBus().fireEvent(getFeatureInfo);
                        }
                    }
                });
                mapLoadedHandler.onSuccess(result);
            }
        });
    }

    @Override
    public void setWMSLayer(LayerInfoDTO layerInfoDTO) {
        if(wmsLayer != null) {
            mapContainer.map.removeWMSLayer(wmsLayer);
        }
        Extent extent = layerInfoDTO.getExtent();
        ExtentJSNI extentJSNI = MapJSNI.createExtent(extent.getWest(), extent.getSouth(), extent.getEast(), extent.getNorth());
        wmsLayer = mapContainer.map.addWMSLayer(layerInfoDTO.getServerUrl(),
                WMSLayerInfoJSNI.createInfo(layerInfoDTO.getLayerName(), layerInfoDTO.getLayerName()),
                extentJSNI, layerInfoDTO.getStyleName());
        mapContainer.map.setExtent(extentJSNI);
        updateOpacity();
    }

    @Override
    public void setLoadingInformation(String message) {
        hideContent();
        loading.setText(message);
        loading.setVisible(true);
    }

    @Override
    public void hideLoadingInformation() {
        hideContent();
        dataAccessDetails.setVisible(true);
    }

    @Override
    public void displayInformationError(String message) {
        hideContent();
        errorMessage.setVisible(true);
        errorMessage.setText(message);
    }

    private void hideContent() {
        loading.setVisible(false);
        errorMessage.setVisible(false);
        dataAccessDetails.setVisible(false);
    }

    @Override
    public void setProductService(ProductServiceVisualisationDTO productServiceVisualisationDTO) {
        image.setUrl(productServiceVisualisationDTO.getServiceImage());
        name.setText(productServiceVisualisationDTO.getName());
        setCompany(productServiceVisualisationDTO.getCompany());
        setProduct(productServiceVisualisationDTO.getProduct());
        dataAccessList.clear();
        boolean hasSamples = false;
        // add samples
        {
            List<DatasetAccess> samples = productServiceVisualisationDTO.getSamples();
            if (samples != null && samples.size() > 0) {
                hasSamples = true;
                MaterialLabel materialLabel = new MaterialLabel("Samples");
                materialLabel.addStyleName(style.dataAccess());
                dataAccessList.add(materialLabel);
                for (final DatasetAccess datasetAccess : samples) {
                    addDatasetAccess(datasetAccess);
                }
            }
        }
        moreDatasets.setVisible(hasSamples);
        moreDatasets.setText("More layers from this service...");
    }

    @Override
    public void enableGetFeatureInfo(boolean queryable) {
        getFeature.setVisible(queryable);
    }

    @Override
    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
        mapContainer.getElement().getStyle().setCursor(queryable ? com.google.gwt.dom.client.Style.Cursor.CROSSHAIR : com.google.gwt.dom.client.Style.Cursor.AUTO);
        getFeature.setValue(queryable);
        panel.setWidgetHidden(featureInfo, !queryable);
        featureInfo.clear();
        featureInfo.add(new MaterialLabel("Click on a feature in the map to get information"));
    }

    @Override
    public HasValue<Boolean> getFeatureInfoSwitch() {
        return getFeature;
    }

    @Override
    public void displayLayerInfo(LayerInfoDTO layerInfoDTO) {
        serverUrl.setText("Server URL: " + layerInfoDTO.getServerUrl());
        layerName.setText("Layer name: " + layerInfoDTO.getLayerName());
    }

    @Override
    public void setProductDataset(ProductDatasetVisualisationDTO productDatasetVisualisationDTO) {
        image.setUrl(productDatasetVisualisationDTO.getImageUrl());
        name.setText(productDatasetVisualisationDTO.getName());
        setCompany(productDatasetVisualisationDTO.getCompany());
        setProduct(productDatasetVisualisationDTO.getProduct());
        dataAccessList.clear();
        boolean hasSamples = false;
        // add data access
        {
            List<DatasetAccess> dataAccesses = productDatasetVisualisationDTO.getDatasetAccess();
            if (dataAccesses != null && dataAccesses.size() > 0) {
                hasSamples = true;
                MaterialLabel materialLabel = new MaterialLabel("Full data access");
                materialLabel.addStyleName(style.dataAccess());
                dataAccessList.add(materialLabel);
                for (final DatasetAccess datasetAccess : dataAccesses) {
                    addDatasetAccess(datasetAccess);
                }
            }
        }
        // add samples
        {
            List<DatasetAccess> samples = productDatasetVisualisationDTO.getSamples();
            if (samples != null && samples.size() > 0) {
                hasSamples = true;
                MaterialLabel materialLabel = new MaterialLabel("Samples");
                materialLabel.addStyleName(style.dataAccess());
                dataAccessList.add(materialLabel);
                for (final DatasetAccess datasetAccess : samples) {
                    addDatasetAccess(datasetAccess);
                }
            }
        }
        moreDatasets.setVisible(hasSamples);
        moreDatasets.setText("More layers from this dataset...");
    }

    private void addDatasetAccess(final DatasetAccess datasetAccess) {
        MaterialLink materialLink = new MaterialLink();
        materialLink.setIconType(IconType.CLOUD_CIRCLE);
        materialLink.setText(datasetAccess.getTitle());
        materialLink.setTruncate(true);
        dataAccessList.add(materialLink);
        materialLink.addClickHandler(event -> presenter.datasetAccessSelected(datasetAccess));
    }

    private void setProduct(final ProductDTO product) {
        resource.setText(product.getName());
        resource.setUrl(Utils.getImageMaybe(product.getImageUrl()));
        resource.addClickHandler(event -> clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + product.getId())));
    }

    private void setCompany(final CompanyDTO company) {
        supplier.setText(company.getName());
        supplier.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + company.getId()));
            }
        });
        supplier.getElement().getStyle().setCursor(com.google.gwt.dom.client.Style.Cursor.POINTER);
    }

    @Override
    public void selectDataAccess(DatasetAccess datasetAccess) {
        selectedDataset.setText(datasetAccess.getTitle());
    }

    @Override
    public void setDataAccessDescription(String pitch) {
        description.setText(pitch);
    }

    @Override
    public void displayMapInfoLoading(PointJSNI location, String title, String message) {
        MaterialLabelIcon loadingWidget = new MaterialLabelIcon(StyleResources.INSTANCE.loadingSmall(), message);
        mapContainer.displayInfoWindow(title, loadingWidget.getElement().getString(), location);
    }

    @Override
    public void hideMapInfoLoading(PointJSNI location, String message) {
        mapContainer.hideInfoWindow();
    }

    @Override
    public void displayMapInfoContent(PointJSNI location, String title, String content) {
        mapContainer.displayInfoWindow(title, content, location);
    }

    @Override
    public void displayGetFeatureInfoContent(String content) {
        featureInfo.clear();
        featureInfo.add(new HTML(content));
    }

    @Override
    public void displayGetFeatureInfoLoading(String message) {
        featureInfo.clear();
        featureInfo.add(new LoadingWidget(message));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void onResize(ResizeEvent event) {
        if(mapContainer.map != null) {
            mapContainer.map.resize();
        }
        imagePanel.onResize();
    }

}