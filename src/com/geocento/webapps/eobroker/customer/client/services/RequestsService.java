package com.geocento.webapps.eobroker.customer.client.services;

import com.geocento.webapps.eobroker.common.shared.entities.requests.Request;
import com.geocento.webapps.eobroker.common.shared.entities.requests.RequestDTO;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.geocento.webapps.eobroker.customer.shared.requests.ImageryResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ImagesServiceResponseDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.MessageDTO;
import com.geocento.webapps.eobroker.customer.shared.requests.ProductServiceResponseDTO;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;
import java.util.List;

public interface RequestsService extends DirectRestService {

    @GET
    @Path("/requests/")
    @Produces("application/json")
    List<RequestDTO> getRequests(@QueryParam("status") Request.STATUS status) throws RequestException;

    @GET
    @Path("/requests/count/")
    Integer getRequestsCount(@QueryParam("status") Request.STATUS status) throws RequestException;

    @PUT
    @Path("/requests/{id}")
    void updateRequestStatus(@PathParam("id") String id, @QueryParam("status") Request.STATUS status) throws RequestException;

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
    List<ConversationDTO> listConversations(@QueryParam("start") int start, @QueryParam("limit") int limit, @QueryParam("companyId") Long companyId) throws RequestException;

    @GET
    @Path("/requests/feedback/{id}")
    @Produces("application/json")
    FeedbackDTO getFeedback(@PathParam("id") String feedbackid) throws RequestException;

    @POST
    @Path("/requests/feedback/")
    @Produces("application/json")
    FeedbackDTO createFeedback(CreateFeedbackDTO feedbackDTO) throws RequestException;

    @POST
    @Path("/requests/feedback/{id}")
    @Produces("application/json")
    MessageDTO addFeedbackMessage(@PathParam("id") String feedbackid, String text) throws RequestException;

    @GET
    @Path("/assets/feedback/")
    @Produces("application/json")
    List<FeedbackDTO> listFeedbacks() throws RequestException;

}
