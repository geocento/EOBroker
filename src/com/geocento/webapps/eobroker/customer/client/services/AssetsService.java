package com.geocento.webapps.eobroker.customer.client.services;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.http.client.RequestException;
import org.fusesource.restygwt.client.DirectRestService;

import javax.ws.rs.*;

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

    @GET
    @Path("/assets/products/{id}")
    @Produces("application/json")
    public ProductDTO getProduct(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/productsfasibility/{id}")
    @Produces("application/json")
    public ProductFeasibilityDTO getProductFeasibility(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/productservices/")
    @Produces("application/json")
    public ProductServiceDTO getProductService(@PathParam("id") Long id);

    @POST
    @Path("/assets/productservices/")
    @Produces("application/json")
    public Long addProductService(ProductServiceDTO productService);

    @PUT
    @Path("/assets/productservices/")
    @Produces("application/json")
    public void updateProductService(ProductServiceDTO product);

    @GET
    @Path("/assets/companies/{id}")
    @Produces("application/json")
    public CompanyDTO getCompany(@PathParam("id") Long id);

    @POST
    @Path("/assets/companies/")
    @Produces("application/json")
    public Long addCompany(CompanyDTO company);

    @PUT
    @Path("/assets/companies/")
    @Produces("application/json")
    public void updateCompany(CompanyDTO product);

    @GET
    @Path("/assets/productsform/{id}")
    @Produces("application/json")
    ProductFormDTO getProductForm(@PathParam("id") Long productId) throws RequestException;

    @GET
    @Path("/assets/companies/full/{id}")
    @Produces("application/json")
    CompanyDescriptionDTO getCompanyDescription(@PathParam("id") Long companyId) throws RequestException;

    @GET
    @Path("/assets/productservices/full/{id}")
    @Produces("application/json")
    ProductServiceDescriptionDTO getProductServiceDescription(@PathParam("id") Long productServiceId) throws RequestException;

    @GET
    @Path("/assets/products/full/{id}")
    @Produces("application/json")
    ProductDescriptionDTO getProductDescription(@PathParam("id") Long productId) throws RequestException;
}
