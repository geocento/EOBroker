package com.geocento.webapps.eobroker.client.services;

import com.geocento.webapps.eobroker.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.shared.entities.dtos.ProductServiceDTO;
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
    public ProductDTO getProduct(@PathParam("id") Long id);

    @POST
    @Path("/assets/products/")
    @Produces("application/json")
    public Long addProduct(ProductDTO product);

    @PUT
    @Path("/assets/products/")
    @Produces("application/json")
    public void updateProduct(ProductDTO product);

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
    @Path("/assets/companies/")
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

}