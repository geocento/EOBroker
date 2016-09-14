package com.geocento.webapps.eobroker.supplier.client.services;

import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ImageryServiceRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ImagesRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceSupplierRequestDTO;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import java.util.List;

public interface OrdersService extends DirectRestService {

    @GET
    @Path("/orders/product/{id}")
    @Produces("application/json")
    public ProductServiceSupplierRequestDTO getProductRequest(@PathParam("id") String id) throws RequestException;

    @GET
    @Path("/orders/imagery/{id}")
    @Produces("application/json")
    ImageryServiceRequestDTO getImageryRequest(@PathParam("id") String id) throws RequestException;

    @GET
    @Path("/orders/images/{id}")
    @Produces("application/json")
    ImagesRequestDTO getImagesRequest(@PathParam("id") String id) throws RequestException;

    @GET
    @Path("/orders/")
    @Produces("application/json")
    List<RequestDTO> getRequests() throws RequestException;
}
