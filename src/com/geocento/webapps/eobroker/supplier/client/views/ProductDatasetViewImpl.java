package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.shared.datasets.DatasetStandard;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.utils.DatasetAccessMapper;
import com.geocento.webapps.eobroker.supplier.client.widgets.DataAccessWidget;
import com.geocento.webapps.eobroker.supplier.client.widgets.OGCDataAccessWidget;
import com.geocento.webapps.eobroker.supplier.client.widgets.PerformanceValueWidget;
import com.geocento.webapps.eobroker.supplier.client.widgets.ProductTextBox;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.animate.MaterialAnimator;
import gwt.material.design.client.ui.animate.Transition;
import gwt.material.design.client.ui.html.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductDatasetViewImpl extends Composite implements ProductDatasetView {

    interface ProductDatasetViewUiBinder extends UiBinder<Widget, ProductDatasetViewImpl> {
    }

    private static ProductDatasetViewUiBinder ourUiBinder = GWT.create(ProductDatasetViewUiBinder.class);

    @UiField
    MaterialTextBox name;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialTitle title;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    ProductTextBox product;
    @UiField
    MaterialTextArea description;
    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.material.MaterialRichEditor fullDescription;
    @UiField
    MapContainer mapContainer;
    @UiField
    MaterialListValueBox<ServiceType> serviceType;
    @UiField
    MaterialRow dataAccess;
    @UiField
    MaterialButton addDataAccess;
    @UiField
    MaterialLabel dataAccessMessage;
    @UiField
    MaterialButton addHostedSample;
    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.material.MaterialFileUploader sampleUploader;
    @UiField
    MaterialListBox sampleAccessType;
    @UiField
    MaterialRow samples;
    @UiField
    MaterialLabel samplesMessage;
    @UiField
    MaterialListBox datasetAccessType;
    @UiField
    MaterialPanel geoinformation;
    @UiField
    MaterialLabel uploadSampleTitle;
    @UiField
    MaterialLabel uploadSampleComment;
    @UiField
    MaterialButton uploadSampleButton;
    @UiField
    MaterialPanel performances;
    @UiField
    MaterialTextBox performancesComment;
    @UiField
    MaterialPanel productPanel;
    @UiField
    MaterialTextBox geoinformationComment;
    @UiField
    MaterialDatePicker from;
    @UiField
    MaterialCheckBox untilCheck;
    @UiField
    MaterialDatePicker until;
    @UiField
    MaterialCheckBox refreshed;
    @UiField
    MaterialTextBox refreshRateDescription;
    @UiField
    MaterialTextBox temporalCoverageComment;
    @UiField
    MaterialLink viewClient;
    @UiField
    MaterialTextBox datasetsURL;
    @UiField
    MaterialListValueBox<DatasetStandard> datasetsStandard;
    @UiField
    MaterialPanel coverageLayers;
    @UiField
    MaterialButton addCoverageLayer;
    @UiField
    MaterialLabel coverageLayersMessage;

    private Presenter presenter;

    public ProductDatasetViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        for(ServiceType serviceType : ServiceType.values()) {
            this.serviceType.addItem(serviceType, serviceType.getName());
        }

        product.setPresenter(productDTO -> presenter.productChanged());

        for(AccessType accessType : AccessType.values()) {
            Option optionWidget = new Option();
            optionWidget.setText(accessType.getName());
            optionWidget.setValue(accessType.toString());
            datasetAccessType.add(optionWidget);
        }
        // quirk to make sure the list box is initialised
        datasetAccessType.setEnabled(true);

        for(AccessType accessType : new AccessType[] {AccessType.file, AccessType.ogc}) {
            Option optionWidget = new Option();
            optionWidget.setText(accessType.getName());
            optionWidget.setValue(accessType.toString());
            sampleAccessType.add(optionWidget);
        }
        // quirk to make sure the list box is initialised
        sampleAccessType.setEnabled(true);

        // TODO - replace with a widget that can ve used in various views
        // configure the sample uploader
        final String uploadUrl = GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/", "") + "upload/datasets/";
        sampleUploader.setUrl(uploadUrl);
        sampleUploader.setMaxFileSize(200);

        // Added the progress to card uploader
        sampleUploader.addTotalUploadProgressHandler(event -> {
        });

        sampleUploader.addSuccessHandler(event -> {
            String error = StringUtils.extract(event.getResponse().getBody(), "<error>", "</error>");
            if(error.length() > 0) {
                Window.alert(error);
                return;
            }
            String response = StringUtils.extract(event.getResponse().getBody(), "<value>", "</value>");

            DatasetAccessMapper datasetAccessMapper = GWT.create(DatasetAccessMapper.class);
            DatasetAccess datasetAccess = datasetAccessMapper.read(response);
            addSample(datasetAccess);

/*
            JSONObject sampleUploadDTOJson = JSONParser.parseLenient(response).isObject();
            SampleUploadDTO sampleUploadDTO = new SampleUploadDTO();
            sampleUploadDTO.setFileUri(sampleUploadDTOJson.containsKey("fileUri") ? sampleUploadDTOJson.get("fileUri").isString().stringValue() : null);
            sampleUploadDTO.setLayerName(sampleUploadDTOJson.containsKey("layerName") ? sampleUploadDTOJson.get("layerName").isString().stringValue() : null);
            sampleUploadDTO.setServer(sampleUploadDTOJson.containsKey("server") ? sampleUploadDTOJson.get("server").isString().stringValue() : null);
            sampleUploadDTO.setStyleName(sampleUploadDTOJson.containsKey("styleName") ? sampleUploadDTOJson.get("styleName").isString().stringValue() : null);
            if(sampleUploadDTO.getFileUri() != null) {
                DatasetAccessFile datasetAccessFile = new DatasetAccessFile();
                datasetAccessFile.setUri(sampleUploadDTO.getFileUri());
                datasetAccessFile.setTitle("Sample file");
                datasetAccessFile.setHostedData(false);
                addSample(datasetAccessFile);
            }
            if(sampleUploadDTO.getLayerName() != null) {
                DatasetAccessOGC datasetAccessOGC = new DatasetAccessOGC();
                // TODO - change to use the geoserver address
                datasetAccessOGC.setServerUrl(sampleUploadDTO.getServer());
                datasetAccessOGC.setUri(sampleUploadDTO.getLayerName());
                datasetAccessOGC.setTitle("Sample data available as OGC service");
                datasetAccessOGC.setStyleName(sampleUploadDTO.getStyleName());
                datasetAccessOGC.setHostedData(false);
                addSample(datasetAccessOGC);
            }
*/
        });

        sampleUploader.addDragOverHandler(event -> MaterialAnimator.animate(Transition.RUBBERBAND, sampleUploader, 0));

        for(DatasetStandard datasetStandard : DatasetStandard.values()) {
            datasetsStandard.addItem(datasetStandard, datasetStandard.getName());
        }

        template.setPlace("productdatasets");
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void setTitleLine(String title) {
        this.title.setTitle(title);
    }

    @Override
    public HasText getName() {
        return name;
    }

    @Override
    public void setServiceType(ServiceType serviceType) {
        this.serviceType.setValue(serviceType == null ? ServiceType.commercial : serviceType);
    }

    @Override
    public ServiceType getServiceType() {
        return serviceType.getSelectedValue();
    }

    @Override
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public String getImageUrl() {
        return imageUploader.getImageUrl();
    }

    @Override
    public void setIconUrl(String iconURL) {
        imageUploader.setImageUrl(iconURL);
    }

    @Override
    public HasText getDescription() {
        return description;
    }

    @Override
    public String getFullDescription() {
        return fullDescription.getHTML();
    }

    @Override
    public void setFullDescription(String fullDescription) {
        this.fullDescription.setHTML(fullDescription);
    }

    @Override
    public ProductDTO getSelectedProduct() {
        return product.getSelectedObject() == null ? null : (ProductDTO) product.getSelectedObject().getO();
    }

    @Override
    public void setSelectedProduct(ProductDTO productDTO) {
        product.setProduct(productDTO);
        productPanel.setVisible(productDTO != null);
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void setExtent(AoIDTO extent) {
        mapContainer.displayAoI(extent);
        mapContainer.centerOnAoI();
    }

    @Override
    public AoIDTO getExtent() {
        return mapContainer.getAoi();
    }

    @Override
    public void setCoverageLayers(List<DatasetAccessOGC> coverageLayers) {
        this.coverageLayers.clear();
        if(coverageLayers != null && coverageLayers.size() > 0) {
            for(DatasetAccessOGC coverageLayer : coverageLayers) {
                addCoverageLayer(coverageLayer);
            }
        }
        updateCoverageLayersMessage();
    }

    private void addCoverageLayer(DatasetAccessOGC coverageLayer) {
        OGCDataAccessWidget ogcDataAccessWidget = new OGCDataAccessWidget(coverageLayer, true, false);
        ogcDataAccessWidget.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        this.coverageLayers.add(ogcDataAccessWidget);
        ogcDataAccessWidget.getRemove().addClickHandler(event -> {
            if(Window.confirm("Are you sure you want to remove this layer?")) {
                this.coverageLayers.remove(ogcDataAccessWidget);
                updateCoverageLayersMessage();
            }
        });
        updateCoverageLayersMessage();
    }

    private void updateCoverageLayersMessage() {
        List<DatasetAccessOGC> coverageLayers = getCoverageLayers();
        coverageLayersMessage.setText(coverageLayers.size() == 0 ? "No layer, add a layer using the button below" :
                coverageLayers.size() + " layers provided, add more using the button below");
    }

    @Override
    public List<DatasetAccessOGC> getCoverageLayers() {
        List<DatasetAccessOGC> coverageLayers = new ArrayList<DatasetAccessOGC>();
        for(int index = 0; index < this.coverageLayers.getWidgetCount(); index++) {
            Widget widget = this.coverageLayers.getWidget(index);
            if(widget instanceof OGCDataAccessWidget) {
                OGCDataAccessWidget ogcDataAccessWidget = (OGCDataAccessWidget) widget;
                coverageLayers.add((DatasetAccessOGC) ogcDataAccessWidget.getDatasetAccess());
            }
        }
        return coverageLayers;
    }

    @UiHandler("addCoverageLayer")
    void addCoverageLayer(ClickEvent clickEvent) {
        addCoverageLayer(new DatasetAccessOGC());
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        mapContainer.setMapLoadedHandler(mapLoadedHandler);
    }

    @Override
    public void setDataAccess(List<DatasetAccess> datasetAccesses) {
        this.dataAccess.clear();
        if(datasetAccesses != null) {
            for(DatasetAccess datasetAccess : datasetAccesses) {
                addDataAccess(datasetAccess);
            }
        }
        updateDataAccessMessage();
    }

    private void addDataAccess(DatasetAccess datasetAccess) {
        final MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        this.dataAccess.add(materialColumn);
        DataAccessWidget dataAccessWidget = createDataAccessWidget(datasetAccess, true);
        dataAccessWidget.getRemove().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (Window.confirm("Are you sure you want to remove this data access?")) {
                    dataAccess.remove(materialColumn);
                }
            }
        });
        materialColumn.add(dataAccessWidget);
    }

    private DataAccessWidget createDataAccessWidget(DatasetAccess datasetAccess, boolean editableUri) {
        if(datasetAccess instanceof DatasetAccessFile) {
            return new DataAccessWidget(datasetAccess, editableUri);
        } else if(datasetAccess instanceof DatasetAccessAPP) {
            return new DataAccessWidget(datasetAccess, editableUri);
        } else if(datasetAccess instanceof DatasetAccessOGC) {
            return new OGCDataAccessWidget((DatasetAccessOGC) datasetAccess, editableUri);
        } else if(datasetAccess instanceof DatasetAccessAPI) {
            return new DataAccessWidget(datasetAccess, editableUri);
        }
        return null;
    }

    private void updateDataAccessMessage() {
        List<DatasetAccess> dataAccess = getDataAccesses();
        dataAccessMessage.setText(dataAccess.size() == 0 ? "No data access provided, add new data access using the button below" :
                dataAccess.size() + " data access defined, add more using the add button below");
    }

    @Override
    public List<DatasetAccess> getDataAccesses() {
        List<DatasetAccess> dataAccesses = new ArrayList<DatasetAccess>();
        for(int index = 0; index < dataAccess.getWidgetCount(); index++) {
            Widget widget = dataAccess.getWidget(index);
            if(widget instanceof MaterialColumn) {
                MaterialColumn materialColumn = (MaterialColumn) widget;
                if(materialColumn.getWidgetCount() > 0) {
                    Widget dataAccessWidget = materialColumn.getWidget(0);
                    if(dataAccessWidget instanceof DataAccessWidget) {
                        dataAccesses.add(((DataAccessWidget) dataAccessWidget).getDatasetAccess());
                    }
                }
            }
        }
        return dataAccesses;
    }

    @Override
    public void setSampleDataAccess(List<DatasetAccess> samples) {
        this.samples.clear();
        if(samples != null) {
            for(DatasetAccess datasetAccess : samples) {
                addSample(datasetAccess);
            }
        }
        updateSamplesMessage();
    }

    private void addSample(DatasetAccess datasetAccess) {
        final MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        this.samples.add(materialColumn);
        DataAccessWidget dataAccessWidget = createDataAccessWidget(datasetAccess, datasetAccess.isHostedData());
        dataAccessWidget.getRemove().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(Window.confirm("Are you sure you want to remove this sample?")) {
                    samples.remove(materialColumn);
                }
            }
        });
        materialColumn.add(dataAccessWidget);
    }

    private void updateSamplesMessage() {
        List<DatasetAccess> dataAccess = getSamples();
        samplesMessage.setText(dataAccess.size() == 0 ? "No samples provided, add new samples using the button below" :
                dataAccess.size() + " samples defined, add more using the add button below");
    }

    @Override
    public List<DatasetAccess> getSamples() {
        List<DatasetAccess> dataAccesses = new ArrayList<DatasetAccess>();
        for(int index = 0; index < samples.getWidgetCount(); index++) {
            Widget widget = samples.getWidget(index);
            if(widget instanceof MaterialColumn) {
                MaterialColumn materialColumn = (MaterialColumn) widget;
                if(materialColumn.getWidgetCount() > 0) {
                    Widget dataAccessWidget = materialColumn.getWidget(0);
                    if(dataAccessWidget instanceof DataAccessWidget) {
                        dataAccesses.add(((DataAccessWidget) dataAccessWidget).getDatasetAccess());
                    }
                }
            }
        }
        return dataAccesses;
    }

    @Override
    public List<FeatureDescription> getSelectedGeoinformation() {
        List<FeatureDescription> selectedFeatures = new ArrayList<FeatureDescription>();
        for(int index = 0; index < geoinformation.getWidgetCount(); index++) {
            Widget widget = geoinformation.getWidget(index);
            if(widget instanceof MaterialCheckBox) {
                MaterialCheckBox materialCheckBox = (MaterialCheckBox) widget;
                if(materialCheckBox.getValue()) {
                    selectedFeatures.add((FeatureDescription) materialCheckBox.getObject());
                }
            }
        }
        return selectedFeatures;
    }

    @Override
    public void setProductGeoinformation(List<FeatureDescription> featureDescriptions) {
        geoinformation.clear();
        if(featureDescriptions == null || featureDescriptions.size() == 0) {
            geoinformation.add(new MaterialLabel("No geoinformation associated to this product"));
        } else {
            for (FeatureDescription featureDescription : featureDescriptions) {
                MaterialCheckBox materialCheckBox = new MaterialCheckBox(featureDescription.getName());
                materialCheckBox.setObject(featureDescription);
                MaterialTooltip materialTooltip = new MaterialTooltip(materialCheckBox, featureDescription.getDescription());
                geoinformation.add(materialTooltip);
            }
        }
    }

    @Override
    public void setSelectedGeoinformation(List<FeatureDescription> featureDescriptions) {
        List<Long> selectedFeatures = ListUtil.mutate(featureDescriptions, new ListUtil.Mutate<FeatureDescription, Long>() {
            @Override
            public Long mutate(FeatureDescription object) {
                return object.getId();
            }
        });
        for(int index = 0; index < geoinformation.getWidgetCount(); index++) {
            Widget widget = geoinformation.getWidget(index);
            if(widget instanceof MaterialCheckBox) {
                MaterialCheckBox materialCheckBox = (MaterialCheckBox) widget;
                materialCheckBox.setValue(selectedFeatures.contains(((FeatureDescription) materialCheckBox.getObject()).getId()));
            }
        }
    }

    @Override
    public HasText getGeoinformationComment() {
        return geoinformationComment;
    }

    @Override
    public List<PerformanceValue> getSelectedPerformances() {
        List<PerformanceValue> performanceValues = new ArrayList<PerformanceValue>();
        for(int index = 0; index < performances.getWidgetCount(); index++) {
            Widget widget = performances.getWidget(index);
            if(widget instanceof PerformanceValueWidget) {
                PerformanceValueWidget performanceValueWidget = (PerformanceValueWidget) widget;
                PerformanceValue performanceValue = performanceValueWidget.getPerformanceValue();
                if(performanceValue != null) {
                    performanceValues.add(performanceValue);
                }
            }
        }
        return performanceValues;
    }

    @Override
    public void setProductPerformances(List<PerformanceDescription> performanceDescriptions) {
        performances.clear();
        if(performanceDescriptions == null || performanceDescriptions.size() == 0) {
            performances.add(new MaterialLabel("No performance associated to this product"));
        } else {
            for (PerformanceDescription performanceDescription : performanceDescriptions) {
                PerformanceValueWidget performanceValueWidget = new PerformanceValueWidget();
                performanceValueWidget.setPerformanceDescription(performanceDescription);
                performances.add(performanceValueWidget);
            }
        }
    }

    @Override
    public void setProvidedPerformances(List<PerformanceValue> performanceValues) {
        for(int index = 0; index < performances.getWidgetCount(); index++) {
            Widget widget = performances.getWidget(index);
            if(widget instanceof PerformanceValueWidget) {
                final PerformanceValueWidget performanceValueWidget = (PerformanceValueWidget) widget;
                PerformanceValue performanceValue = ListUtil.findValue(performanceValues, new ListUtil.CheckValue<PerformanceValue>() {
                    @Override
                    public boolean isValue(PerformanceValue value) {
                        return value.getPerformanceDescription().getId().equals(performanceValueWidget.getPerformanceDescription().getId());
                    }
                });
                performanceValueWidget.setPerformanceValue(performanceValue);
            }
        }
    }

    @Override
    public HasText getPerformancesComment() {
        return performancesComment;
    }

    @Override
    public void setSampleProductDatasetId(Long datasetId) {
        uploadSampleTitle.setText("Upload sample files to our servers");
        if(datasetId == null) {
            uploadSampleComment.setText("Sorry, you need to SUBMIT before you can upload files to our servers");
            uploadSampleComment.setTextColor(Color.ORANGE);
            uploadSampleButton.setEnabled(false);
        } else {
            uploadSampleComment.setText("Allowed files are shapefiles (zipped), GeoTIFF and documents (PDF, doc, xls...)");
            uploadSampleComment.setTextColor(Color.GREY_DARKEN_1);
            uploadSampleButton.setEnabled(true);
            sampleUploader.setParameter("resourceId", datasetId + "");
        }
    }

    @Override
    public void setTemporalCoverage(TemporalCoverage temporalCoverage) {
        from.setDate(temporalCoverage.getStartDate());
        untilCheck.setValue(temporalCoverage.getStopDate() != null);
        until.setDate(temporalCoverage.getStopDate());
        refreshed.setValue(temporalCoverage.getRefreshed());
        refreshRateDescription.setText(temporalCoverage.getRefreshRateDescription());
    }

    @Override
    public TemporalCoverage getTemporalCoverage() {
        TemporalCoverage temporalCoverage = new TemporalCoverage();
        temporalCoverage.setStartDate(from.getDate());
        if(untilCheck.getValue()) {
            temporalCoverage.setStopDate(until.getDate());
        }
        temporalCoverage.setRefreshed(refreshed.getValue());
        temporalCoverage.setRefreshRateDescription(refreshRateDescription.getValue());
        return temporalCoverage;
    }

    @Override
    public HasText getTemporalCoverageComment() {
        return temporalCoverageComment;
    }

    @UiHandler("addDataAccess")
    void addDataAccess(ClickEvent clickEvent) {
        AccessType selectedType = AccessType.valueOf(datasetAccessType.getValue());
        DatasetAccess datasetAccess = createDataAccess(selectedType, true);
        addDataAccess(datasetAccess);
        updateDataAccessMessage();
    }

    private DatasetAccess createDataAccess(AccessType selectedType, boolean hostedData) {
        DatasetAccess datasetAccess = null;
        switch(selectedType) {
            case file:
                datasetAccess = new DatasetAccessFile();
                break;
            case ogc:
                datasetAccess = new DatasetAccessOGC();
                break;
            case application:
                datasetAccess = new DatasetAccessAPP();
                break;
            case api:
                datasetAccess = new DatasetAccessAPI();
                break;
        }
        datasetAccess.setHostedData(hostedData);
        return datasetAccess;
    }

    @UiHandler("addHostedSample")
    void addHostedSample(ClickEvent clickEvent) {
        AccessType selectedType = AccessType.valueOf(sampleAccessType.getValue());
        addSample(createDataAccess(selectedType, true));
        updateSamplesMessage();
    }

    @Override
    public HasClickHandlers getViewClient() {
        return viewClient;
    }

    @Override
    public HasText getDatasetURL() {
        return datasetsURL;
    }

    @Override
    public HasValue<DatasetStandard> getDatasetStandard() {
        return datasetsStandard;
    }

}