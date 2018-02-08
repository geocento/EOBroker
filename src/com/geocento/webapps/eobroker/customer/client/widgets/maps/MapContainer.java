package com.geocento.webapps.eobroker.customer.client.widgets.maps;

import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialMessage;
import com.geocento.webapps.eobroker.common.client.widgets.WidgetUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.ExtentJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.WMSLayerInfoJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.WMSLayerJSNI;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.geocento.webapps.eobroker.common.shared.entities.Extent;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.widgets.LayerWidget;
import com.geocento.webapps.eobroker.customer.client.widgets.SelectAoI;
import com.geocento.webapps.eobroker.customer.client.widgets.SelectLayers;
import com.geocento.webapps.eobroker.customer.shared.LayerInfoDTO;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 17/11/2016.
 */
public class MapContainer extends com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer {

    public static interface Presenter extends com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer.Presenter {
    }

    private Presenter presenter;

    private final MaterialButton saveButton;

    private MaterialButton addLayerButton;
    private final MaterialPanel layersList;

    static class LayerSettings {
        DatasetAccessOGC datasetAccessOGC;
        LayerInfoDTO layerInfoDTO;
        WMSLayerJSNI layer;
        boolean activated;
        int transparency;
    }
    private List<LayerSettings> selectedLayers;

    public MapContainer() {
        // add select and save buttons
        // add save buttom
        saveButton = new MaterialButton();
        saveButton.setBackgroundColor(Color.GREEN);
        saveButton.setIconType(IconType.SAVE);
        addButton(saveButton, "Save the current AoI");
        saveButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if(aoi != null) {
                    if (aoi.getName() == null) {
                        QueryValueModal.getInstance().getValue("AoI Name", "Please provide a name for your AoI", "", new QueryValueModal.Presenter() {
                            @Override
                            public void onValue(String value) {
                                aoi.setName(value);
                                saveAoI();
                            }

                            @Override
                            public void onCancel() {

                            }
                        });
                    } else {
                        saveAoI();
                    }
                } else {
                    try {
                        REST.withCallback(new MethodCallback<Void>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                saveButton.setVisible(true);
                                MaterialToast.fireToast("Could not save AoI, please retry", Color.RED.getCssName());
                            }

                            @Override
                            public void onSuccess(Method method, Void response) {
                                MaterialToast.fireToast("AoI saved", Color.GREEN.getCssName());
                                presenter.aoiChanged(null);
                            }
                        }).call(ServicesUtil.assetsService).removeLatestAoI();
                    } catch (Exception e) {

                    }
                }
            }
        });
        saveButton.setVisible(false);

        // add select buttom
        // problem with rendering, need to have it in the uibinder file
