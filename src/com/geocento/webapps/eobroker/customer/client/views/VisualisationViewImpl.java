package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialSideNav;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.ExtentJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.WMSLayerInfoJSNI;
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
import com.google.gwt.dom.client.Style;
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
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class VisualisationViewImpl extends Composite implements VisualisationView, ResizeHandler {

    private Presenter presenter;

    interface VisualisationUiBinder extends UiBinder<Widget, VisualisationViewImpl> {
    }

    private static VisualisationUiBinder ourUiBinder = GWT.create(VisualisationUiBinder.class);

    static public interface Style extends CssResource {

        String navOpened();

        String dataAccess();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MapContainer mapContainer;
    @UiField
    HTMLPanel displayPanel;
    @UiField
    MaterialSideNav resourcesBar;
    @UiField
    MaterialDropDown dataAccessList;
    @UiField
    MaterialImage image;
    @UiField
    MaterialLabel name;
    @UiField
    MaterialPanel details;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLink addToFavourites;
    @UiField
    MaterialLink selectedDataset;
    @UiField
    MaterialChip supplier;
    @UiField
    MaterialChip resource;

    public VisualisationViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                resourcesBar.show();
                onResize(null);
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        mapContainer.setMapLoadedHandler(mapLoadedHandler);
    }

    @Override
    public void addWMSLayer(String wmsUrl, String layerName) {
        //mapContainer.map.removeAllLayers();
        mapContainer.map.addWMSLayer(wmsUrl, WMSLayerInfoJSNI.createInfo(layerName, layerName), MapJSNI.createExtent(-180.0, -90.0, 180.0, 90.0));
    }

    @Override
    public void addWMSLayer(LayerInfoDTO layerInfoDTO) {
        Extent extent = layerInfoDTO.getExtent();
        ExtentJSNI extentJSNI = MapJSNI.createExtent(extent.getWest(), extent.getSouth(), extent.getEast(), extent.getNorth());
        mapContainer.map.addWMSLayer(layerInfoDTO.getServerUrl(),
                WMSLayerInfoJSNI.createInfo(layerInfoDTO.getLayerName(), layerInfoDTO.getLayerName()),
                extentJSNI);
        mapContainer.map.setExtent(extentJSNI);
    }

    @Override
    public void setLoadingInformation(String message) {
        details.clear();
        details.add(new LoadingWidget("Loading..."));
    }

    @Override
    public void hideLoadingInformation() {
        details.clear();
    }

    @Override
    public void displayInformationError(String message) {
        details.clear();
        details.add(new MaterialLabel(message));
    }

    @Override
    public void setProductService(ProductServiceVisualisationDTO productServiceVisualisationDTO) {
        image.setUrl(productServiceVisualisationDTO.getServiceImage());
        name.setText(productServiceVisualisationDTO.getName());
        setCompany(productServiceVisualisationDTO.getCompany());
        setProduct(productServiceVisualisationDTO.getProduct());
        dataAccessList.clear();
        // add samples
        {
            List<DatasetAccess> samples = productServiceVisualisationDTO.getSamples();
            if (samples != null && samples.size() > 0) {
                MaterialLabel materialLabel = new MaterialLabel("Samples");
                materialLabel.addStyleName(style.dataAccess());
                dataAccessList.add(materialLabel);
                for (final DatasetAccess datasetAccess : samples) {
                    addDatasetAccess(datasetAccess);
                }
            }
        }
    }

    @Override
    public void displayLayerInfo(LayerInfoDTO layerInfoDTO) {
        details.clear();
        details.add(new HTMLPanel("<dl>" +
                "<dt>Server URL</dt>" +
                "<dl>" + layerInfoDTO.getServerUrl() + "</dl>" +
                "<dt>Layer name</dt>" +
                "<dl>" + layerInfoDTO.getLayerName() + "</dl>" +
                "<dt>Available styles<dt>" +
                "<dl>TODO...</dl>"));
    }

    @Override
    public void setProductDataset(ProductDatasetVisualisationDTO productDatasetVisualisationDTO) {
        image.setUrl(productDatasetVisualisationDTO.getImageUrl());
        name.setText(productDatasetVisualisationDTO.getName());
        setCompany(productDatasetVisualisationDTO.getCompany());
        setProduct(productDatasetVisualisationDTO.getProduct());
        dataAccessList.clear();
        // add data access
        {
            List<DatasetAccess> dataAccesses = productDatasetVisualisationDTO.getDatasetAccess();
            if (dataAccesses != null && dataAccesses.size() > 0) {
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
                MaterialLabel materialLabel = new MaterialLabel("Samples");
                materialLabel.addStyleName(style.dataAccess());
                dataAccessList.add(materialLabel);
                for (final DatasetAccess datasetAccess : samples) {
                    addDatasetAccess(datasetAccess);
                }
            }
        }
    }

    private void addDatasetAccess(final DatasetAccess datasetAccess) {
        MaterialLink materialLink = new MaterialLink();
        materialLink.setIconType(IconType.CLOUD_CIRCLE);
        materialLink.setText(datasetAccess.getTitle());
        materialLink.setTruncate(true);
        dataAccessList.add(materialLink);
        materialLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                presenter.datasetAccessSelected(datasetAccess);
            }
        });
    }

    private void setProduct(final ProductDTO product) {
        resource.setText(product.getName());
        resource.setUrl(Utils.getImageMaybe(product.getImageUrl()));
        resource.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                template.getClientFactory().getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + product.getId()));
            }
        });
    }

    private void setCompany(final CompanyDTO company) {
        supplier.setText(company.getName());
        supplier.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                template.getClientFactory().getPlaceController().goTo(new FullViewPlace(FullViewPlace.TOKENS.companyid.toString() + "=" + company.getId()));
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
        displayPanel.setHeight((Window.getClientHeight() - 64) + "px");
        template.setPanelStyleName(style.navOpened(), resourcesBar.isOpen());
        if(mapContainer.map != null) {
            mapContainer.map.resize();
        }
    }

}