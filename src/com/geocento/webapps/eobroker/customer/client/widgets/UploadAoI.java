package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialFileUploader;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.AoIWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import gwt.material.design.addins.client.fileuploader.base.UploadFile;
import gwt.material.design.addins.client.fileuploader.events.DragOverEvent;
import gwt.material.design.addins.client.fileuploader.events.ErrorEvent;
import gwt.material.design.addins.client.fileuploader.events.SuccessEvent;
import gwt.material.design.addins.client.fileuploader.events.TotalUploadProgressEvent;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialProgress;
import gwt.material.design.client.ui.animate.MaterialAnimator;
import gwt.material.design.client.ui.animate.Transition;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 15/11/2016.
 */
public class UploadAoI {

    interface UploadAoIUiBinder extends UiBinder<MaterialModal, UploadAoI> {
    }

    private static UploadAoIUiBinder ourUiBinder = GWT.create(UploadAoIUiBinder.class);

    public static interface Presenter {
        void aoiSelected(AoIDTO aoIDTO);
    }

    @UiField
    MaterialFileUploader uploadAoI;
    @UiField
    MaterialLabel iconName;
    @UiField
    MaterialLabel iconSize;
    @UiField
    MaterialProgress iconProgress;
    @UiField
    MaterialPanel listOfAoIs;

    private final MaterialModal materialModal;

    private static UploadAoI instance = null;

    private Presenter presenter;

    public UploadAoI() {

        materialModal = ourUiBinder.createAndBindUi(this);

        final String uploadUrl = GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/", "") + "upload/geometry/";
        uploadAoI.setUrl(uploadUrl);
        // Added the progress to card uploader
        uploadAoI.addTotalUploadProgressHandler(new TotalUploadProgressEvent.TotalUploadProgressHandler() {
            @Override
            public void onTotalUploadProgress(TotalUploadProgressEvent event) {
                iconProgress.setPercent(event.getProgress());
            }
        });

        uploadAoI.addSuccessHandler(new SuccessEvent.SuccessHandler<UploadFile>() {
            @Override
            public void onSuccess(SuccessEvent<UploadFile> event) {
                iconName.setText(event.getTarget().getName());
                iconSize.setText(event.getTarget().getType());
                String response = StringUtils.extract(event.getResponse().getMessage(), "<value>", "</value>");
                AoIDTO aoIDTO = new AoIDTO();
                JSONObject aoiJson = JSONParser.parseLenient(response).isObject();
                aoIDTO.setId((long) aoiJson.get("id").isNumber().doubleValue());
                aoIDTO.setName(aoiJson.get("name").isString().stringValue());
                aoIDTO.setWktGeometry(aoiJson.get("wktGeometry").isString().stringValue());
                presenter.aoiSelected(aoIDTO);
            }
        });

        uploadAoI.addErrorHandler(new ErrorEvent.ErrorHandler<UploadFile>() {
            @Override
            public void onError(ErrorEvent<UploadFile> event) {
                Window.alert("Error loading file, message is " + event.getResponse());
            }
        });

        uploadAoI.addDragOverHandler(new DragOverEvent.DragOverHandler() {
            @Override
            public void onDragOver(DragOverEvent event) {
                MaterialAnimator.animate(Transition.RUBBERBAND, uploadAoI, 0);
            }
        });

        // add to document
        RootPanel.get().add(materialModal);

    }

    public static UploadAoI getInstance() {
        if(instance == null) {
            instance = new UploadAoI();
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
                            aoIWidget.getSelect().addClickHandler(new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent event) {
                                    presenter.aoiSelected(aoIDTO);
                                    hide();
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

    private void hide() {
        materialModal.closeModal();
    }

}