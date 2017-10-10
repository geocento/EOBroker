package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialMessage;
import com.geocento.webapps.eobroker.common.client.widgets.WidgetUtil;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.ui.*;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 15/11/2016.
 */
public class SelectLayers {

    interface SelectAoIUiBinder extends UiBinder<MaterialModal, SelectLayers> {
    }

    private static SelectAoIUiBinder ourUiBinder = GWT.create(SelectAoIUiBinder.class);

    public static interface Presenter {
        void layerSelected(DatasetAccessOGC layer);
    }

    @UiField
    MaterialPanel savedLayers;
    @UiField
    MaterialPanel companyLayers;
    @UiField
    MaterialPanel brokerLayers;
    @UiField
    MaterialTab tab;

    private final MaterialModal materialModal;

    private boolean loadingSavedLayers;
    private boolean loadingCompanyLayers;
    private boolean loadingBrokerLayers;

    private static SelectLayers instance = null;

    private Presenter presenter;

    public SelectLayers() {

        materialModal = ourUiBinder.createAndBindUi(this);

        tab.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> event) {
                // load the corresponding layers
                int selectedTab = event.getSelectedItem();
                if(selectedTab == 0) {
                    loadSavedLayers();
                } else if(selectedTab == 1) {
                    loadCompanyLayers();
                } else if(selectedTab == 2) {
                    loadBrokerLayers();
                }
            }
        });

        // add to document
        RootPanel.get().add(materialModal);

    }

    private void loadBrokerLayers() {
        if(loadingBrokerLayers) {
            return;
        }
        loadingBrokerLayers = true;
        brokerLayers.clear();
        brokerLayers.add(new LoadingWidget("Loading EO Broker layers..."));
        try {
            REST.withCallback(new MethodCallback<List<DatasetAccessOGC>>() {

                @Override
                public void onFailure(Method method, Throwable exception) {
                    loadingBrokerLayers = false;
                    brokerLayers.clear();
                    MaterialMessage materialMessage = new MaterialMessage();
                    materialMessage.displayErrorMessage("Could not load EO Broker layers...");
                    brokerLayers.add(materialMessage);
                }

                @Override
                public void onSuccess(Method method, List<DatasetAccessOGC> layers) {
                    loadingBrokerLayers = false;
                    setBrokerLayers(layers);
                }
            }).call(ServicesUtil.assetsService).getApplicationLayers();
        } catch (Exception e) {
        }
    }

    private void loadCompanyLayers() {
        if(loadingCompanyLayers) {
            return;
        }
        loadingCompanyLayers = true;
        companyLayers.clear();
        companyLayers.add(new LoadingWidget("Loading company layers..."));
        try {
            REST.withCallback(new MethodCallback<List<DatasetAccessOGC>>() {

                @Override
                public void onFailure(Method method, Throwable exception) {
                    loadingCompanyLayers = false;
                    companyLayers.clear();
                    MaterialMessage materialMessage = new MaterialMessage();
                    materialMessage.displayErrorMessage("Could not load company layers...");
                    companyLayers.add(materialMessage);
                }

                @Override
                public void onSuccess(Method method, List<DatasetAccessOGC> savedLayers) {
                    loadingCompanyLayers = false;
                    setCompanyLayers(savedLayers);
                }
            }).call(ServicesUtil.assetsService).getCompanySavedLayers();
        } catch (Exception e) {
        }
    }

    private void loadSavedLayers() {
        if(loadingSavedLayers) {
            return;
        }
        loadingSavedLayers = true;
        savedLayers.clear();
        savedLayers.add(new LoadingWidget("Loading saved layers..."));
        try {
            REST.withCallback(new MethodCallback<List<DatasetAccessOGC>>() {

                @Override
                public void onFailure(Method method, Throwable exception) {
                    loadingSavedLayers = false;
                    savedLayers.clear();
                    MaterialMessage materialMessage = new MaterialMessage();
                    materialMessage.displayErrorMessage("Could not load saved layers...");
                    savedLayers.add(materialMessage);
                }

                @Override
                public void onSuccess(Method method, List<DatasetAccessOGC> savedLayers) {
                    loadingSavedLayers = false;
                    setSavedLayers(savedLayers);
                }
            }).call(ServicesUtil.assetsService).getSavedLayers();
        } catch (Exception e) {
        }
    }

    private void setSavedLayers(List<DatasetAccessOGC> savedLayers) {
        this.savedLayers.clear();
        if(savedLayers.size() == 0) {
            this.savedLayers.add(new MaterialLabel("No saved layer..."));
            return;
        }
        for(DatasetAccessOGC savedLayer : savedLayers) {
            SelectLayerWidget selectLayerWidget = new SelectLayerWidget(savedLayer);
            selectLayerWidget.enableDelete(true);
            selectLayerWidget.getDelete().addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    removeSavedLayer(savedLayer);
                }
            });
            selectLayerWidget.getSelect().addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    selectLayer(savedLayer);
                }
            });
            this.savedLayers.add(selectLayerWidget);
        }
    }

    private void setCompanyLayers(List<DatasetAccessOGC> companyLayers) {
        this.companyLayers.clear();
        if(companyLayers.size() == 0) {
            this.companyLayers.add(new MaterialLabel("No company layer..."));
            return;
        }
        for(DatasetAccessOGC companyLayer : companyLayers) {
            SelectLayerWidget selectLayerWidget = new SelectLayerWidget(companyLayer);
            selectLayerWidget.enableDelete(false);
            selectLayerWidget.getSelect().addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    selectLayer(companyLayer);
                }
            });
            this.companyLayers.add(selectLayerWidget);
        }
    }

    private void setBrokerLayers(List<DatasetAccessOGC> brokerLayers) {
        this.brokerLayers.clear();
        if(brokerLayers.size() == 0) {
            this.brokerLayers.add(new MaterialLabel("No EO Broker layer available..."));
            return;
        }
        for(DatasetAccessOGC brokerLayer : brokerLayers) {
            SelectLayerWidget selectLayerWidget = new SelectLayerWidget(brokerLayer);
            selectLayerWidget.enableDelete(false);
            selectLayerWidget.getSelect().addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    selectLayer(brokerLayer);
                }
            });
            this.brokerLayers.add(selectLayerWidget);
        }
    }

    public static SelectLayers getInstance() {
        if(instance == null) {
            instance = new SelectLayers();
        }
        return instance;
    }

    public void display(final Presenter presenter) {
        this.presenter = presenter;
        materialModal.open();
        tab.selectTab("saved");
        loadSavedLayers();
    }

    private void selectLayer(DatasetAccessOGC layer) {
        try {
            REST.withCallback(new MethodCallback<Void>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Failed to load AoI");
                }

                @Override
                public void onSuccess(Method method, Void result) {
                    presenter.layerSelected(layer);
                }
            }).call(ServicesUtil.assetsService).addSelectedLayer(layer.getId());
        } catch (Exception e) {
        }
        hide();
    }

    private void removeSavedLayer(DatasetAccessOGC layer) {
        MaterialToast.fireToast("Deleting layer...");
        try {
            REST.withCallback(new MethodCallback<Void>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    MaterialToast.fireToast("Could not remove saved layer, please retry", Color.RED.getCssName());
                }

                @Override
                public void onSuccess(Method method, Void response) {
                    MaterialToast.fireToast("Layer removed", Color.GREEN.getCssName());
                    WidgetUtil.removeWidgets(savedLayers, new WidgetUtil.CheckValue() {
                        @Override
                        public boolean isValue(Widget widget) {
                            return widget instanceof SelectLayerWidget && ((SelectLayerWidget) widget).getDatasetAccessOGC() == layer;
                        }
                    });
                }
            }).call(ServicesUtil.assetsService).removeSavedLayer(layer.getId());
        } catch (Exception e) {

        }
    }

    private void hide() {
        materialModal.close();
    }

}