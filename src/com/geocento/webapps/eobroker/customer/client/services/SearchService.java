package com.geocento.webapps.eobroker.customer.client.services;

import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.SearchResult;
import com.geocento.webapps.eobroker.customer.shared.SupplierAPIResponse;
import com.geocento.webapps.eobroker.customer.shared.FeasibilityRequestDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.geocento.webapps.eobroker.common.shared.imageapi.SearchRequest;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;
import java.util.List;

public interface SearchService extends DirectRestService {

    @GET
    @Path("/search/complete")
    @Produces("application/json")
    public List<Suggestion> complete(@QueryParam("text") String text, @QueryParam("category") Category category, @QueryParam("aoi") String aoi);

    @GET
    @Path("/search/services")
    @Produces("application/json")
    public SearchResult getMatchingServices(@QueryParam("text") String text, @QueryParam("category") Category category, @QueryParam("aoiId") Long aoiId) throws RequestException;

    @GET
    @Path("/search/product/{id}/services")
    @Produces("application/json")
    public SearchResult getMatchingServicesForProduct(@PathParam("id") Long productId, @QueryParam("aoiId") Long aoiId) throws RequestException;

    @GET
    @Path("/search/products")
    @Produces("application/json")
    public List<ProductDTO> listProducts(@QueryParam("text") String textFilter, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit, @QueryParam("aoiId") Long aoiId) throws RequestException;

    @POST
    @Path("/search/images/")
    @Produces("application/json")
    public List<Product> queryImages(SearchRequest searchRequest) throws RequestException;

    @POST
    @Path("/search/products/feasibility")
    @Produces("application/json")
    public SupplierAPIResponse callSupplierAPI(FeasibilityRequestDTO feasibilityRequestDTO) throws RequestException;
}
