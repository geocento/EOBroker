package com.geocento.webapps.eobroker.customer.client.widgets.maps;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.widgets.SelectAoI;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialToast;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

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