/*
        MaterialAnchorButton selectButton = new MaterialAnchorButton();
        selectButton.setLayoutPosition(Style.Position.ABSOLUTE);
        selectButton.setBackgroundColor(Color.GREEN);
        selectButton.setIconType(IconType.FILTER);
        addFABButton(selectButton, "Select a saved AoI");
*/
        selectButton.setVisible(true);
        selectButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                SelectAoI.getInstance().display(new SelectAoI.Presenter() {
                    @Override
                    public void aoiSelected(AoIDTO aoIDTO) {
                        displayAoI(aoIDTO);
                        centerOnAoI();
                        presenter.aoiChanged(aoIDTO);
                    }
                });
            }
        });

        MaterialPanel addLayerPanel = new MaterialPanel();
        addLayerButton = new MaterialButton();
        addLayerButton.setTruncate(true);
        addLayerButton.setActivates("layersList");
        addLayerButton.setBackgroundColor(Color.WHITE);
        addLayerButton.setTextColor(Color.BLACK);
        addLayerButton.setIconType(IconType.LAYERS);
        addLayerButton.setIconSize(IconSize.SMALL);
        addLayerButton.setIconColor(Color.BLACK);
        addLayerButton.setType(ButtonType.FLOATING);
        addLayerButton.setMarginRight(10);
        addLayerButton.addStyleName(style.fabButton());
        MaterialTooltip materialTooltip = new MaterialTooltip(addLayerButton);
        materialTooltip.setText("Select map layers");
        materialTooltip.setPosition(Position.TOP);
        addLayerPanel.add(addLayerButton);
        addLayerPanel.setPaddingBottom(10);
        addLayerPanel.setPaddingLeft(10);
        addControl(addLayerPanel, Position.BOTTOM, Position.LEFT);
        MaterialDropDown layersListPanel = new MaterialDropDown();
        layersListPanel.setPadding(5);
        layersListPanel.setBackgroundColor(Color.WHITE);
        layersListPanel.setActivator("layersList");
        layersListPanel.setConstrainWidth(false);
        layersList = new MaterialPanel();
        layersListPanel.add(layersList);
        layersList.add(new LoadingWidget("Loading layers..."));
        MaterialLink addNewLayerButton = new MaterialLink("Add new layer");
        addNewLayerButton.setIconType(IconType.ADD);
        addNewLayerButton.setBackgroundColor(Color.WHITE);
        addNewLayerButton.setTextColor(Color.GREY_DARKEN_3);
        addNewLayerButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // open the layer selection window
                SelectLayers.getInstance().display(new SelectLayers.Presenter() {
                    @Override
                    public void layerSelected(DatasetAccessOGC layer) {
                        try {
                            REST.withCallback(new MethodCallback<Void>() {

                                @Override
                                public void onFailure(Method method, Throwable exception) {
                                    MaterialToast.fireToast("Could not add selected layer...");
                                }

                                @Override
                                public void onSuccess(Method method, Void result) {
                                    LayerSettings layerSettings = new LayerSettings();
                                    layerSettings.datasetAccessOGC = layer;
                                    addLayerWidget(layerSettings);
                                }
                            }).call(ServicesUtil.assetsService).addSelectedLayer(layer.getId());
                        } catch (RequestException e) {
                        }
                    }
                });
            }
        });
        layersListPanel.add(addNewLayerButton);
        addWidget(layersListPanel);
        // disable by default
        setLayersEnabled(false);
    }

    private void saveAoI() {
        saveButton.setVisible(false);
        MaterialToast.fireToast("Saving AoI...");
        try {
            REST.withCallback(new MethodCallback<AoIDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    saveButton.setVisible(true);
                    MaterialToast.fireToast("Could not save AoI, please retry", Color.RED.getCssName());
                }

                @Override
                public void onSuccess(Method method, AoIDTO aoIDTO) {
                    MaterialToast.fireToast("AoI saved", Color.GREEN.getCssName());
                    presenter.aoiChanged(aoIDTO);
                }
            }).call(ServicesUtil.assetsService).updateAoI(aoi);
        } catch (Exception e) {

        }
    }

    public void setLayersEnabled(boolean enabled) {
        addLayerButton.setVisible(enabled);
        if(enabled) {
            loadSelectedLayers();
        }
    }

    private void loadSelectedLayers() {
        // check if we have selected layers
        if(selectedLayers == null) {
            try {
                REST.withCallback(new MethodCallback<List<DatasetAccessOGC>>() {

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        layersList.clear();
                        MaterialMessage materialMessage = new MaterialMessage();
                        materialMessage.displayErrorMessage("Could not load selected layers...");
                        layersList.add(materialMessage);
                    }

                    @Override
                    public void onSuccess(Method method, List<DatasetAccessOGC> selectedLayers) {
                        setSelectedLayers(selectedLayers);
                    }
                }).call(ServicesUtil.assetsService).getSelectedLayers();
            } catch (Exception e) {
            }
        } else {
            updateLayersDisplay();
        }
    }

    private void setSelectedLayers(List<DatasetAccessOGC> selectedLayers) {

        this.selectedLayers = new ArrayList<LayerSettings>();
        for(DatasetAccessOGC datasetAccessOGC : selectedLayers) {
            LayerSettings layerSettings = new LayerSettings();
            layerSettings.datasetAccessOGC = datasetAccessOGC;
            this.selectedLayers.add(layerSettings);
        }
        updateLayersDisplay();
    }

    private void updateLayersDisplay() {
        layersList.clear();
        if (this.selectedLayers.size() > 0) {
            for (LayerSettings layerSettings : this.selectedLayers) {
                addLayerWidget(layerSettings);
            }
            addLayerButton.setText("(" + this.selectedLayers.size() + ")");
        } else {
            MaterialLabel materialLabel = new MaterialLabel("No layers selected...");
            materialLabel.setMarginTop(10);
            materialLabel.setMarginBottom(20);
            layersList.add(materialLabel);
            addLayerButton.setText("");
        }
        updateLayersMapDisplay();
    }

    private void addLayerWidget(LayerSettings layerSettings) {
        LayerWidget layerWidget = new LayerWidget(layerSettings.datasetAccessOGC);
        layerWidget.getSelection().setValue(layerSettings.activated);
        layersList.add(layerWidget);
        layerWidget.addDomHandler(DomEvent::stopPropagation, ClickEvent.getType());
        //layerWidget.stopTouchStartEvent();
        layerWidget.getSelectionHandler().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                layerSettings.activated = event.getValue();
                updateLayersMapDisplay();
            }
        });
        layerWidget.getExtentButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Extent extent = layerSettings.layerInfoDTO.getExtent();
                ExtentJSNI extentJSNI = MapJSNI.createExtent(extent.getWest(), extent.getSouth(), extent.getEast(), extent.getNorth());
                map.setExtent(extentJSNI);
            }
        });
    }

    private void updateLayersMapDisplay() {
        // update map and widget display based on layer settings values
        for(LayerSettings layerSettings : selectedLayers) {
            LayerWidget layerWidget = (LayerWidget) WidgetUtil.findChild(layersList, new WidgetUtil.CheckValue() {
                @Override
                public boolean isValue(Widget widget) {
                    return widget instanceof LayerWidget && ((LayerWidget) widget).getDatasetAccessOGC() == layerSettings.datasetAccessOGC;
                }
            });
            layerWidget.getSelection().setValue(layerSettings.activated);
            layerWidget.enableExtent(layerSettings.activated);
            if(layerSettings.activated) {
                if (layerSettings.layerInfoDTO == null) {
                    // find layer widget
                    try {
                        REST.withCallback(new MethodCallback<LayerInfoDTO>() {

                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                layerWidget.setLoading(false);
                                MaterialToast.fireToast("Could not load layer...");
                            }

                            @Override
                            public void onSuccess(Method method, LayerInfoDTO layerInfoDTO) {
                                layerWidget.setLoading(false);
                                layerSettings.layerInfoDTO = layerInfoDTO;
                                addLayer(layerSettings);
                            }
                        }).call(ServicesUtil.assetsService).getLayerInfo(layerSettings.datasetAccessOGC.getId());
                    } catch (RequestException e) {
                    }
                } else {
                    addLayer(layerSettings);
                }
            } else {
                removeLayer(layerSettings);
            }
        }
    }

    private void removeLayer(LayerSettings layerSettings) {
        if(layerSettings.layer != null) {
            map.removeWMSLayer(layerSettings.layer);
            layerSettings.layer = null;
        }
    }

    private void addLayer(LayerSettings layerSettings) {
        LayerInfoDTO layerInfoDTO = layerSettings.layerInfoDTO;
        if(layerSettings.layer == null) {
            Extent extent = layerInfoDTO.getExtent();
            ExtentJSNI extentJSNI = MapJSNI.createExtent(extent.getWest(), extent.getSouth(), extent.getEast(), extent.getNorth());
            WMSLayerJSNI layer = map.addWMSLayer(layerInfoDTO.getServerUrl(),
                    WMSLayerInfoJSNI.createInfo(layerInfoDTO.getLayerName(), layerInfoDTO.getLayerName()),
                    extentJSNI, layerInfoDTO.getStyleName());
            layerSettings.layer = layer;
        }
    }

    public void setPresenter(final Presenter presenter) {
        this.presenter = presenter;
        super.setPresenter(new com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer.Presenter() {
            @Override
            public void aoiChanged(final AoIDTO aoi) {
                // enable save button if an aoi has changed
                // disable save button if aoi has been removed
                saveButton.setVisible(aoi != null);
                presenter.aoiChanged(aoi);
            }
        });
    }

}
