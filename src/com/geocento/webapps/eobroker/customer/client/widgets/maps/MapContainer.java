package com.geocento.webapps.eobroker.customer.client.widgets.maps;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.customer.client.widgets.UploadAoI;

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
            public void aoiChanged(AoIDTO aoi) {
                presenter.aoiChanged(aoi);
            }

            @Override
            public void selectAoI() {
                UploadAoI.getInstance().display(new UploadAoI.Presenter() {
                    @Override
                    public void aoiSelected(AoIDTO aoIDTO) {
                        displayAoI(aoIDTO);
                        presenter.aoiSelected(aoIDTO);
                    }
                });
            }
        });
    }
}
