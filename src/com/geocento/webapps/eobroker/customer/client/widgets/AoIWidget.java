package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.*;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

/**
 * Created by thomas on 08/11/2016.
 */
public class AoIWidget extends Composite {

    interface AoIWidgetUiBinder extends UiBinder<Widget, AoIWidget> {
    }

    private static AoIWidgetUiBinder ourUiBinder = GWT.create(AoIWidgetUiBinder.class);

    @UiField
    MaterialButton select;
    @UiField
    MaterialLabel name;
    @UiField
    MaterialButton edit;
    @UiField
    MaterialPanel editPanel;
    @UiField
    MaterialPanel selectPanel;
    @UiField
    MaterialTextBox editName;
    @UiField
    MaterialButton delete;

    private final AoIDTO aoi;

    public AoIWidget(AoIDTO aoIDTO) {
        this.aoi = aoIDTO;
        initWidget(ourUiBinder.createAndBindUi(this));
        updateName();
    }

    public AoIDTO getAoI() {
        return aoi;
    }

    private void updateName() {
        String name = aoi.getName();
        name = name == null || name.length() == 0 ? "No name" : name;
        this.name.setText(name);
        editName.setText(name);
    }

    public HasClickHandlers getSelect() {
        return select;
    }

    public void setEditing(boolean editing) {
        selectPanel.setVisible(!editing);
        editPanel.setVisible(editing);

    }

    @UiHandler("edit")
    void edit(ClickEvent clickEvent) {
        setEditing(true);
    }

    @UiHandler("validate")
    void validate(ClickEvent clickEvent) {
        String name = editName.getText();
        if(name.length() == 0) {
            MaterialToast.fireToast("Please provide a valid name", "red");
            return;
        }
        // update aoi
        aoi.setName(name);
        setEditing(false);
        MaterialToast.fireToast("Saving AoI...");
        try {
            REST.withCallback(new MethodCallback<Void>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    setEditing(true);
                    MaterialToast.fireToast("Could not save AoI, please retry", "red");
                }

                @Override
                public void onSuccess(Method method, Void response) {
                    MaterialToast.fireToast("AoI saved", "green");
                    updateName();
                }
            }).call(ServicesUtil.assetsService).updateAoIName(aoi.getId(), name);
        } catch (Exception e) {

        }
    }

    public HasClickHandlers getDelete() {
        return delete;
    }

}