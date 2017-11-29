package com.geocento.webapps.eobroker.admin.client.services;

import com.geocento.webapps.eobroker.admin.shared.dtos.*;
import com.geocento.webapps.eobroker.common.shared.entities.ApplicationSettings;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.geocento.webapps.eobroker.common.shared.entities.ProductCategory;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;
import java.util.List;

public interface AssetsService extends DirectRestService {

    @POST
    @Path("/assets/products/")
    @Produces("application/json")
    public Long updateProduct(EditProductDTO productDTO) throws RequestException;

    @GET
    @Path("/assets/products/{id}")
    @Produces("application/json")
    public EditProductDTO getProduct(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/products/")
    @Produces("application/json")
    public List<ProductDTO> listProducts(@QueryParam("start") int start,
                                         @QueryParam("limit") int limit,
                                         @QueryParam("orderby") String orderBy,
                                         @QueryParam("filter") String filter) throws RequestException;

    @DELETE
    @Path("/assets/products/{id}")
    @Produces("application/json")
    public void removeProduct(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/productcategories/")
    @Produces("application/json")
    public List<ProductCategory> listProductCategories(@QueryParam("start") int start,
                                         @QueryParam("limit") int limit,
                                         @QueryParam("orderby") String orderBy,
                                         @QueryParam("filter") String filter) throws RequestException;

    @GET
    @Path("/assets/companies/{id}")
    @Produces("application/json")
    public CompanyDTO getCompany(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/companies/")
    @Produces("application/json")
    public List<CompanyDTO> listCompanies(@QueryParam("start") int start,
                                          @QueryParam("limit") int limit,
                                          @QueryParam("orderby") String orderby,
                                          @QueryParam("filter") String filter) throws RequestException;

    @GET
    @Path("/assets/companies/find/")
    @Produces("application/json")
    List<CompanyDTO> findCompanies(@QueryParam("text") String text);

    @POST
    @Path("/assets/companies/")
    @Produces("application/json")
    Long saveCompany(CompanyDTO companyDTO) throws RequestException;

    @PUT
    @Path("/companies/status/")
    void approveCompany(Long id) throws RequestException;

    @DELETE
    @Path("/assets/companies/{id}")
    void removeCompany(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/challenges/")
    @Produces("application/json")
    public List<ChallengeDTO> listChallenges(@QueryParam("start") int start,
                                          @QueryParam("limit") int limit,
                                          @QueryParam("orderby") String orderby,
                                          @QueryParam("filter") String filter) throws RequestException;

    @GET
    @Path("/assets/challenges/{id}")
    @Produces("application/json")
    public ChallengeDTO getChallenge(@PathParam("id") Long id) throws RequestException;

    @POST
    @Path("/assets/challenges/")
    @Produces("application/json")
    Long saveChallenge(ChallengeDTO challengeDTO) throws RequestException;

    @DELETE
    @Path("/assets/challenges/{id}")
    void removeChallenge(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/newsitem/")
    @Produces("application/json")
    List<NewsItem> listNewsItems(@QueryParam("start") int start,
                                 @QueryParam("limit") int limit,
                                 @QueryParam("orderby") String orderby,
                                 @QueryParam("filter") String filter) throws RequestException;

    @GET
    @Path("/assets/newsitem/{id}")
    @Produces("application/json")
    NewsItem getNewsItem(@PathParam("id") Long newsItemId) throws RequestException;

    @POST
    @Path("/assets/newsitem/")
    @Produces("application/json")
    void saveNewsItem(NewsItem newsItem) throws RequestException;

    @DELETE
    @Path("/assets/newsitem/{id}")
    void removeNewsItem(@PathParam("id") Long newsItemId) throws RequestException;

    @GET
    @Path("/assets/dataset/")
    @Produces("application/json")
    List<DatasetProviderDTO> listDatasets() throws RequestException;

    @GET
    @Path("/notifications/")
    @Produces("application/json")
    List<NotificationDTO> getNotifications() throws RequestException;

    @GET
    @Path("/feedback/{id}")
    @Produces("application/json")
    FeedbackDTO getFeedback(@PathParam("id") String feedbackid) throws RequestException;

    @POST
    @Path("/feedback/{id}")
    @Produces("application/json")
    MessageDTO addFeedbackMessage(@PathParam("id") String feedbackid, String text) throws RequestException;

    @GET
    @Path("/feedback/")
    @Produces("application/json")
    List<FeedbackDTO> listFeedbacks(@QueryParam("name") String name) throws RequestException;

    @GET
    @Path("/users/")
    @Produces("application/json")
    List<UserDescriptionDTO> listUsers(@QueryParam("start") int start,
                                       @QueryParam("limit") int limit,
                                       @QueryParam("orderby") String orderby,
                                       @QueryParam("filter") String filter) throws RequestException;

    @GET
    @Path("/users/{userName}")
    @Produces("application/json")
    UserDescriptionDTO getUser(@PathParam("userName") String userName) throws RequestException;

    @POST
    @Path("/users/")
    @Consumes("application/json")
    void saveUser(UserDescriptionDTO userDescriptionDTO) throws RequestException;

    @PUT
    @Path("/users/")
    @Consumes("application/json")
    void createUser(UserDescriptionDTO userDescriptionDTO) throws RequestException;

    @DELETE
    @Path("/users/{userName}")
    void removeUser(@PathParam("userName") String userName) throws RequestException;

    @GET
    @Path("/settings/")
    @Consumes("application/json")
    ApplicationSettings getSettings() throws RequestException;

    @PUT
    @Path("/settings/")
    @Consumes("application/json")
    void saveSettings(ApplicationSettings settings) throws RequestException;

    @GET
    @Path("/logs/")
    @Consumes("application/json")
    @Produces("application/json")
    LogsDTO getLogs() throws RequestException;
}
