package com.geocento.webapps.eobroker.customer.client.services;

import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.CSWGetRecordsResponse;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.OSDescriptionResponse;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.OSQueryRequest;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.OSQueryResponse;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;
import java.util.Date;
import java.util.List;

public interface SearchService extends DirectRestService {

    @GET
    @Path("/search/complete")
    @Produces("application/json")
    public List<Suggestion> complete(@QueryParam("text") String text, @QueryParam("category") Category category, @QueryParam("aoi") String aoi) throws RequestException;

    @GET
    @Path("/search/complete/allcompanies")
    @Produces("application/json")
    public List<Suggestion> completeAllCompanies(@QueryParam("text") String text, @QueryParam("aoi") String aoi);

    @GET
    @Path("/search/offer")
    @Produces("application/json")
    List<Offer> getMatchingOffer(@QueryParam("text") String text, @QueryParam("category") Category category, @QueryParam("aoi") Long aoiId) throws RequestException;

    @GET
    @Path("/search/services")
    @Produces("application/json")
    public SearchResult getMatchingServices(@QueryParam("text") String text) throws RequestException;

    @GET
    @Path("/search/product/{id}/services")
    @Produces("application/json")
    public SearchResult getMatchingServicesForProduct(@PathParam("id") Long productId, @QueryParam("aoiId") Long aoiId) throws RequestException;

    @POST
    @Path("/search/images/")
    @Produces("application/json")
    public List<Product> queryImages(SearchQuery searchQuery) throws RequestException;

    @POST
    @Path("/search/products/feasibility")
    @Produces("application/json")
    public FeasibilityResponseDTO callSupplierAPI(FeasibilityRequestDTO feasibilityRequestDTO) throws RequestException;

    @GET
    @Path("/search/sensors")
    @Produces("application/json")
    List<Suggestion> completeSensors(@QueryParam("text") String sensors);

    @POST
    @Path("/search/datasets/proxy")
    @Produces("application/json")
    CSWGetRecordsResponse getRecordsResponse(CSWGetRecordsRequestDTO request) throws RequestException;

    @GET
    @Path("/search/products")
    @Produces("application/json")
    public List<ProductDTO> listProducts(@QueryParam("text") String textFilter, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit, @QueryParam("aoiId") Long aoiId, @QueryParam("sector") Sector sector, @QueryParam("thematic") Thematic thematic) throws RequestException;

    // use POST to be able to query by AOI WKT
    @POST
    @Path("/search/productservices")
    @Produces("application/json")
    public List<ProductServiceDTO> listProductServices(@QueryParam("text") String textFilter, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit,
                                                       @QueryParam("aoiId") Long aoiId, @QueryParam("aoiIdWKT") String aoiWKT,
                                                       @QueryParam("affiliatesOnly") boolean affiliatesOnly, @QueryParam("companyId") Long companyId, @QueryParam("productId") Long productId
                                                       ) throws RequestException;

    @GET
    @Path("/search/productdatasets")
    @Produces("application/json")
    public List<ProductDatasetDTO> listProductDatasets(@QueryParam("text") String textFilter, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit,
                                                       @QueryParam("aoiId") Long aoiId, @QueryParam("aoiIdWKT") String aoiWKT,
                                                       @QueryParam("serviceType") ServiceType serviceType, @QueryParam("startDate") Long startTimeFrame, @QueryParam("stopDate") Long stopTimeFrame,
                                                       @QueryParam("affiliatesOnly") boolean affiliatesOnly, @QueryParam("companyId") Long companyId, @QueryParam("productId") Long productId
    ) throws RequestException;

    @GET
    @Path("/search/software")
    @Produces("application/json")
    public List<SoftwareDTO> listSoftware(@QueryParam("text") String textFilter, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit, @QueryParam("aoiId") Long aoiId, @QueryParam("type") SoftwareType softwareType,
                                          @QueryParam("affiliatesOnly") boolean affiliatesOnly, @QueryParam("companyId") Long companyId, @QueryParam("productId") Long productId) throws RequestException;

    @GET
    @Path("/search/projects")
    @Produces("application/json")
    public List<ProjectDTO> listProjects(@QueryParam("text") String textFilter, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit, @QueryParam("aoiId") Long aoiId,
                                         @QueryParam("startDate") Date startDate, @QueryParam("stopDate") Date stopDate,
                                         @QueryParam("affiliatesOnly") boolean affiliatesOnly, @QueryParam("companyId") Long companyId, @QueryParam("productId") Long productId) throws RequestException;

    @GET
    @Path("/search/companies")
    @Produces("application/json")
    List<CompanyDTO> listCompanies(@QueryParam("text") String textFilter, @QueryParam("start") Integer start, @QueryParam("limit") Integer limit, @QueryParam("aoiId") Long aoiId, @QueryParam("size") COMPANY_SIZE companySize, @QueryParam("years") int minYears, @QueryParam("country") String countryCode) throws RequestException;

    @POST
    @Path("/search/opensearch/query")
    @Produces("application/json")
    OSQueryResponse getOSQueryResponse(OSQueryRequest requestDTO) throws RequestException;

    @GET
    @Path("/search/opensearch/description")
    @Produces("application/json")
    OSDescriptionResponse getOSDescriptionResponse(String requestUrl) throws Exception;

}
