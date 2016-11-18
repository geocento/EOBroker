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
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.DragOverEvent;
import gwt.material.design.addins.client.fileuploader.events.SuccessEvent;
import gwt.material.design.addins.client.fileuploader.events.TotalUploadProgressEvent;
import gwt.material.design.addins.client.richeditor.MaterialRichEditor;
import gwt.material.design.client.base.SearchObject;
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
public class ProductDatasetViewImpl extends Composite implements ProductDatasetView {

    private Presenter presenter;

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
    MaterialSearch product;
    @UiField
    MaterialTextArea description;
    @UiField
    MaterialRichEditor fullDescription;
    @UiField
    MapContainer mapContainer;
    @UiField
    MaterialListBox serviceType;
    @UiField
    MaterialRow dataAccess;
    @UiField
    MaterialButton addDataAccess;
    @UiField
    MaterialLabel dataAccessMessage;
    @UiField
    MaterialButton addHostedSample;
    @UiField
    MaterialFileUploader sampleUploader;
    @UiField
    MaterialListBox sampleAccessType;
    @UiField
    MaterialTextBox resourceName;
    @UiField
    MaterialRow samples;
    @UiField
    MaterialLabel samplesMessage;
    @UiField
    MaterialListBox datasetAccessType;
    @UiField
    MaterialPanel geoinformation;

    public ProductDatasetViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        for(ServiceType serviceType : ServiceType.values()) {
            this.serviceType.addItem(serviceType.getName(), serviceType.toString());
        }

        product.addSearchFinishHandler(new SearchFinishEvent.SearchFinishHandler() {
            @Override
            public void onSearchFinish(SearchFinishEvent event) {
                presenter.productChanged();
            }
        });

        for(AccessType accessType : AccessType.values()) {
            Option optionWidget = new Option();
            optionWidget.setText(accessType.getName());
            optionWidget.setValue(accessType.toString());
            datasetAccessType.add(optionWidget);
        }

        for(AccessType accessType : new AccessType[] {AccessType.file, AccessType.ogc}) {
            Option optionWidget = new Option();
            optionWidget.setText(accessType.getName());
            optionWidget.setValue(accessType.toString());
            sampleAccessType.add(optionWidget);
        }

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
                String uri = StringUtils.extract(event.getResponse().getMessage(), "<value>", "</value>");
            }
        });

        sampleUploader.addDragOverHandler(new DragOverEvent.DragOverHandler() {
            @Override
            public void onDragOver(DragOverEvent event) {
                MaterialAnimator.animate(Transition.RUBBERBAND, sampleUploader, 0);
            }
        });

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
        this.serviceType.setSelectedValue(serviceType == null ? ServiceType.commercial.toString() : serviceType.toString());
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.valueOf(serviceType.getSelectedValue());
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
        return (ProductDTO) product.getSelectedObject().getO();
    }

    @Override
    public void setSelectedProduct(ProductDTO productDTO) {
        product.setSelectedObject(productDTO == null ? null : new SearchObject(productDTO.getName(), "", productDTO));
        product.setText(productDTO == null ? "" : productDTO.getName());
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
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
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        this.dataAccess.add(materialColumn);
        DataAccessWidget dataAccessWidget = new DataAccessWidget(datasetAccess);
        materialColumn.add(dataAccessWidget);
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
            dataAccesses.add(((DataAccessWidget)((MaterialColumn) dataAccess.getWidget(index)).getWidget(0)).getDatasetAccess());
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
        MaterialColumn materialColumn = new MaterialColumn(12, 12, 12);
        this.samples.add(materialColumn);
        DataAccessWidget dataAccessWidget = new DataAccessWidget(datasetAccess);
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
            dataAccesses.add(((DataAccessWidget)((MaterialColumn) samples.getWidget(index)).getWidget(0)).getDatasetAccess());
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

    @UiHandler("addDataAccess")
    void addDataAccess(ClickEvent clickEvent) {
        AccessType selectedType = AccessType.valueOf(datasetAccessType.getValue());
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
        addDataAccess(datasetAccess);
        updateDataAccessMessage();
    }

    @UiHandler("addHostedSample")
    void addSample(ClickEvent clickEvent) {
        addSample(new DatasetAccess());
        updateSamplesMessage();
    }

}