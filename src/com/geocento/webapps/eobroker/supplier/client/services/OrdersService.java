package com.geocento.webapps.eobroker.supplier.client.services;

import com.geocento.webapps.eobroker.common.shared.entities.requests.RequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;
import java.util.List;

public interface OrdersService extends DirectRestService {

    @GET
    @Path("/requests/product/{id}")
    @Produces("application/json")
    public ProductServiceSupplierRequestDTO getProductRequest(@PathParam("id") String id) throws RequestException;

    @GET
    @Path("/requests/imagery/{id}")
    @Produces("application/json")
    ImageryServiceRequestDTO getImageryRequest(@PathParam("id") String id) throws RequestException;

    @GET
    @Path("/requests/images/{id}")
    @Produces("application/json")
    ImagesRequestDTO getImagesRequest(@PathParam("id") String id) throws RequestException;

    @GET
    @Path("/requests/")
    @Produces("application/json")
    List<RequestDTO> getRequests() throws RequestException;

    @POST
    @Path("/requests/response/")
    @Produces("application/json")
    void submitRequestResponse(@QueryParam("type") RequestDTO.TYPE type, @QueryParam("id") String id, String response) throws RequestException;

    @POST
    @Path("/requests/messages/")
    @Produces("application/json")
    MessageDTO addRequestMessage(@QueryParam("type") RequestDTO.TYPE type, @QueryParam("id") String id, String text) throws RequestException;

    @GET
    @Path("/requests/conversation/{id}")
    @Produces("application/json")
    ConversationDTO getConversation(@PathParam("id") String conversationid) throws RequestException;

    @POST
    @Path("/requests/conversation/{id}")
    @Produces("application/json")
    MessageDTO addConversationMessage(@PathParam("id") String conversationid, String text) throws RequestException;

    @GET
    @Path("/requests/conversation/")
    @Produces("application/json")
    List<ConversationDTO> getConversations() throws RequestException;
}
