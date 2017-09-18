package com.geocento.webapps.eobroker.customer.client.services;

import com.google.gwt.http.client.RequestException;

import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * Created by thomas on 20/06/2016.
 */
public interface LoginService extends com.geocento.webapps.eobroker.common.client.services.LoginService {

    @PUT
    @Path("/pwdreset")
    @Produces("application/json")
    void resetPassword(@QueryParam("username") String username) throws RequestException;

    @PUT
    @Path("/pwdchange")
    @Produces("application/json")
    void changePassword(@QueryParam("username") String accountName, @QueryParam("resetToken") String resetToken, @QueryParam("password") String password) throws RequestException;

}
