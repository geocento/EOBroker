package com.geocento.webapps.eobroker.supplier.client.services;

import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
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
    public ProductServiceEditDTO getProductService(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/productservices/")
    @Produces("application/json")
    public List<ProductServiceDTO> listProductServices() throws RequestException;

    @PUT
    @Path("/assets/productservices/")
    public Long updateProductService(ProductServiceEditDTO product) throws RequestException;

    @DELETE
    @Path("/assets/productservices/{id}")
    void removeProductServices(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/companies/")
    @Produces("application/json")
    public CompanyDTO getCompany() throws RequestException;

    @PUT
    @Path("/assets/companies/")
    @Produces("application/json")
    public void updateCompany(CompanyDTO product) throws RequestException;

    @GET
    @Path("/assets/products/find/")
    @Produces("application/json")
    List<ProductDTO> findProducts(@QueryParam("text") String text);

    @GET
    @Path("/assets/notifications/")
    @Produces("application/json")
    List<SupplierNotificationDTO> getNotifications() throws RequestException;

    @GET
    @Path("/assets/notifications/{id}")
    @Produces("application/json")
    SupplierNotificationDTO getNotification(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/dataset/")
    @Produces("application/json")
    List<DatasetProviderDTO> listDatasets() throws RequestException;

    @GET
    @Path("/assets/dataset/{id}")
    @Produces("application/json")
    DatasetProviderDTO getDatasetProvider(@PathParam("id") Long id) throws RequestException;

    @POST
    @Path("/assets/dataset/")
    Long saveDatasetProvider(DatasetProviderDTO datasetProviderDTO) throws RequestException;

    @GET
    @Path("/assets/productdataset/")
    @Produces("application/json")
    List<ProductDatasetDTO> listProductDatasets() throws RequestException;

    @GET
    @Path("/assets/productdataset/{id}")
    @Produces("application/json")
    ProductDatasetDTO getProductDataset(@PathParam("id") Long id) throws RequestException;

    @POST
    @Path("/assets/productdataset/")
    Long updateProductDataset(ProductDatasetDTO productDatasetDTO) throws RequestException;

    @DELETE
    @Path("/assets/productdataset/{id}")
    void removeProductDataset(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/software/")
    @Produces("application/json")
    List<SoftwareDTO> listSoftwares() throws RequestException;

    @GET
    @Path("/assets/software/{id}")
    @Produces("application/json")
    SoftwareDTO getSoftware(@PathParam("id") Long id) throws RequestException;

    @POST
    @Path("/assets/software/")
    Long updateSoftware(SoftwareDTO softwareDTO) throws RequestException;

    @DELETE
    @Path("/assets/software/{id}")
    void removeSoftware(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/project/")
    @Produces("application/json")
    List<ProjectDTO> listProjects() throws RequestException;

    @GET
    @Path("/assets/project/{id}")
    @Produces("application/json")
    ProjectDTO getProject(@PathParam("id") Long id) throws RequestException;

    @POST
    @Path("/assets/project/")
    Long updateProject(ProjectDTO projectDTO) throws RequestException;

    @DELETE
    @Path("/assets/project/{id}")
    void removeProject(@PathParam("id") Long id) throws RequestException;

    @GET
    @Path("/assets/offer/{category}")
    @Produces("application/json")
    OfferDTO getOffer(@PathParam("category") Category category) throws RequestException;

    @GET
    @Path("/assets/products/geoinformation/{id}")
    @Produces("application/json")
    ProductGeoinformation getProductGeoinformation(@PathParam("id") Long productId) throws RequestException;

    @GET
    @Path("/assets/companies/find/")
    @Produces("application/json")
    List<CompanyDTO> findCompanies(@QueryParam("text") String text) throws RequestException;

    @POST
    @Path("/assets/ows/styles/")
    public String saveStyle(StyleDTO styleDTO) throws RequestException;

    @GET
    @Path("/assets/ows/styles/")
    @Produces("application/json")
    public List<String> getStyles() throws RequestException;

    @GET
    @Path("/assets/settings/")
    @Produces("application/json")
    SupplierSettingsDTO getSettings() throws RequestException;

    @PUT
    @Path("/assets/settings/")
    void saveSettings(SupplierSettingsDTO supplierSettings) throws RequestException;

    @GET
    @Path("/assets/stories/")
    @Produces("application/json")
    List<SuccessStoryDTO> getSuccessStories() throws RequestException;

    @GET
    @Path("/assets/stories/{id}")
    @Produces("application/json")
    SuccessStoryEditDTO getSuccessStory(@PathParam("id") Long id) throws RequestException;

    @POST
    @Path("/assets/stories/")
    Long updateSuccessStory(SuccessStoryEditDTO successStoryEditDTO) throws RequestException;

    @GET
    @Path("/assets/testimonials/")
    @Produces("application/json")
    List<TestimonialDTO> getTestimonials() throws RequestException;

    @GET
    @Path("/assets/statistics/")
    @Produces("application/json")
    SupplierStatisticsDTO getStatistics() throws RequestException;
}
