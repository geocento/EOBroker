package com.geocento.webapps.eobroker.supplier.client.services;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
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

    @GET
    @Path("/assets/products/{id}")
    @Produces("application/json")
    public ProductDTO getProduct(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/products/")
    @Produces("application/json")
    public List<ProductDTO> listProducts() throws RequestException;

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
    @Path("/assets/companies/")
    @Produces("application/json")
    public CompanyDTO getCompany(@PathParam("id") Long id) throws RequestException;

    @PUT
    @Path("/assets/companies/")
    @Produces("application/json")
    public void updateCompany(CompanyDTO product) throws RequestException;

}
