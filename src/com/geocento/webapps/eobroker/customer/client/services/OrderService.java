package com.geocento.webapps.eobroker.customer.client.services;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.customer.shared.OrderDTO;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;
import java.util.List;

public interface OrderService extends DirectRestService {

    @GET
    @Path("/orders/{id}")
    @Produces("application/json")
    public OrderDTO getOrder(@PathParam("id") String id);

    @POST
    @Path("/orders/images/")
    @Produces("application/json")
    String submitImageRequest(@QueryParam("supplierids") List<Long> supplierIds, @QueryParam("values")List<FormElementValue> values);

    @POST
    @Path("/orders/products/")
    @Produces("application/json")
    String submitProductRequest(@QueryParam("productid")Long productId, @QueryParam("productserviceids") List<Long> productServiceIds, @QueryParam("values") List<FormElementValue> values) throws RequestException;
}
