package com.geocento.webapps.eobroker.customer.client.widgets.maps;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.widgets.UploadAoI;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

/**
 * Created by thomas on 17/11/2016.
 */
public class MapContainer extends com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer {

    public static interface Presenter {
        void aoiChanged(AoIDTO aoi);
        void aoiSelected(AoIDTO aoi);
    }

    public void setPresenter(final Presenter presenter) {
        super.setPresenter(new com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer.Presenter() {
            @Override
            public void aoiChanged(final AoIDTO aoi) {
                if(aoi == null) {
                    presenter.aoiChanged(aoi);
                } else {
                    displayLoading();
                    // untick the filtering
                    try {
                        REST.withCallback(new MethodCallback<AoIDTO>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                hideLoading();
                                presenter.aoiChanged(aoi);
                            }

                            @Override
                            public void onSuccess(Method method, AoIDTO aoIDTO) {
                                hideLoading();
                                presenter.aoiChanged(aoIDTO);
                            }
                        }).call(ServicesUtil.assetsService).updateAoI(aoi);
                    } catch (Exception e) {

                    }
                }
            }

            @Override
            public void selectAoI() {
                UploadAoI.getInstance().display(new UploadAoI.Presenter() {
                    @Override
                    public void aoiSelected(AoIDTO aoIDTO) {
                        displayAoI(aoIDTO);
                        centerOnAoI();
                        presenter.aoiSelected(aoIDTO);
                    }
                });
            }
        });
    }

}
