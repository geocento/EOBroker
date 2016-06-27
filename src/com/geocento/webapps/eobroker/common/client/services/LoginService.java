package com.geocento.webapps.eobroker.common.client.services;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

public interface LoginService extends DirectRestService {

    @GET
    @Path("/signin")
    @Produces("application/json")
    public LoginInfo signin(@QueryParam("username") String userName, @QueryParam("password") String passwordHash);

    @GET
    @Path("/signout")
    @Produces("application/json")
    public void signout();

    @GET
    @Path("/session")
    @Produces("application/json")
    public LoginInfo getSession();
}
