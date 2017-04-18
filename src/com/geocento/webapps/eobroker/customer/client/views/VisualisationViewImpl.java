package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialSideNav;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.ExtentJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.WMSLayerInfoJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.WMSLayerJSNI;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.shared.LayerInfoDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductDatasetVisualisationDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceVisualisationDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HeaderPanel;
import com.google.gwt.user.client.ui.Widget;
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

    private Presenter presenter;

    private WMSLayerJSNI wmsLayer;

    private final ClientFactoryImpl clientFactory;

    public VisualisationViewImpl(ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        opacity.addChangeHandler(event -> setOpacity(opacity.getValue()));
        setOpacity(100);

        imagePanel.getElement().getParentElement().addClassName("z-depth-1");
        imagePanel.getElement().getParentElement().getStyle().setZIndex(10);

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
        mapContainer.setMapLoadedHandler(mapLoadedHandler);
    }

    @Override
    public void addWMSLayer(LayerInfoDTO layerInfoDTO) {
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
    public Widget asWidget() {
        return this;
    }

    @Override
    public void onResize(ResizeEvent event) {
        if(mapContainer.map != null) {
            mapContainer.map.resize();
        }
    }

}