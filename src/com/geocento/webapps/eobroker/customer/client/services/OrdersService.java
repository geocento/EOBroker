package com.geocento.webapps.eobroker.customer.client.services;

import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.geocento.webapps.eobroker.customer.shared.requests.ImageryResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ImagesServiceResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceResponseDTO;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;
import java.util.List;

public interface OrdersService extends DirectRestService {

    @GET
    @Path("/requests/")
    @Produces("application/json")
    List<RequestDTO> getRequests() throws RequestException;

    @POST
    @Path("/requests/image/")
    @Produces("application/json")
    RequestDTO submitImageRequest(ImageRequestDTO imageRequestDTO) throws RequestException;

    @POST
    @Path("/requests/product/")
    @Produces("application/json")
    RequestDTO submitProductRequest(ProductServiceRequestDTO productServiceRequestDTO) throws RequestException;

    @POST
    @Path("/requests/images/")
    @Produces("application/json")
    RequestDTO submitImagesRequest(ImagesRequestDTO imagesRequestDTO) throws RequestException;

    @GET
    @Path("/requests/product/{id}")
    @Produces("application/json")
    ProductServiceResponseDTO getProductResponse(@PathParam("id") String id) throws RequestException;

    @GET
    @Path("/requests/image/{id}")
    @Produces("application/json")
    ImageryResponseDTO getImageResponse(@PathParam("id") String id) throws RequestException;

    @GET
    @Path("/requests/images/{id}")
    @Produces("application/json")
    ImagesServiceResponseDTO getImagesResponse(@PathParam("id") String id) throws RequestException;

    @POST
    @Path("/requests/product/message/{id}")
    @Produces("application/json")
    com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO addProductResponseMessage(@PathParam("id") Long id, String text) throws RequestException;

    @POST
    @Path("/requests/image/message/{id}")
    @Produces("application/json")
    com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO addImageryResponseMessage(@PathParam("id") Long id, String text) throws RequestException;

    @POST
    @Path("/requests/images/message/{id}")
    @Produces("application/json")
    com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO addImagesResponseMessage(@PathParam("id") String id, String text) throws RequestException;

    @GET
    @Path("/requests/conversation/{id}")
    @Produces("application/json")
    ConversationDTO getConversation(@PathParam("id") String conversationid) throws RequestException;

    @POST
    @Path("/requests/conversation/")
    @Produces("application/json")
    ConversationDTO createConversation(CreateConversationDTO conversationDTO) throws RequestException;

    @POST
    @Path("/requests/conversation/{id}")
    @Produces("application/json")
    MessageDTO addConversationMessage(@PathParam("id") String conversationid, String text) throws RequestException;

    @GET
    @Path("/assets/conversation/")
    @Produces("application/json")
    List<ConversationDTO> listConversations(@QueryParam("companyId") Long companyId) throws RequestException;
}
