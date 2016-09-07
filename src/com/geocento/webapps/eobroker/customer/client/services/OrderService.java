package com.geocento.webapps.eobroker.customer.client.services;

import com.geocento.webapps.eobroker.customer.shared.ImageRequestDTO;
import com.geocento.webapps.eobroker.customer.shared.ImagesRequestDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceRequestDTO;
import com.geocento.webapps.eobroker.customer.shared.RequestDTO;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;
import java.util.List;

public interface OrderService extends DirectRestService {

    @GET
    @Path("/requests/")
    @Produces("application/json")
    List<RequestDTO> getRequests() throws RequestException;

    @GET
    @Path("/requests/{id}")
    @Produces("application/json")
    public RequestDTO getRequest(@PathParam("id") String id);

    @POST
    @Path("/orders/image/")
    @Produces("application/json")
    RequestDTO submitImageRequest(ImageRequestDTO imageRequestDTO) throws RequestException;

    @POST
    @Path("/orders/products/")
    @Produces("application/json")
    RequestDTO submitProductRequest(ProductServiceRequestDTO productServiceRequestDTO) throws RequestException;

    @POST
    @Path("/orders/images/")
    @Produces("application/json")
    RequestDTO submitImagesRequest(ImagesRequestDTO imagesRequestDTO) throws RequestException;

}
