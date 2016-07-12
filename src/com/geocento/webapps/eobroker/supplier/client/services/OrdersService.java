package com.geocento.webapps.eobroker.supplier.client.services;

import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceSupplierRequestDTO;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

public interface OrdersService extends DirectRestService {

    @GET
    @Path("/orders/{id}")
    @Produces("application/json")
    public ProductServiceSupplierRequestDTO getRequest(@PathParam("id") String id) throws RequestException;

}
