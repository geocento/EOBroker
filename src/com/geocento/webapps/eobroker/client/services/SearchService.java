package com.geocento.webapps.eobroker.client.services;

import com.geocento.webapps.eobroker.shared.imageapi.SearchRequest;
import com.geocento.webapps.eobroker.shared.Suggestion;
import com.geocento.webapps.eobroker.shared.entities.Category;
import com.geocento.webapps.eobroker.shared.entities.SearchResult;
import com.geocento.webapps.eobroker.shared.entities.dtos.ProductDTO;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;
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
    public List<ProductDTO> query(SearchRequest searchRequest) throws RequestException;

}
