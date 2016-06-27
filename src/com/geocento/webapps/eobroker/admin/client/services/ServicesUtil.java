package com.geocento.webapps.eobroker.admin.client.services;

import com.geocento.webapps.eobroker.common.client.services.LoginService;
import com.google.gwt.core.client.GWT;

/**
 * Created by thomas on 03/06/2016.
 */
public class ServicesUtil {

    public static LoginService loginService = GWT.create(LoginService.class);

    public static AssetsService assetsService = GWT.create(AssetsService.class);

}
