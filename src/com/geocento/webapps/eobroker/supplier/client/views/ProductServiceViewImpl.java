package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialFileUploader;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.widgets.DataAccessWidget;
import com.geocento.webapps.eobroker.supplier.client.widgets.OGCDataAccessWidget;
import com.geocento.webapps.eobroker.supplier.client.widgets.PerformanceValueWidget;
import com.geocento.webapps.eobroker.supplier.client.widgets.ProductTextBox;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SampleUploadDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.DragOverEvent;
import gwt.material.design.addins.client.fileuploader.events.SuccessEvent;
import gwt.material.design.addins.client.fileuploader.events.TotalUploadProgressEvent;
import gwt.material.design.addins.client.richeditor.MaterialRichEditor;
import gwt.material.design.client.events.SearchFinishEvent;
import gwt.material.design.client.ui.*;
import gwt.material.design.client.ui.animate.MaterialAnimator;
import gwt.material.design.client.ui.animate.Transition;
import gwt.material.design.client.ui.html.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductServiceViewImpl extends Composite implements ProductServiceView {

    private Presenter presenter;

    interface ServicesViewUiBinder extends UiBinder<Widget, ProductServiceViewImpl> {
    }

    private static ServicesViewUiBinder ourUiBinder = GWT.create(ServicesViewUiBinder.class);

    @UiField
    MaterialTitle title;
    @UiField
    MaterialTextBox name;
    @UiField
    MaterialTextBox email;
    @UiField
    MaterialTextBox website;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialTextArea description;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialRichEditor fullDescription;
    @UiField
    ProductTextBox product;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialTextBox apiURL;
    @UiField
    MaterialPanel geoinformation;
    @UiField
    MapContainer mapContainer;
    @UiField
    MaterialLabel samplesMessage;
    @UiField
    MaterialRow samples;
    @UiField
    MaterialListBox sampleAccessType;
    @UiField
    MaterialFileUploader sampleUploader;
    @UiField
    MaterialLabel dataAccessMessage;
    @UiField
    MaterialRow dataAccess;
    @UiField
    MaterialButton uploadSampleButton;
    @UiField
    MaterialLabel uploadSampleTitle;
    @UiField
    MaterialLabel uploadSampleComment;
    @UiField
    MaterialPanel performances;
    @UiField
    MaterialTextBox geoinformationComment;
    @UiField
    MaterialTextBox performancesComment;
    @UiField
    MaterialPanel productPanel;
    @UiField
    MaterialTextBox disseminationComment;
    @UiField
    MaterialTextBox deliveryTime;

    public ProductServiceViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        product.addSearchFinishHandler(new SearchFinishEvent.SearchFinishHandler() {
            @Override
            public void onSearchFinish(SearchFinishEvent event) {
                presenter.productChanged();
            }
        });

        for(AccessType accessType : new AccessType[] {AccessType.file, AccessType.ogc}) {
            Option optionWidget = new Option();
            optionWidget.setText(accessType.getName());
            optionWidget.setValue(accessType.toString());
            sampleAccessType.add(optionWidget);
        }
        // quirk to make sure the list box is initialised
        sampleAccessType.setEnabled(true);

        // configure the sample uploader
        final String uploadUrl = GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/", "") + "upload/datasets/";
        sampleUploader.setUrl(uploadUrl);

        // Added the progress to card uploader
        sampleUploader.addTotalUploadProgressHandler(new TotalUploadProgressEvent.TotalUploadProgressHandler() {
            @Override
            public void onTotalUploadProgress(TotalUploadProgressEvent event) {
            }
        });

        sampleUploader.addSuccessHandler(new SuccessEvent.SuccessHandler<UploadFile>() {
            @Override
            public void onSuccess(SuccessEvent<UploadFile> event) {
                String error = StringUtils.extract(event.getResponse().getMessage(), "<error>", "</error>");
                if(error.length() > 0) {
                    Window.alert(error);
                    return;
                }
                String response = StringUtils.extract(event.getResponse().getMessage(), "<value>", "</value>");
                JSONObject sampleUploadDTOJson = JSONParser.parseLenient(response).isObject();
                SampleUploadDTO sampleUploadDTO = new SampleUploadDTO();
                sampleUploadDTO.setFileUri(sampleUploadDTOJson.containsKey("fileUri") ? sampleUploadDTOJson.get("fileUri").isString().stringValue() : null);
                sampleUploadDTO.setLayerName(sampleUploadDTOJson.containsKey("layerName") ? sampleUploadDTOJson.get("layerName").isString().stringValue() : null);
                sampleUploadDTO.setServer(sampleUploadDTOJson.containsKey("server") ? sampleUploadDTOJson.get("server").isString().stringValue() : null);
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
                    datasetAccessOGC.setHostedData(false);
                    addSample(datasetAccessOGC);
                }
            }
        });

        sampleUploader.addDragOverHandler(new DragOverEvent.DragOverHandler() {
            @Override
            public void onDragOver(DragOverEvent event) {
                MaterialAnimator.animate(Transition.RUBBERBAND, sampleUploader, 0);
            }
        });

        // add data access
        dataAccess.clear();
        for (AccessType accessType : AccessType.values()) {
            MaterialCheckBox materialCheckBox = new MaterialCheckBox(accessType.getName());
            materialCheckBox.setObject(accessType);
            dataAccess.add(materialCheckBox);
        }

        template.setPlace("productservices");
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
    public HasText getEmail() {
        return email;
    }

    @Override
    public HasText getWebsite() {
        return website;
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
        this.fullDescription.setHTML(fullDescription == null ? "" : fullDescription);
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        mapContainer.setMapLoadedHandler(mapLoadedHandler);
    }

    @Override
    public String getIconUrl() {
        return imageUploader.getImageUrl();
    }

    @Override
    public void setIconUrl(String iconURL) {
        imageUploader.setImageUrl(iconURL);
    }

    @Override
    public ProductDTO getSelectedProduct() {
        return product.getProduct();
    }

    @Override
    public void setSelectedProduct(ProductDTO productDTO) {
        product.setProduct(productDTO);
        productPanel.setVisible(productDTO != null);
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
    public HasText getDisseminationComment() {
        return disseminationComment;
    }

    @Override
    public HasText getDeliveryTime() {
        return deliveryTime;
    }

    @Override
    public void setExtent(AoIDTO extent) {
        mapContainer.displayAoI(extent);
    }

    @Override
    public AoIDTO getExtent() {
        return mapContainer.getAoi();
    }

    @Override
    public HasText getAPIUrl() {
        return apiURL;
    }

    @Override
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void setSelectedDataAccessTypes(List<AccessType> selectedAccessTypes) {
        if(selectedAccessTypes == null) selectedAccessTypes = new ArrayList<>();
        for(int index = 0; index < dataAccess.getWidgetCount(); index++) {
            Widget widget = dataAccess.getWidget(index);
            if(widget instanceof MaterialCheckBox) {
                MaterialCheckBox materialCheckBox = (MaterialCheckBox) widget;
                materialCheckBox.setValue(selectedAccessTypes.contains((AccessType) materialCheckBox.getObject()));
            }
        }
        updateDataAccessTypesMessage();
    }

    private void updateDataAccessTypesMessage() {
        List<AccessType> accessTypes = getSelectedDataAccessTypes();
        dataAccessMessage.setText(accessTypes.size() == 0 ? "No data access types supported" :
                accessTypes.size() + " data access types supported");
    }

    @Override
    public List<AccessType> getSelectedDataAccessTypes() {
        List<AccessType> selectedAccessTypes = new ArrayList<AccessType>();
        for(int index = 0; index < dataAccess.getWidgetCount(); index++) {
            Widget widget = dataAccess.getWidget(index);
            if(widget instanceof MaterialCheckBox) {
                MaterialCheckBox materialCheckBox = (MaterialCheckBox) widget;
                if(materialCheckBox.getValue()) {
                    selectedAccessTypes.add((AccessType) materialCheckBox.getObject());
                }
            }
        }
        return selectedAccessTypes;
    }

    @Override
    public void setSampleProductServiceId(Long serviceId) {
        uploadSampleTitle.setText("Upload sample files to our servers");
        if(serviceId == null) {
            uploadSampleComment.setText("Sorry, you need to SUBMIT before you can upload files to our servers");
            uploadSampleComment.setTextColor("orange");
            uploadSampleButton.setEnabled(false);
        } else {
            uploadSampleComment.setText("Allowed files are shapefiles (zipped), GeoTIFF and documents (PDF, doc, xls...)");
            uploadSampleComment.setTextColor("dark_grey");
            uploadSampleButton.setEnabled(true);
            sampleUploader.setParameter("resourceId", serviceId + "");
        }
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
        // check uri name
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
    void addSample(ClickEvent clickEvent) {
        AccessType selectedType = AccessType.valueOf(sampleAccessType.getValue());
        addSample(createDataAccess(selectedType, true));
        updateSamplesMessage();
    }

}