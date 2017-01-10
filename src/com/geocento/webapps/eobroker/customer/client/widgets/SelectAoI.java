package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.WidgetUtil;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialToast;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 15/11/2016.
 */
public class SelectAoI {

    interface SelectAoIUiBinder extends UiBinder<MaterialModal, SelectAoI> {
    }

    private static SelectAoIUiBinder ourUiBinder = GWT.create(SelectAoIUiBinder.class);

    public static interface Presenter {
        void aoiSelected(AoIDTO aoIDTO);
    }

    @UiField
    MaterialPanel listOfAoIs;

    private final MaterialModal materialModal;

    private static SelectAoI instance = null;

    private Presenter presenter;

    public SelectAoI() {

        materialModal = ourUiBinder.createAndBindUi(this);

        // add to document
        RootPanel.get().add(materialModal);

    }

    public static SelectAoI getInstance() {
        if(instance == null) {
            instance = new SelectAoI();
        }
        return instance;
    }

    // TODO - clean the dropzone?
    public void display(final Presenter presenter) {
        this.presenter = presenter;
        materialModal.openModal();
        listOfAoIs.clear();
        listOfAoIs.add(new LoadingWidget("Loading AoIs..."));
        // load user's AOIs
        try {
            REST.withCallback(new MethodCallback<List<AoIDTO>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    listOfAoIs.clear();
                    listOfAoIs.add(new MaterialLabel("Error loading AoIs"));
                }

                @Override
                public void onSuccess(Method method, List<AoIDTO> aoIDTOs) {
                    listOfAoIs.clear();
                    if(aoIDTOs.size() == 0) {
                        listOfAoIs.add(new MaterialLabel("No AoIs defined..."));
                    } else {
                        for(final AoIDTO aoIDTO : aoIDTOs) {
                            AoIWidget aoIWidget = new AoIWidget(aoIDTO);
                            aoIWidget.getDelete().addClickHandler(new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent event) {
                                    removeAoI(aoIDTO);
                                }
                            });
                            aoIWidget.getSelect().addClickHandler(new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent event) {
                                    selectAoI(aoIDTO);
                                }
                            });
                            listOfAoIs.add(aoIWidget);
                        }
                    }
                }
            }).call(ServicesUtil.assetsService).listAoIs();
        } catch (Exception e) {

        }
    }

    private void selectAoI(AoIDTO aoIDTO) {
        try {
            REST.withCallback(new MethodCallback<AoIDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    Window.alert("Failed to load AoI");
                }

                @Override
                public void onSuccess(Method method, AoIDTO aoIDTO) {
                    presenter.aoiSelected(aoIDTO);
                }
            }).call(ServicesUtil.assetsService).getAoI(aoIDTO.getId());
        } catch (Exception e) {
        }
        hide();
    }

    private void removeAoI(final AoIDTO aoIDTO) {
        MaterialToast.fireToast("Deleting AoI...");
        try {
            REST.withCallback(new MethodCallback<Void>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    MaterialToast.fireToast("Could not remove AoI, please retry", "red");
                }

                @Override
                public void onSuccess(Method method, Void response) {
                    MaterialToast.fireToast("AoI removed", "green");
                    WidgetUtil.removeWidgets(listOfAoIs, new WidgetUtil.CheckValue() {
                        @Override
                        public boolean isValue(Widget widget) {
                            return widget instanceof AoIWidget && ((AoIWidget) widget).getAoI() == aoIDTO;
                        }
                    });
                }
            }).call(ServicesUtil.assetsService).deleteAoI(aoIDTO.getId());
        } catch (Exception e) {

        }
    }

    private void hide() {
        materialModal.closeModal();
    }

}