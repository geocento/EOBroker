package com.geocento.webapps.eobroker.admin.client.services;

import com.geocento.webapps.eobroker.admin.shared.dtos.EditProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.NewsItem;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.admin.shared.dtos.DatasetProviderDTO;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;
import java.util.List;

public interface AssetsService extends DirectRestService {

    @GET
    @Path("/assets/aoi/{id}")
    @Produces("application/json")
    public AoIDTO getAoI(@PathParam("id") Long id);

    @POST
    @Path("/assets/aoi/")
    @Produces("application/json")
    public Long addAoI(AoIDTO aoi);

    @PUT
    @Path("/assets/aoi/")
    @Produces("application/json")
    public void updateAoI(AoIDTO aoi);

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
    public List<ProductDTO> listProducts(@QueryParam("start") int start, @QueryParam("limit") int limit, @QueryParam("orderby") String orderBy, @QueryParam("filter") String filter) throws RequestException;

    @GET
    @Path("/assets/productservices/{id}")
    @Produces("application/json")
    public ProductServiceDTO getProductService(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/productservices/")
    @Produces("application/json")
    public List<ProductServiceDTO> listProductServices() throws RequestException;

    @POST
    @Path("/assets/productservices/")
    @Produces("application/json")
    public Long addProductService(ProductServiceDTO productService);

    @PUT
    @Path("/assets/productservices/")
    @Produces("application/json")
    public void updateProductService(ProductServiceDTO product) throws RequestException;

    @GET
    @Path("/assets/companies/{id}")
    @Produces("application/json")
    public CompanyDTO getCompany(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/companies/")
    @Produces("application/json")
    public List<CompanyDTO> listCompanies() throws RequestException;

/*
    @PUT
    @Path("/assets/companies/")
    @Produces("application/json")
    public void updateCompany(CompanyDTO product) throws RequestException;
*/

    @POST
    @Path("/assets/companies/")
    @Produces("application/json")
    Long saveCompany(CompanyDTO companyDTO) throws RequestException;

    @GET
    @Path("/assets/newsitem/")
    @Produces("application/json")
    List<NewsItem> listNewsItems(@QueryParam("start") int start,
                                 @QueryParam("limit") int limit,
                                 @QueryParam("orderby") String orderby,
                                 @QueryParam("filterby") String filter) throws RequestException;

    @GET
    @Path("/assets/newsitem/{id}")
    @Produces("application/json")
    NewsItem getNewsItem(@PathParam("id") Long newsItemId) throws RequestException;

    @POST
    @Path("/assets/newsitem/")
    @Produces("application/json")
    void saveNewsItem(NewsItem newsItem) throws RequestException;

    @GET
    @Path("/assets/dataset/")
    @Produces("application/json")
    List<DatasetProviderDTO> listDatasets() throws RequestException;

}
