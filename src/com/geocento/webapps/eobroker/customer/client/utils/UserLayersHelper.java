package com.geocento.webapps.eobroker.customer.client.utils;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.geocento.webapps.eobroker.customer.shared.LayerInfoDTO;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 02/10/2017.
 */
public class UserLayersHelper {

    static private List<DatasetAccessOGC> userLayers = null;
    static private HashMap<DatasetAccessOGC, LayerInfoDTO> userSelectedLayers = null;

    static public void getUserLayers(AsyncCallback<List<DatasetAccessOGC>> callback) {
        if(userLayers == null) {

        }
    }

}
