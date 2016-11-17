package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.ProgressButton;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.shared.entities.ImageService;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.widgets.UploadAoI;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.ProgressType;
import gwt.material.design.client.ui.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class RequestImageryViewImpl extends Composite implements RequestImageryView {

    private Presenter presenter;

    private boolean mapRevealed = true;

    interface RequestImageryUiBinder extends UiBinder<Widget, RequestImageryViewImpl> {
    }

    private static RequestImageryUiBinder ourUiBinder = GWT.create(RequestImageryUiBinder.class);

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MapContainer mapContainer;
    @UiField
    HTMLPanel suppliers;
    @UiField
    MaterialTextBox imagetype;
    @UiField
    MaterialListBox application;
    @UiField
    ProgressButton submit;
    @UiField
    MaterialDatePicker start;
    @UiField
    MaterialDatePicker stop;
    @UiField
    MaterialTextArea information;

    public MapJSNI map;

    public RequestImageryViewImpl(final ClientFactoryImpl clientFactory) {
        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        this.application.setPlaceholder("Select your application");
        String[] applications = new String[] {
                "agriculture",
                "forestry",
                "planning and development",
                "natural resources and exploration",
                "emergency response and crisis management",
                "tourism",
                "insurance",
                "health",
                "maritime and coastal",
                "defence and homeland security",
                "telecommunications",
                "mapping and topography",
                "other"
        };
        for(String application : applications) {
            this.application.addItem(application);
        }
        mapContainer.setHeight("100%");
        mapContainer.setPresenter(new MapContainer.Presenter() {
            @Override
            public void aoiChanged(AoIDTO aoi) {
                presenter.aoiChanged(aoi);
            }

            @Override
            public void selectAoI() {
                UploadAoI.getInstance().display(new UploadAoI.Presenter() {
                    @Override
                    public void aoiSelected(AoIDTO aoIDTO) {
                    }
                });
            }
        });
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        mapContainer.setMapLoadedHandler(mapLoadedHandler);
    }

    @Override
    public void displayAoI(AoIDTO aoi) {
        mapContainer.displayAoI(aoi);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displaySearchError(String message) {
        MaterialToast.fireToast(message);
    }

    @Override
    public void setDescription(String description) {
    }

    @Override
    public void setSuppliers(List<ImageService> imageServices) {
        suppliers.clear();
        for(final ImageService imageService : imageServices) {
/*
            MaterialPanel materialPanel = new MaterialPanel();
            MaterialLabelIcon labelIcon = new MaterialLabelIcon();
            labelIcon.setImageHeight("16px");
            labelIcon.setImageUrl(imageService.getCompany().getIconURL());
            labelIcon.setText("by " + imageService.getCompany().getName());
            labelIcon.getElement().getStyle().setFloat(Style.Float.RIGHT);
            labelIcon.addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    Window.open("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(Utils.generateTokens(FullViewPlace.TOKENS.companyid.toString(), imageService.getCompany().getId() + ""))), "_blank", null);
                }
            }, ClickEvent.getType());
            labelIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            materialPanel.add(labelIcon);
            MaterialCheckBox materialCheckBox = new MaterialCheckBox();
            materialCheckBox.setText(imageService.getName());
            materialCheckBox.setObject(imageService);
            materialPanel.add(materialCheckBox);
            suppliers.add(materialPanel);
*/
            MaterialCheckBox materialCheckBox = new MaterialCheckBox("<span style='display: inline;'><b>" + imageService.getName() + "</b> " +
                    "by <img style='max-height: 24px; vertical-align: middle; margin: 0px 5px;' src='" + imageService.getCompany().getIconURL() + "'/> <b>" + imageService.getCompany().getName() + "</b></span>", true);
            materialCheckBox.setObject(imageService);
            suppliers.add(materialCheckBox);
        }
    }

    @Override
    public HasClickHandlers getSubmitButton() {
        return submit;
    }

    @Override
    public String getImageType() {
        return imagetype.getText();
    }

    @Override
    public Date getStartDate() {
        return start.getDate();
    }

    @Override
    public Date getStopDate() {
        return stop.getDate();
    }

    @Override
    public String getAdditionalInformation() {
        return information.getText();
    }

    @Override
    public List<ImageService> getSelectedServices() {
        List<ImageService> selectedServices = new ArrayList<ImageService>();
        for(int index = 0; index < suppliers.getWidgetCount(); index++) {
            Widget widget = suppliers.getWidget(index);
            if (widget instanceof MaterialCheckBox) {
                if (((MaterialCheckBox) widget).getValue()) {
                    selectedServices.add((ImageService) ((MaterialCheckBox) widget).getObject());
                }
            }
/*
            if(widget instanceof MaterialPanel) {
                widget = ((MaterialPanel) widget).getWidget(1);
                if (widget instanceof MaterialCheckBox) {
                    if (((MaterialCheckBox) widget).getValue()) {
                        selectedServices.add((ImageService) ((MaterialCheckBox) widget).getObject());
                    }
                }
            }
*/
        }
        return selectedServices;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void displaySubmitLoading(boolean display) {
        if(display) {
            template.displayLoading();
            submit.showProgress(ProgressType.INDETERMINATE);
        } else {
            template.hideLoading();
            submit.hideProgress();
        }
    }

    @Override
    public void displaySucces(String message) {
        template.displaySuccess(message);
    }

    @Override
    public String getApplication() {
        return application.getSelectedItemText();
    }

    @Override
    public void displayFormError(String message) {
        Window.alert(message);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}