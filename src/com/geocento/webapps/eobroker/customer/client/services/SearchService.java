package com.geocento.webapps.eobroker.customer.client.services;

import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.SearchResult;
import com.geocento.webapps.eobroker.common.shared.entities.SupplierAPIResponse;
import com.geocento.webapps.eobroker.common.shared.imageapi.ImageProductDTO;
import com.geocento.webapps.eobroker.common.shared.imageapi.SearchRequest;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;
import java.util.Date;
import java.util.List;

public interface SearchService extends DirectRestService {

    @GET
    @Path("/complete")
    @Produces("application/json")
    public List<Suggestion> complete(@QueryParam("text") String text, @QueryParam("category") Category category, @QueryParam("aoi") String aoi);

    @GET
    @Path("/search")
    @Produces("application/json")
    public SearchResult getMatchingServices(@QueryParam("text") String text, @QueryParam("category") Category category, @QueryParam("aoiId") Long aoiId) throws RequestException;

    @GET
    @Path("/searchproduct")
    @Produces("application/json")
    public SearchResult getMatchingServicesForProduct(@QueryParam("productid") Long productId, @QueryParam("aoiId") Long aoiId) throws RequestException;

    @GET
    @Path("/listproducts")
    @Produces("application/json")
    public SearchResult listProducts(@QueryParam("text") String textFilter, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit, @QueryParam("aoiId") Long aoiId) throws RequestException;

    @POST
    @Path("/images/query")
    @Produces("application/json")
    public List<ImageProductDTO> queryImages(SearchRequest searchRequest) throws RequestException;

    @GET
    @Path("/products/feasibility")
    @Produces("application/json")
    public SupplierAPIResponse callSupplierAPI(@QueryParam("productid") Long productId, @QueryParam("aoiid") Long aoiId, @QueryParam("start") Date start, @QueryParam("stop") Date stop, @QueryParam("values") List<FormElementValue> formElementValues) throws RequestException;
}
