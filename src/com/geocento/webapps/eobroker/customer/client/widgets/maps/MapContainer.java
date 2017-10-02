package com.geocento.webapps.eobroker.customer.client.widgets.maps;

import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialMessage;
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
import com.geocento.webapps.eobroker.customer.shared.LayerInfoDTO;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import gwt.material.design.client.constants.*;
import gwt.material.design.client.ui.*;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 17/11/2016.
 */
public class MapContainer extends com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer {

    public static interface Presenter extends com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer.Presenter {
    }

    private Presenter presenter;

    private final MaterialButton saveButton;

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

    public void enableLayers() {
        MaterialButton layerName = new MaterialButton();
        layerName.getElement().getStyle().setProperty("maxWidth", "30%");
        layerName.setTruncate(true);
        layerName.setActivates("layersList");
        layerName.setBackgroundColor(Color.WHITE);
        layerName.setText("No layer selected");
        layerName.setTextColor(Color.BLACK);
        layerName.setIconType(IconType.LAYERS);
        layerName.setIconSize(IconSize.SMALL);
        layerName.setIconPosition(IconPosition.RIGHT);
        layerName.setIconColor(Color.BLACK);
        addControl(layerName, Position.TOP, Position.RIGHT);
        MaterialDropDown layersList = new MaterialDropDown();
        layersList.setPadding(5);
        layersList.setBackgroundColor(Color.WHITE);
        layersList.setActivator("layersList");
        layersList.add(new LoadingWidget("Loading layers..."));
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
                    layersList.clear();
                    if(selectedLayers != null && selectedLayers.size() > 0) {
                        for (DatasetAccessOGC selectedLayer : selectedLayers) {
                            LayerWidget layerWidget = new LayerWidget(selectedLayer);
                            layersList.add(layerWidget);
                            layerWidget.getSelection().addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                                public WMSLayerJSNI layer;

                                @Override
                                public void onValueChange(ValueChangeEvent<Boolean> event) {
                                    layerWidget.setLoading(true);
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
                                                Extent extent = layerInfoDTO.getExtent();
                                                ExtentJSNI extentJSNI = MapJSNI.createExtent(extent.getWest(), extent.getSouth(), extent.getEast(), extent.getNorth());
                                                layer = map.addWMSLayer(layerInfoDTO.getServerUrl(),
                                                        WMSLayerInfoJSNI.createInfo(layerInfoDTO.getLayerName(), layerInfoDTO.getLayerName()),
                                                        extentJSNI, layerInfoDTO.getStyleName());
                                                //map.setExtent(extentJSNI);
                                            }
                                        }).call(ServicesUtil.assetsService).getLayerInfo(selectedLayer.getId());
                                    } catch (RequestException e) {
                                    }

                                }
                            });
                        }
                    } else {
                        MaterialLabel materialLabel = new MaterialLabel("No layers selected...");
                        materialLabel.setMarginTop(10);
                        materialLabel.setMarginBottom(20);
                        layersList.add(materialLabel);
                    }
                    MaterialAnchorButton addNewLayerButton = new MaterialAnchorButton("Add new layer");
                    addNewLayerButton.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent event) {

                        }
                    });
                    layersList.add(addNewLayerButton);
                }
            }).call(ServicesUtil.assetsService).getSelectedLayers();
        } catch (Exception e) {
        }
        addWidget(layersList);
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
