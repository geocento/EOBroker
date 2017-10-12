package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.Utils.DBHelper;
import com.geocento.webapps.eobroker.common.server.Utils.EventHelper;
import com.geocento.webapps.eobroker.common.server.Utils.GeoserverUtils;
import com.geocento.webapps.eobroker.common.shared.AuthorizationException;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.DatasetProvider;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIPolygonDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;
import com.geocento.webapps.eobroker.common.shared.entities.requests.ProductServiceRequest;
import com.geocento.webapps.eobroker.common.shared.entities.requests.ProductServiceSupplierRequest;
import com.geocento.webapps.eobroker.common.shared.entities.subscriptions.Event;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;
import com.geocento.webapps.eobroker.common.shared.feasibility.BarChartStatistics;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import com.geocento.webapps.eobroker.supplier.client.services.AssetsService;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.geocento.webapps.eobroker.supplier.shared.utils.ProductHelper;
import com.google.gwt.http.client.RequestException;
import it.geosolutions.geoserver.rest.GeoServerRESTReader;
import it.geosolutions.geoserver.rest.decoder.RESTStyleList;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/")
public class AssetsResource implements AssetsService {

    Logger logger = Logger.getLogger(AssetsResource.class);

    // assumes service is not a singleton
    @Context
    HttpServletRequest request;

    public AssetsResource() {
        logger.info("Starting service...");
    }

    @Override
    public AoIDTO getAoI(Long id) {
        return new AoIPolygonDTO();
    }

    @Override
    public Long addAoI(AoIDTO aoi) {
        return null;
    }

    @Override
    public void updateAoI(AoIDTO aoi) {

    }

    @Override
    public ProductDTO getProduct(Long id) throws RequestException {
        UserUtils.verifyUserSupplier(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Product product = em.find(Product.class, id);
            if(product == null) {
                throw new RequestException("Unknown product");
            }
            return ProductHelper.createProductDTO(product);
        } catch (Exception e) {
            throw new RequestException("Error");
        } finally {
            em.close();
        }
    }

    @Override
    public List<ProductDTO> listProducts() throws RequestException {
        UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            TypedQuery<Product> query = em.createQuery("select p from Product p", Product.class);
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<Product, ProductDTO>() {
                @Override
                public ProductDTO mutate(Product product) {
                    return ProductHelper.createProductDTO(product);
                }
            });
        } catch (Exception e) {
            throw new RequestException("Error");
        } finally {
            em.close();
        }
    }

    @Override
    public ProductGeoinformation getProductGeoinformation(Long productId) throws RequestException {
        UserUtils.verifyUserSupplier(request);
        if(productId == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Product product = em.find(Product.class, productId);
            if(product == null) {
                throw new RequestException("Unknown product");
            }
            ProductGeoinformation productGeoinformation = new ProductGeoinformation();
            productGeoinformation.setFeatureDescriptions(product.getGeoinformation());
            productGeoinformation.setPerformanceDescriptions(product.getPerformances());
            return productGeoinformation;
        } catch (Exception e) {
            throw new RequestException("Error");
        } finally {
            em.close();
        }
    }

    @Override
    public ProductServiceEditDTO getProductService(Long id) throws RequestException {
        UserUtils.verifyUserSupplier(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductService productService = em.find(ProductService.class, id);
            if(productService == null) {
                throw new RequestException("Unknown product");
            }
            ProductServiceEditDTO productServiceDTO = new ProductServiceEditDTO();
            productServiceDTO.setId(productService.getId());
            productServiceDTO.setName(productService.getName());
            productServiceDTO.setDescription(productService.getDescription());
            productServiceDTO.setFullDescription(productService.getFullDescription());
            productServiceDTO.setEmail(productService.getEmail());
            productServiceDTO.setWebsite(productService.getWebsite());
            productServiceDTO.setServiceImage(productService.getImageUrl());
            productServiceDTO.setExtent(productService.getExtent());
            productServiceDTO.setCoverageLayers(productService.getCoverageLayers());
            if(productService.getProduct() != null) {
                productServiceDTO.setProduct(ProductHelper.createProductDTO(productService.getProduct()));
                productServiceDTO.setProductFeatures(productService.getProduct().getGeoinformation());
                productServiceDTO.setSelectedFeatures(ListUtil.mutate(productService.getGeoinformation(), new ListUtil.Mutate<FeatureDescription, Long>() {
                    @Override
                    public Long mutate(FeatureDescription featureDescription) {
                        return featureDescription.getId();
                    }
                }));
                productServiceDTO.setGeoinformationComment(productService.getGeoinformationComment());
                productServiceDTO.setPerformances(productService.getProduct().getPerformances());
                productServiceDTO.setProvidedPerformances(productService.getPerformances());
                productServiceDTO.setPerformancesComment(productService.getPerformancesComment());
            }
            productServiceDTO.setApiURL(productService.getApiUrl());
            productServiceDTO.setSelectedDataAccessTypes(productService.getSelectedAccessTypes());
            productServiceDTO.setDisseminationComment(productService.getDisseminationComment());
            productServiceDTO.setTimeToDelivery(productService.getTimeToDelivery());
            productServiceDTO.setSamples(productService.getSamples());
            productServiceDTO.setTermsAndConditions(productService.getTermsAndConditions());
            return productServiceDTO;
        } catch (Exception e) {
            throw new RequestException("Error");
        } finally {
            em.close();
        }
    }

    @Override
    public List<ProductServiceDTO> listProductServices() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<ProductService> query = em.createQuery("select p from ProductService p where p.company = :company", ProductService.class);
            query.setParameter("company", user.getCompany());
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<ProductService, ProductServiceDTO>() {
                @Override
                public ProductServiceDTO mutate(ProductService productService) {
                        ProductServiceDTO productServiceDTO = new ProductServiceDTO();
                        productServiceDTO.setId(productService.getId());
                        productServiceDTO.setName(productService.getName());
                        productServiceDTO.setDescription(productService.getDescription());
                        productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
                        productServiceDTO.setCompanyName(productService.getCompany().getName());
                        productServiceDTO.setServiceImage(productService.getImageUrl());
                        return productServiceDTO;
                    }
            });
        } catch (Exception e) {
            throw new RequestException("Error");
        } finally {
            em.close();
        }
    }

    @Override
    public Long updateProductService(final ProductServiceEditDTO productServiceDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        if(productServiceDTO == null ) {
            throw new RequestException("Product service cannot be null");
        }
        boolean newService = productServiceDTO.getId() == null;
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            ProductService productService = null;
            User user = em.find(User.class, userName);
            if(!newService) {
                productService = em.find(ProductService.class, productServiceDTO.getId());
                if(productService == null) {
                    throw new RequestException("Unknown product");
                }
                if(user.getCompany() != productService.getCompany()) {
                    throw new AuthorizationException();
                }
            } else {
                productService = new ProductService();
                productService.setCompany(user.getCompany());
                em.persist(productService);
                user.getCompany().getServices().add(productService);
            }
            productService.setName(productServiceDTO.getName());
            productService.setDescription(productServiceDTO.getDescription());
            productService.setImageUrl(productServiceDTO.getServiceImage());
            Product product = em.find(Product.class, productServiceDTO.getProduct().getId());
            if (product == null) {
                throw new RequestException("Product does not exist");
            }
            // if it was previously assigned to a different product, make sure we remove the service from the product first
            if(productService.getProduct() != null) {
                productService.getProduct().getProductServices().remove(productService);
            }
            productService.setProduct(product);
            product.getProductServices().add(productService);
            productService.setEmail(productServiceDTO.getEmail());
            productService.setWebsite(productServiceDTO.getWebsite());
            productService.setFullDescription(productServiceDTO.getFullDescription());
            productService.setExtent(productServiceDTO.getExtent());
            // update the coverage layers
            List<DatasetAccessOGC> dbCoverageLayers = updateCoverageLayers(em, productService.getCoverageLayers(), productServiceDTO.getCoverageLayers());
            productService.setCoverageLayers(dbCoverageLayers);
            productService.setGeoinformation(ListUtil.filterValues(productService.getProduct().getGeoinformation(), new ListUtil.CheckValue<FeatureDescription>() {
                @Override
                public boolean isValue(FeatureDescription value) {
                    return productServiceDTO.getSelectedFeatures().contains(value.getId());
                }
            }));
            productService.setGeoinformationComment(productServiceDTO.getGeoinformationComment());
            List<PerformanceValue> performances = updatePerformances(em, productService.getPerformances(), productServiceDTO.getProvidedPerformances());
            productService.setPerformances(performances);
            productService.setPerformancesComment(productServiceDTO.getPerformancesComment());
            productService.setSelectedAccessTypes(productServiceDTO.getSelectedDataAccessTypes());
            productService.setApiUrl(productServiceDTO.getApiURL());
            productService.setDisseminationComment(productServiceDTO.getDisseminationComment());
            productService.setTimeToDelivery(productServiceDTO.getTimeToDelivery());
            // update the sample access
            List<DatasetAccess> dbSamples = updateSamples(em, productService.getSamples(), productServiceDTO.getSamples());
            productService.setSamples(dbSamples);
            productService.setTermsAndConditions(productService.getTermsAndConditions());
            // update the keyphrases
            Query query = em.createNativeQuery("UPDATE productservice SET tsv = " + DBHelper.getProductServiceTSV(productService) +
                    ", tsvname = " + DBHelper.getProductServiceNameTSV(productService) + " where id = " + productService.getId() +
                    ";");
            query.executeUpdate();
            em.getTransaction().commit();
            // send notifications
            if(newService) {
                // fail silently
                try {
                    em.getTransaction().begin();
                    // add event for company and for product
                    EventHelper.createAndPropagateCompanyEvent(em, user.getCompany(), Category.productservices, Event.TYPE.OFFER, "New service available for product " + product.getName(), productService.getId() + "");
                    EventHelper.createAndPropagateProductEvent(em, product, user.getCompany(), Category.productservices, Event.TYPE.OFFER, "New service available for product " + product.getName(), productService.getId() + "");
                    em.getTransaction().commit();
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    logger.error(e.getMessage(), e);
                }
            }
            return productService.getId();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException("Error updating product service");
        } finally {
            em.close();
        }
    }

    private List<DatasetAccessOGC> updateCoverageLayers(EntityManager em, List<DatasetAccessOGC> dbCoverageLayers, List<DatasetAccessOGC> coverageLayers) {
        List<DatasetAccessOGC> changeCoverageLayers = new ArrayList<DatasetAccessOGC>();
        if (coverageLayers != null && coverageLayers.size() > 0) {
            for (final DatasetAccessOGC datasetAccess : coverageLayers) {
                DatasetAccessOGC dbDatasetAccess = null;
                if (datasetAccess.getId() != null) {
                    dbDatasetAccess = ListUtil.findValue(dbCoverageLayers, new ListUtil.CheckValue<DatasetAccessOGC>() {
                        @Override
                        public boolean isValue(DatasetAccessOGC value) {
                            return value.getId().equals(datasetAccess.getId());
                        }
                    });
                }
                if (dbDatasetAccess == null) {
                    em.persist(datasetAccess);
                    dbDatasetAccess = datasetAccess;
                }
                dbDatasetAccess.setTitle(datasetAccess.getTitle());
                dbDatasetAccess.setPitch(datasetAccess.getPitch());
                // no need for these for coverage layers
/*
                dbDatasetAccess.setUri(datasetAccess.getUri());
                dbDatasetAccess.setSize(datasetAccess.getSize());
*/
                dbDatasetAccess.setLayerName(datasetAccess.getLayerName());
                // now do some data access specific stuff
                // check if style has changed
                if (!StringUtils.areStringEqualsOrNull(dbDatasetAccess.getStyleName(), ((DatasetAccessOGC) datasetAccess).getStyleName())) {
                    // update db and update geoserver
                    try {
                        dbDatasetAccess.setStyleName(datasetAccess.getStyleName());
                    } catch (Exception e) {

                    }
                }
                dbDatasetAccess.setServerUrl(datasetAccess.getServerUrl());
                dbDatasetAccess.setCorsEnabled(datasetAccess.isCorsEnabled());
                changeCoverageLayers.add(dbDatasetAccess);
            }
        }
        return changeCoverageLayers;
    }

    @Override
    public void removeProductServices(Long id) throws RequestException {
        // TODO - make sure service is not used elsewhere and remove all samples from the server
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            ProductService productService = em.find(ProductService.class, id);
            if(productService == null || user.getCompany() != productService.getCompany()) {
                throw new RequestException("Not authorised");
            }
            // check where the product service is being used first
            // look for requests
            TypedQuery<ProductServiceSupplierRequest> query = em.createQuery("select p from ProductServiceSupplierRequest p where p.productService = :service", ProductServiceSupplierRequest.class);
            query.setParameter("service", productService);
            // loop through requests and remove
            for(ProductServiceSupplierRequest productServiceSupplierRequest : query.getResultList()) {
                em.remove(productServiceSupplierRequest);
                // check if the request has more supplier requests
                ProductServiceRequest productServiceRequest = productServiceSupplierRequest.getProductServiceRequest();
                productServiceRequest.getSupplierRequests().remove(productServiceSupplierRequest);
                if(productServiceRequest.getSupplierRequests().size() == 0) {
                    em.remove(productServiceRequest);
                }
            }
            // TODO - remove the samples?
            productService.getCompany().getServices().remove(productService);
            em.remove(productService);
            em.getTransaction().commit();
            // notify user
            // fail silently
            try {
                em.getTransaction().begin();
                // add event for company and for product
                EventHelper.createAndPropagateCompanyEvent(em, user.getCompany(), Category.productservices, Event.TYPE.OFFER,
                        "Product service has been discontinued " + productService.getName(), null);
                EventHelper.createAndPropagateProductEvent(em, productService.getProduct(), user.getCompany(), Category.productservices, Event.TYPE.OFFER,
                        "Product service has been discontinued " + productService.getName(), null);
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                logger.error(e.getMessage(), e);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error removing service");
        } finally {
            em.close();
        }
    }

    private List<PerformanceValue> updatePerformances(EntityManager em, List<PerformanceValue> dbPerformanceValues, List<PerformanceValue> performanceValues) {
        List<PerformanceValue> changedDbPerformances = new ArrayList<PerformanceValue>();
        if (performanceValues != null && performanceValues.size() > 0) {
            for (final PerformanceValue performanceValue : performanceValues) {
                PerformanceValue dbPerformanceValue = null;
                if (performanceValue.getId() != null) {
                    dbPerformanceValue = ListUtil.findValue(dbPerformanceValues, new ListUtil.CheckValue<PerformanceValue>() {
                        @Override
                        public boolean isValue(PerformanceValue value) {
                            return value.getId().equals(performanceValue.getId());
                        }
                    });
                }
                if (dbPerformanceValue == null) {
                    em.persist(performanceValue);
                    dbPerformanceValue = performanceValue;
                }
                dbPerformanceValue.setPerformanceDescription(performanceValue.getPerformanceDescription());
                dbPerformanceValue.setComment(performanceValue.getComment());
                dbPerformanceValue.setMinValue(performanceValue.getMinValue());
                dbPerformanceValue.setMaxValue(performanceValue.getMaxValue());
                changedDbPerformances.add(dbPerformanceValue);
            }
        }
        // TODO - check for values which have been removed

        return changedDbPerformances;
    }

    private List<DatasetAccess> updateSamples(EntityManager em, List<DatasetAccess> dbSamples, List<DatasetAccess> samples) {
        List<DatasetAccess> changedDbSamples = new ArrayList<DatasetAccess>();
        if (samples != null && samples.size() > 0) {
            for (final DatasetAccess datasetAccess : samples) {
                DatasetAccess dbDatasetAccess = null;
                if (datasetAccess.getId() != null) {
                    dbDatasetAccess = ListUtil.findValue(dbSamples, new ListUtil.CheckValue<DatasetAccess>() {
                        @Override
                        public boolean isValue(DatasetAccess value) {
                            return value.getId().equals(datasetAccess.getId());
                        }
                    });
                }
                if (dbDatasetAccess == null) {
                    em.persist(datasetAccess);
                    dbDatasetAccess = datasetAccess;
                }
                dbDatasetAccess.setTitle(datasetAccess.getTitle());
                dbDatasetAccess.setPitch(datasetAccess.getPitch());
                dbDatasetAccess.setUri(datasetAccess.getUri());
                dbDatasetAccess.setSize(datasetAccess.getSize());
                // now do some data access specific stuff
                if (dbDatasetAccess instanceof DatasetAccessOGC) {
                    DatasetAccessOGC dbDatasetAccessOGC = (DatasetAccessOGC) dbDatasetAccess;
                    DatasetAccessOGC datasetAccessOGC = (DatasetAccessOGC) datasetAccess;
                    // check if style has changed
                    if (!StringUtils.areStringEqualsOrNull(dbDatasetAccessOGC.getStyleName(), ((DatasetAccessOGC) datasetAccess).getStyleName())) {
                        // update db and update geoserver
                        try {
                            dbDatasetAccessOGC.setStyleName(datasetAccessOGC.getStyleName());
                        } catch (Exception e) {

                        }
                    }
                    dbDatasetAccessOGC.setServerUrl(datasetAccessOGC.getServerUrl());
                    dbDatasetAccessOGC.setLayerName(datasetAccessOGC.getLayerName());
                    dbDatasetAccessOGC.setWcsServerUrl(datasetAccessOGC.getWcsServerUrl());
                    dbDatasetAccessOGC.setWcsResourceName(datasetAccessOGC.getWcsResourceName());
                    dbDatasetAccessOGC.setCorsEnabled(datasetAccessOGC.isCorsEnabled());
                }
                changedDbSamples.add(dbDatasetAccess);
            }
        }
        return changedDbSamples;
    }

    @Override
    public CompanyDTO getCompany() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            Company company = user.getCompany();
            if (company == null) {
                throw new RequestException("No company assigned!");
            }
            return CompanyHelper.createFullCompanyDTO(company);
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException("Issue retrieving company");
        } finally {
            em.close();
        }
    }

    @Override
    public void updateCompany(CompanyDTO companyDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        if(companyDTO == null || companyDTO.getId() == null) {
            throw new RequestException("Company cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        Company company = em.find(Company.class, companyDTO.getId());
        if(company == null) {
            throw new RequestException("Unknown company");
        }
        User user = em.find(User.class, userName);
        if(user.getCompany().getId().compareTo(companyDTO.getId()) != 0) {
            throw new AuthorizationException();
        }
        try {
            em.getTransaction().begin();
            company.setName(companyDTO.getName());
            company.setIconURL(companyDTO.getIconURL());
            company.setWebsite(companyDTO.getWebsite());
            company.setContactEmail(companyDTO.getContactEmail());
            company.setDescription(companyDTO.getDescription());
            company.setFullDescription(companyDTO.getFullDescription());
            company.setStartedIn(companyDTO.getStartedIn());
            company.setAddress(companyDTO.getAddress());
            company.setCountryCode(companyDTO.getCountryCode());
            company.setCompanySize(companyDTO.getCompanySize());
            company.setAwards(companyDTO.getAwards());
            em.getTransaction().commit();
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException("Error updating company");
        } finally {
            em.close();
        }
    }

    @Override
    public List<ProductDTO> findProducts(String textFilter) {
/*
        // check if last character is a space
        boolean partialMatch = !text.endsWith(" ");
        text.trim();
        if(text.length() == 0) {
            return null;
        }
        // break down text into sub words
        String[] words = text.split(" ");
        String keywords = StringUtils.join(words, " | ");
        if(partialMatch) {
            keywords += ":*";
        }
*/
        String keywords = DBHelper.generateKeywords(textFilter);
        // change the last word so that it allows for partial match
        String sqlStatement = "SELECT id, \"name\", imageurl, ts_rank(tsvname, keywords, 8) AS rank, id\n" +
                "          FROM product, to_tsquery('" + keywords + "') AS keywords\n" +
                "          WHERE tsvname @@ keywords\n" +
                "          ORDER BY rank\n" +
                "          LIMIT 10;";
        EntityManager em = EMF.get().createEntityManager();
        Query q = em.createNativeQuery(sqlStatement);
        List<Object[]> results = q.getResultList();
        List<ProductDTO> suggestions = new ArrayList<ProductDTO>();
        for(Object[] result : results) {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setId((Long) result[0]);
            productDTO.setName((String) result[1]);
            productDTO.setImageUrl((String) result[2]);
            suggestions.add(productDTO);
        }
        return suggestions;
    }

    @Override
    public List<CompanyDTO> findCompanies(String textFilter) {
/*
        // check if last character is a space
        boolean partialMatch = !text.endsWith(" ");
        text.trim();
        if(text.length() == 0) {
            return null;
        }
        // break down text into sub words
        String[] words = text.split(" ");
        String keywords = StringUtils.join(words, " | ");
        if(partialMatch) {
            keywords += ":*";
        }
*/
        String keywords = DBHelper.generateKeywords(textFilter);
        // change the last word so that it allows for partial match
        String sqlStatement = "SELECT id, \"name\", iconurl, ts_rank(tsvname, keywords, 8) AS rank, id\n" +
                "          FROM company, to_tsquery('" + keywords + "') AS keywords\n" +
                "          WHERE tsvname @@ keywords\n" +
                "          ORDER BY rank\n" +
                "          LIMIT 10;";
        EntityManager em = EMF.get().createEntityManager();
        Query q = em.createNativeQuery(sqlStatement);
        List<Object[]> results = q.getResultList();
        List<CompanyDTO> suggestions = new ArrayList<CompanyDTO>();
        for(Object[] result : results) {
            CompanyDTO companyDTO = new CompanyDTO();
            companyDTO.setId((Long) result[0]);
            companyDTO.setName((String) result[1]);
            companyDTO.setIconURL((String) result[2]);
            suggestions.add(companyDTO);
        }
        return suggestions;
    }

    @Override
    public String saveStyle(StyleDTO styleDTO) throws RequestException {
        String logUserName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, logUserName);
            // define the style
            String workspace = user.getCompany().getId() + "";
            // TODO - change when geoserver workspace style is fixed
            String styleName = styleDTO.getStyleName(); //workspace + "___" + styleDTO.getStyleName();
            String sldBody = styleDTO.getSldBody();
            // publish to GeoServer
/*
            boolean added = GeoserverUtils.getGeoserverPublisher().publishStyle(sldBody, styleName);
*/
            boolean added = GeoserverUtils.getGeoserverPublisher().publishStyleInWorkspace(workspace, sldBody, styleName);
            if(!added) {
                throw new RequestException("Failed to add style, style name is probably taken");
            }
            return "\"" + workspace + ":" + styleName + "\"";
        } catch (Exception e) {
            throw new RequestException("Problem creating style");
        } finally {
            em.close();
        }
    }

    @Override
    public List<String> getStyles() throws RequestException {
        String logUserName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        User user = em.find(User.class, logUserName);
        // define the style
        final String workspace = user.getCompany().getId() + "";
        try {
            // publish to GeoServer
            GeoServerRESTReader reader = GeoserverUtils.getGeoserverReader(); //new GeoServerRESTReader(RESTURL, RESTUSER, RESTPW);
            ArrayList<String> styles = new ArrayList<String>();
            // add the user ones first
            RESTStyleList stylesResponse = reader.getStyles(workspace);
            if(stylesResponse != null) {
                for(String styleName : stylesResponse.getNames()) {
                    styles.add(workspace + ":" + styleName);
                }
            }
            stylesResponse = reader.getStyles();
            if(stylesResponse != null) {
                styles.addAll(stylesResponse.getNames());
/*
                styles.addAll(ListUtil.filterValues(stylesResponse.getNames(), new ListUtil.CheckValue<String>() {
                    @Override
                    public boolean isValue(String value) {
                        return !(value.contains("___") && !value.startsWith(workspace + "___"));
                    }
                }));
*/
            }
            return styles;
        } catch (Exception e) {
            throw new RequestException("Failed to call geoserver service");
        }
    }

    @Override
    public SupplierSettingsDTO getSettings() throws RequestException {
        String logUserName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, logUserName);
            SupplierSettings settings = user.getCompany().getSettings();
            SupplierSettingsDTO supplierSettingsDTO = new SupplierSettingsDTO();
            supplierSettingsDTO.setNotificationDelayMessages(settings.getNotificationDelayMessages());
            supplierSettingsDTO.setNotificationDelayRequests(settings.getNotificationDelayRequests());
            return supplierSettingsDTO;
        } catch (Exception e) {
            throw new RequestException("Problem retrieving settings");
        } finally {
            em.close();
        }
    }

    @Override
    public void saveSettings(SupplierSettingsDTO supplierSettings) throws RequestException {
        String logUserName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, logUserName);
            SupplierSettings dbSupplierSettings = user.getCompany().getSettings();
            dbSupplierSettings.setNotificationDelayMessages(supplierSettings.getNotificationDelayMessages());
            dbSupplierSettings.setNotificationDelayRequests(supplierSettings.getNotificationDelayRequests());
            em.getTransaction().commit();
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RequestException("Problem retrieving settings");
        } finally {
            em.close();
        }
    }

    @Override
    public List<SuccessStoryDTO> getSuccessStories() throws RequestException {
        String logUserName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, logUserName);
            return ListUtil.mutate(user.getCompany().getSuccessStories(), new ListUtil.Mutate<SuccessStory, SuccessStoryDTO>() {
                @Override
                public SuccessStoryDTO mutate(SuccessStory successStory) {
                    SuccessStoryDTO successStoryDTO = new SuccessStoryDTO();
                    successStoryDTO.setName(successStory.getName());
                    successStoryDTO.setDescription(successStory.getDescription());
                    successStoryDTO.setDate(successStory.getDate());
                    return successStoryDTO;
                }
            });
        } catch (Exception e) {
            throw new RequestException("Problem retrieving success stories");
        } finally {
            em.close();
        }
    }

    @Override
    public SuccessStoryEditDTO getSuccessStory(final Long id) throws RequestException {
        String logUserName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, logUserName);
            SuccessStory successStory = ListUtil.findValue(user.getCompany().getSuccessStories(), new ListUtil.CheckValue<SuccessStory>() {
                @Override
                public boolean isValue(SuccessStory value) {
                    return value.getId().equals(id);
                }
            });
            if(successStory == null) {
                throw new RequestException("Could not find success story");
            }
            SuccessStoryEditDTO successStoryEditDTO = new SuccessStoryEditDTO();
            successStoryEditDTO.setName(successStory.getName());
            successStoryEditDTO.setDescription(successStory.getDescription());
            successStoryEditDTO.setCustomer(CompanyHelper.createCompanyDTO(successStory.getCustomer()));
            successStoryEditDTO.setDate(successStory.getDate());
            successStoryEditDTO.setFullDescription(successStory.getFullDescription());
            return successStoryEditDTO;
        } catch (Exception e) {
            throw new RequestException("Problem retrieving success stories");
        } finally {
            em.close();
        }
    }

    @Override
    public Long updateSuccessStory(SuccessStoryEditDTO successStoryEditDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            SuccessStory successStory = null;
            if(successStoryEditDTO.getId() == null) {
                successStory = new SuccessStory();
                successStory.setSupplier(user.getCompany());
                em.persist(successStory);
                user.getCompany().getSuccessStories().add(successStory);
            } else {
                successStory = em.find(SuccessStory.class, successStoryEditDTO.getId());
                if(successStory == null) {
                    throw new RequestException("Could not find success story");
                }
                if(user.getCompany() != successStory.getSupplier()) {
                    throw new RequestException("Not allowed");
                }
            }
            // update values
            successStory.setImageUrl(successStoryEditDTO.getImageUrl());
            successStory.setName(successStoryEditDTO.getName());
            successStory.setDescription(successStoryEditDTO.getDescription());
            successStory.setDate(successStory.getDate());
            successStory.setFullDescription(successStoryEditDTO.getFullDescription());
            em.getTransaction().commit();
            return successStory.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error saving success story");
        } finally {
            em.close();
        }
    }

    @Override
    public List<TestimonialDTO> getTestimonials() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            TypedQuery<Testimonial> query = em.createQuery("select t from Testimonial t where t.company = :company", Testimonial.class);
            query.setParameter("company", user.getCompany());
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<Testimonial, TestimonialDTO>() {
                @Override
                public TestimonialDTO mutate(Testimonial testimonial) {
                    TestimonialDTO testimonialDTO = new TestimonialDTO();
                    testimonialDTO.setId(testimonial.getId());
                    testimonialDTO.setFromUser(UserHelper.createUserDTO(testimonial.getFromUser()));
                    testimonialDTO.setTestimonial(testimonial.getTestimonial());
                    testimonialDTO.setCreationDate(testimonial.getCreationDate());
                    return testimonialDTO;
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error saving success story");
        } finally {
            em.close();
        }
    }

    @Override
    public SupplierStatisticsDTO getStatistics() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            SupplierStatisticsDTO supplierStatisticsDTO = new SupplierStatisticsDTO();
            // collect some information
            List<Product> products = new ArrayList<Product>();
            for(ProductService productService : user.getCompany().getServices()) {
                products.add(productService.getProduct());
            }
            for(ProductDataset productDataset : user.getCompany().getDatasets()) {
                products.add(productDataset.getProduct());
            }
            // create a dummy one to illustrate the purpose
            BarChartStatistics barChartStatistics = new BarChartStatistics();
            barChartStatistics.setName("Product searches");
            barChartStatistics.setDescription("Number Searches involving your product categories in the past month");
            barChartStatistics.setxLabel("Product category");
            barChartStatistics.setyLabel("Number of searches");
            Map<String, Double> values = new HashMap<String, Double>();
            for(Product product : products) {
                values.put(product.getName(), Math.floor(Math.random() * 100));
            }
            barChartStatistics.setValues(values);
            supplierStatisticsDTO.setStatistics(ListUtil.toList(barChartStatistics));
            return supplierStatisticsDTO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error getting statistics");
        } finally {
            em.close();
        }
    }

    @Override
    public void removeProductDataset(Long id) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            ProductDataset productDataset = em.find(ProductDataset.class, id);
            if(productDataset == null || user.getCompany() != productDataset.getCompany()) {
                throw new RequestException("Not authorised");
            }
            productDataset.getCompany().getDatasets().remove(productDataset);
            em.remove(productDataset);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error removing dataset");
        } finally {
            em.close();
        }
    }

    @Override
    public List<SupplierNotificationDTO> getNotifications() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<SupplierNotification> query = em.createQuery("select s from SupplierNotification s where s.company = :company order by s.creationDate DESC", SupplierNotification.class);
            query.setParameter("company", user.getCompany());
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<SupplierNotification, SupplierNotificationDTO>() {
                @Override
                public SupplierNotificationDTO mutate(SupplierNotification supplierNotification) {
                    return createNotificationDTO(supplierNotification);
                }
            });
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException("Error loading notifications");
        } finally {
            em.close();
        }
    }

    private SupplierNotificationDTO createNotificationDTO(SupplierNotification supplierNotification) {
        SupplierNotificationDTO supplierNotificationDTO = new SupplierNotificationDTO();
        supplierNotificationDTO.setType(supplierNotification.getType());
        supplierNotificationDTO.setMessage(supplierNotification.getMessage());
        supplierNotificationDTO.setLinkId(supplierNotification.getLinkId());
        supplierNotificationDTO.setCreationDate(supplierNotification.getCreationDate());
        return supplierNotificationDTO;
    }

    @Override
    public SupplierNotificationDTO getNotification(Long id) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            SupplierNotification notification = em.find(SupplierNotification.class, id);
            if(notification.getCompany() != user.getCompany()) {
                throw new RequestException("Not authorised");
            }
            return createNotificationDTO(notification);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error loading notification");
        } finally {
            em.close();
        }
    }

    private DatasetProviderDTO createDatasetProviderDTO(DatasetProvider datasetProvider) {
        DatasetProviderDTO datasetProviderDTO = new DatasetProviderDTO();
        datasetProviderDTO.setId(datasetProvider.getId());
        datasetProviderDTO.setName(datasetProvider.getName());
        datasetProviderDTO.setIconURL(datasetProvider.getIconUrl());
        datasetProviderDTO.setUri(datasetProvider.getUri());
        datasetProviderDTO.setExtent(datasetProvider.getExtent());
        return datasetProviderDTO;
    }

    @Override
    public List<DatasetProviderDTO> listDatasets() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<DatasetProvider> query = em.createQuery("select d from DatasetProvider d where d.company = :company", DatasetProvider.class);
            query.setParameter("company", user.getCompany());
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<DatasetProvider, DatasetProviderDTO>() {
                @Override
                public DatasetProviderDTO mutate(DatasetProvider datasetProvider) {
                    return createDatasetProviderDTO(datasetProvider);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException("Error loading notifications");
        } finally {
            em.close();
        }
    }

    @Override
    public DatasetProviderDTO getDatasetProvider(Long id) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            DatasetProvider datasetProvider = em.find(DatasetProvider.class, id);
            User user = em.find(User.class, userName);
            if(datasetProvider == null) {
                throw new RequestException("Dataset does not exist");
            }
            if(datasetProvider.getCompany() != user.getCompany()) {
                throw new RequestException("Not allowed");
            }
            return createDatasetProviderDTO(datasetProvider);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error loading notifications");
        } finally {
            em.close();
        }
    }

    @Override
    public Long saveDatasetProvider(DatasetProviderDTO datasetProviderDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            DatasetProvider datasetProvider = null;
            if(datasetProviderDTO.getId() == null) {
                datasetProvider = new DatasetProvider();
                datasetProvider.setName(datasetProviderDTO.getName());
                datasetProvider.setUri(datasetProviderDTO.getUri());
                datasetProvider.setIconUrl(datasetProviderDTO.getIconURL());
                datasetProvider.setExtent(datasetProviderDTO.getExtent());
                datasetProvider.setCompany(user.getCompany());
                em.persist(datasetProvider);
            } else {
                datasetProvider = em.find(DatasetProvider.class, datasetProviderDTO.getId());
                if(datasetProvider == null) {
                    throw new RequestException("Could not find the dataset");
                }
                if(user.getCompany() != datasetProvider.getCompany()) {
                    throw new RequestException("Not allowed");
                }
                datasetProvider.setName(datasetProviderDTO.getName());
                datasetProvider.setUri(datasetProviderDTO.getUri());
                datasetProvider.setIconUrl(datasetProviderDTO.getIconURL());
                datasetProvider.setExtent(datasetProviderDTO.getExtent());
            }
            em.getTransaction().commit();
            return datasetProvider.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error saving dataset");
        } finally {
            em.close();
        }
    }

    private ProductDatasetDTO createProductDatasetDTO(ProductDataset productDataset) {
        ProductDatasetDTO productDatasetDTO = new ProductDatasetDTO();
        productDatasetDTO.setId(productDataset.getId());
        productDatasetDTO.setName(productDataset.getName());
        productDatasetDTO.setImageUrl(productDataset.getImageUrl());
        productDatasetDTO.setDescription(productDataset.getDescription());
        productDatasetDTO.setCompany(CompanyHelper.createCompanyDTO(productDataset.getCompany()));
        return productDatasetDTO;
    }

    @Override
    public List<ProductDatasetDTO> listProductDatasets() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<ProductDataset> query = em.createQuery("select p from ProductDataset p where p.company = :company", ProductDataset.class);
            query.setParameter("company", user.getCompany());
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<ProductDataset, ProductDatasetDTO>() {
                @Override
                public ProductDatasetDTO mutate(ProductDataset productDataset) {
                    return createProductDatasetDTO(productDataset);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException("Error loading product datasets");
        } finally {
            em.close();
        }
    }

    @Override
    public ProductDatasetDTO getProductDataset(Long id) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductDataset productDataset = em.find(ProductDataset.class, id);
            User user = em.find(User.class, userName);
            if(productDataset == null) {
                throw new RequestException("Dataset does not exist");
            }
            if(productDataset.getCompany() != user.getCompany()) {
                throw new RequestException("Not allowed");
            }
            ProductDatasetDTO productDatasetDTO = createProductDatasetDTO(productDataset);
            productDatasetDTO.setFullDescription(productDataset.getFullDescription());
/*
            productDatasetDTO.setProduct(ProductHelper.createProductDTO(productDataset.getProduct()));
            productDatasetDTO.setProductFeatures(productDataset.getProduct() == null ? null : productDataset.getProduct().getGeoinformation());
            productDatasetDTO.setSelectedFeatures(ListUtil.mutate(productDataset.getGeoinformation(), new ListUtil.Mutate<FeatureDescription, Long>() {
                @Override
                public Long mutate(FeatureDescription featureDescription) {
                    return featureDescription.getId();
                }
            }));
*/
            if(productDataset.getProduct() != null) {
                productDatasetDTO.setProduct(ProductHelper.createProductDTO(productDataset.getProduct()));
                productDatasetDTO.setProductFeatures(productDataset.getProduct().getGeoinformation());
                productDatasetDTO.setSelectedFeatures(ListUtil.mutate(productDataset.getGeoinformation(), new ListUtil.Mutate<FeatureDescription, Long>() {
                    @Override
                    public Long mutate(FeatureDescription featureDescription) {
                        return featureDescription.getId();
                    }
                }));
                productDatasetDTO.setGeoinformationComment(productDataset.getGeoinformationComment());
                productDatasetDTO.setPerformances(productDataset.getProduct().getPerformances());
                productDatasetDTO.setProvidedPerformances(productDataset.getPerformances());
                productDatasetDTO.setPerformancesComment(productDataset.getPerformancesComment());
            }
            productDatasetDTO.setTemporalCoverage(productDataset.getTemporalCoverage());
            productDatasetDTO.setTemporalCoverageComment(productDataset.getTemporalCoverageComment());
            productDatasetDTO.setExtent(productDataset.getExtent());
            productDatasetDTO.setCoverageLayers(productDataset.getCoverageLayers());
            productDatasetDTO.setDatasetAccesses(productDataset.getDatasetAccesses());
            productDatasetDTO.setSamples(productDataset.getSamples());
            productDatasetDTO.setDatasetStandard(productDataset.getDatasetStandard());
            productDatasetDTO.setDatasetURL(productDataset.getDatasetURL());
            productDatasetDTO.setTermsAndConditions(productDataset.getTermsAndConditions());
            return productDatasetDTO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error loading dataset");
        } finally {
            em.close();
        }
    }

    @Override
    public Long updateProductDataset(final ProductDatasetDTO productDatasetDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        boolean newDataset = productDatasetDTO.getId() == null;
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            ProductDataset productDataset = null;
            if(newDataset) {
                productDataset = new ProductDataset();
                productDataset.setCompany(user.getCompany());
                em.persist(productDataset);
                user.getCompany().getDatasets().add(productDataset);
            } else {
                productDataset = em.find(ProductDataset.class, productDatasetDTO.getId());
                if(productDataset == null) {
                    throw new RequestException("Could not find the dataset");
                }
                if(user.getCompany() != productDataset.getCompany()) {
                    throw new RequestException("Not allowed");
                }
            }
            // update values
            productDataset.setName(productDatasetDTO.getName());
            productDataset.setDescription(productDatasetDTO.getDescription());
            productDataset.setFullDescription(productDatasetDTO.getFullDescription());
            productDataset.setServiceType(productDatasetDTO.getServiceType());
            Product product = em.find(Product.class, productDatasetDTO.getProduct().getId());
            if(product == null) {
                throw new RequestException("Product does not exist");
            }
            // if it was previously assigned to a different product, make sure we remove the service from the product first
            if(productDataset.getProduct() != null) {
                productDataset.getProduct().getProductDatasets().remove(productDataset);
            }
            productDataset.setProduct(product);
            productDataset.setImageUrl(productDatasetDTO.getImageUrl());
/*
            String extentWKT = productDatasetDTO.getExtent();
            if(extentWKT == null) {
                Extent extent = new Extent();
                extent.setSouth(-90.0);
                extent.setNorth(90.0);
                extent.setEast(180.0);
                extent.setWest(-180.0);
                extentWKT = "POLYGON((" +
                        extent.getEast() + " " + extent.getNorth() + "," +
                        extent.getWest() + " " + extent.getNorth() + "," +
                        extent.getWest() + " " + extent.getSouth() + "," +
                        extent.getEast() + " " + extent.getSouth() + "," +
                        extent.getEast() + " " + extent.getNorth() +
                        "))";
            }
*/
            productDataset.setExtent(productDatasetDTO.getExtent());
            // update the coverage layers
            List<DatasetAccessOGC> dbCoverageLayers = updateCoverageLayers(em, productDataset.getCoverageLayers(), productDatasetDTO.getCoverageLayers());
            productDataset.setCoverageLayers(dbCoverageLayers);
            // set selected features
            productDataset.setGeoinformation(ListUtil.filterValues(productDataset.getProduct().getGeoinformation(), new ListUtil.CheckValue<FeatureDescription>() {
                @Override
                public boolean isValue(FeatureDescription value) {
                    return productDatasetDTO.getSelectedFeatures().contains(value.getId());
                }
            }));
            productDataset.setGeoinformationComment(productDatasetDTO.getGeoinformationComment());
            List<PerformanceValue> performances = updatePerformances(em, productDataset.getPerformances(), productDatasetDTO.getProvidedPerformances());
            productDataset.setPerformances(performances);
            productDataset.setPerformancesComment(productDatasetDTO.getPerformancesComment());
            productDataset.setTemporalCoverage(productDatasetDTO.getTemporalCoverage());
            productDataset.setTemporalCoverageComment(productDatasetDTO.getTemporalCoverageComment());
            // update the data access
            {
                List<DatasetAccess> datasetAccesses = productDatasetDTO.getDatasetAccesses();
                List<DatasetAccess> dbDatasetAccesses = new ArrayList<DatasetAccess>();
                if (datasetAccesses != null && datasetAccesses.size() > 0) {
                    for (final DatasetAccess datasetAccess : datasetAccesses) {
                        DatasetAccess dbDatasetAccess = null;
                        if (datasetAccess.getId() != null) {
                            dbDatasetAccess = ListUtil.findValue(productDataset.getDatasetAccesses(), new ListUtil.CheckValue<DatasetAccess>() {
                                @Override
                                public boolean isValue(DatasetAccess value) {
                                    return value.getId().equals(datasetAccess.getId());
                                }
                            });
                        }
                        if (dbDatasetAccess == null) {
                            em.persist(datasetAccess);
                            dbDatasetAccess = datasetAccess;
                        }
                        dbDatasetAccess.setTitle(datasetAccess.getTitle());
                        dbDatasetAccess.setPitch(datasetAccess.getPitch());
                        dbDatasetAccess.setUri(datasetAccess.getUri());
                        dbDatasetAccess.setSize(datasetAccess.getSize());
                        // now do some data access specific stuff
                        if (dbDatasetAccess instanceof DatasetAccessOGC) {
                            DatasetAccessOGC dbDatasetAccessOGC = (DatasetAccessOGC) dbDatasetAccess;
                            DatasetAccessOGC datasetAccessOGC = (DatasetAccessOGC) datasetAccess;
                            // check if style has changed
                            if (!StringUtils.areStringEqualsOrNull(dbDatasetAccessOGC.getStyleName(), ((DatasetAccessOGC) datasetAccess).getStyleName())) {
                                // update db and update geoserver
                                try {
                                    dbDatasetAccessOGC.setStyleName(datasetAccessOGC.getStyleName());
                                } catch (Exception e) {

                                }
                            }
                            dbDatasetAccessOGC.setServerUrl(datasetAccessOGC.getServerUrl());
                            dbDatasetAccessOGC.setLayerName(datasetAccessOGC.getLayerName());
                            dbDatasetAccessOGC.setWcsServerUrl(datasetAccessOGC.getWcsServerUrl());
                            dbDatasetAccessOGC.setWcsResourceName(datasetAccessOGC.getWcsResourceName());
                            dbDatasetAccessOGC.setCorsEnabled(datasetAccessOGC.isCorsEnabled());
                        }
                        dbDatasetAccesses.add(dbDatasetAccess);
                    }
                }
                productDataset.setDatasetAccesses(dbDatasetAccesses);
            }
            // update the sample access
            List<DatasetAccess> dbSamples = updateSamples(em, productDataset.getSamples(), productDatasetDTO.getSamples());
            productDataset.setSamples(dbSamples);
            productDataset.setDatasetStandard(productDatasetDTO.getDatasetStandard());
            productDataset.setDatasetURL(productDatasetDTO.getDatasetURL());
            productDataset.setTermsAndConditions(productDatasetDTO.getTermsAndConditions());
            // update the keyphrases
            Query query = em.createNativeQuery("UPDATE productdataset SET tsv = " + DBHelper.getProductDatasetTSV(productDataset) +
                    ", tsvname = " + DBHelper.getProductDatasetNameTSV(productDataset) + " where id = " + productDataset.getId() +
                    ";");
            query.executeUpdate();
            em.getTransaction().commit();
            if(newDataset) {
                // fail silently
                try {
                    em.getTransaction().begin();
                    // add event for company and for product
                    EventHelper.createAndPropagateCompanyEvent(em, user.getCompany(), Category.productdatasets, Event.TYPE.OFFER,
                            "New off the shelf data available for product " + product.getName(), productDataset.getId() + "");
                    EventHelper.createAndPropagateProductEvent(em, product, user.getCompany(), Category.productdatasets, Event.TYPE.OFFER,
                            "New off the shelf data available for product " + product.getName(), productDataset.getId() + "");
                    em.getTransaction().commit();
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    logger.error(e.getMessage(), e);
                }
            }
            return productDataset.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error saving dataset");
        } finally {
            em.close();
        }
    }

    private SoftwareDTO createSoftwareDTO(Software software) {
        SoftwareDTO softwareDTO = new SoftwareDTO();
        softwareDTO.setId(software.getId());
        softwareDTO.setName(software.getName());
        softwareDTO.setImageUrl(software.getImageUrl());
        softwareDTO.setDescription(software.getDescription());
        softwareDTO.setSoftwareType(software.getSoftwareType());
        return softwareDTO;
    }

    @Override
    public List<SoftwareDTO> listSoftwares() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<Software> query = em.createQuery("select s from Software s where s.company = :company", Software.class);
            query.setParameter("company", user.getCompany());
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<Software, SoftwareDTO>() {
                @Override
                public SoftwareDTO mutate(Software software) {
                    return createSoftwareDTO(software);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException("Error loading product datasets");
        } finally {
            em.close();
        }
    }

    @Override
    public SoftwareDTO getSoftware(Long id) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            Software software = em.find(Software.class, id);
            User user = em.find(User.class, userName);
            if(software == null) {
                throw new RequestException("Dataset does not exist");
            }
            if(software.getCompany() != user.getCompany()) {
                throw new RequestException("Not allowed");
            }
            SoftwareDTO softwareDTO = createSoftwareDTO(software);
            softwareDTO.setFullDescription(software.getFullDescription());
            softwareDTO.setProducts(ListUtil.mutate(software.getProducts(), new ListUtil.Mutate<ProductSoftware, ProductSoftwareDTO>() {
                @Override
                public ProductSoftwareDTO mutate(ProductSoftware productSoftware) {
                    return createProductSoftwareDTO(productSoftware);
                }
            }));
            softwareDTO.setTermsAndConditions(software.getTermsAndConditions());
            return softwareDTO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error loading notifications");
        } finally {
            em.close();
        }
    }

    private ProductSoftwareDTO createProductSoftwareDTO(ProductSoftware productSoftware) {
        ProductSoftwareDTO productSoftwareDTO = new ProductSoftwareDTO();
        productSoftwareDTO.setId(productSoftware.getId());
        productSoftwareDTO.setPitch(productSoftware.getPitch());
        productSoftwareDTO.setProduct(ProductHelper.createProductDTO(productSoftware.getProduct()));
        return productSoftwareDTO;
    }

    @Override
    public Long updateSoftware(SoftwareDTO softwareDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        boolean newSoftware = softwareDTO.getId() == null;
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Software software = null;
            if(newSoftware) {
                software = new Software();
                software.setCompany(user.getCompany());
                em.persist(software);
                user.getCompany().getSoftware().add(software);
            } else {
                software = em.find(Software.class, softwareDTO.getId());
                if(software == null) {
                    throw new RequestException("Could not find the dataset");
                }
                if(user.getCompany() != software.getCompany()) {
                    throw new RequestException("Not allowed");
                }
            }
            // update values
            software.setName(softwareDTO.getName());
            software.setDescription(softwareDTO.getDescription());
            software.setFullDescription(softwareDTO.getFullDescription());
            ArrayList<ProductSoftware> productSoftwares = new ArrayList<ProductSoftware>();
            for(final ProductSoftwareDTO productSoftwareDTO : softwareDTO.getProducts()) {
                ProductSoftware dbProductSoftware = null;
                if(productSoftwareDTO.getId() != null) {
                    dbProductSoftware = ListUtil.findValue(software.getProducts(), new ListUtil.CheckValue<ProductSoftware>() {
                        @Override
                        public boolean isValue(ProductSoftware value) {
                            return value.getId().equals(productSoftwareDTO.getId());
                        }
                    });
                }
                if(dbProductSoftware == null) {
                    dbProductSoftware = new ProductSoftware();
                    em.persist(dbProductSoftware);
                }
                Product product = em.find(Product.class, productSoftwareDTO.getProduct().getId());
                if(product == null) {
                    throw new RequestException("Could not find product with id " + productSoftwareDTO.getProduct().getId());
                }
                dbProductSoftware.setPitch(productSoftwareDTO.getPitch());
                dbProductSoftware.setProduct(product);
                productSoftwares.add(dbProductSoftware);
            }
            software.setProducts(productSoftwares);
            software.setImageUrl(softwareDTO.getImageUrl());
            software.setSoftwareType(softwareDTO.getSoftwareType());
            software.setTermsAndConditions(softwareDTO.getTermsAndConditions());
            // update the keyphrases
            Query query = em.createNativeQuery("UPDATE software SET tsv = " + DBHelper.getSoftwareTSV(software) +
                    ", tsvname = " + DBHelper.getSoftwareNameTSV(software) + " where id = " + software.getId() +
                    ";");
            query.executeUpdate();
            em.getTransaction().commit();
            // add notifications
            if(newSoftware) {
                // fail silently
                try {
                    em.getTransaction().begin();
                    // add event for company and for product
                    EventHelper.createAndPropagateCompanyEvent(em, user.getCompany(),
                            Category.software, Event.TYPE.OFFER,
                            "New software available", software.getId() + "");
                    for(ProductSoftware productSoftware : software.getProducts()) {
                        EventHelper.createAndPropagateProductEvent(em, productSoftware.getProduct(), user.getCompany(),
                                Category.software, Event.TYPE.OFFER,
                                "New software available for product " + productSoftware.getProduct().getName(), software.getId() + "");
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    logger.error(e.getMessage(), e);
                }
            }
            return software.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error saving software");
        } finally {
            em.close();
        }
    }

    @Override
    public void removeSoftware(Long id) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Software software = em.find(Software.class, id);
            if(software == null || user.getCompany() != software.getCompany()) {
                throw new RequestException("Not authorised");
            }
            software.getCompany().getSoftware().remove(software);
            em.remove(software);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error removing software");
        } finally {
            em.close();
        }
    }

    private ProjectDTO createProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setImageUrl(project.getImageUrl());
        projectDTO.setDescription(project.getDescription());
        return projectDTO;
    }

    @Override
    public List<ProjectDTO> listProjects() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<Project> query = em.createQuery("select p from Project p where p.company = :company", Project.class);
            query.setParameter("company", user.getCompany());
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<Project, ProjectDTO>() {
                @Override
                public ProjectDTO mutate(Project project) {
                    return createProjectDTO(project);
                }
            });
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException("Error loading product datasets");
        } finally {
            em.close();
        }
    }

    @Override
    public ProjectDTO getProject(Long id) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            Project project = em.find(Project.class, id);
            User user = em.find(User.class, userName);
            if(project == null) {
                throw new RequestException("Dataset does not exist");
            }
            if(project.getCompany() != user.getCompany()) {
                throw new RequestException("Not allowed");
            }
            ProjectDTO projectDTO = createProjectDTO(project);
            projectDTO.setFrom(project.getStartDate());
            projectDTO.setUntil(project.getStopDate());
            projectDTO.setFullDescription(project.getFullDescription());
            projectDTO.setProducts(ListUtil.mutate(project.getProducts(), new ListUtil.Mutate<ProductProject, ProductProjectDTO>() {
                @Override
                public ProductProjectDTO mutate(ProductProject productProject) {
                    return createProductProjectDTO(productProject);
                }
            }));
            projectDTO.setConsortium(ListUtil.mutate(project.getConsortium(), new ListUtil.Mutate<CompanyRole, CompanyRoleDTO>() {
                @Override
                public CompanyRoleDTO mutate(CompanyRole companyRole) {
                    CompanyRoleDTO companyRoleDTO = new CompanyRoleDTO();
                    companyRoleDTO.setId(companyRole.getId());
                    companyRoleDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(companyRole.getCompany()));
                    companyRoleDTO.setRole(companyRole.getRole());
                    return companyRoleDTO;
                }
            }));
            return projectDTO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error loading notifications");
        } finally {
            em.close();
        }
    }

    private ProductProjectDTO createProductProjectDTO(ProductProject productProject) {
        ProductProjectDTO productProjectDTO = new ProductProjectDTO();
        productProjectDTO.setId(productProject.getId());
        productProjectDTO.setPitch(productProject.getPitch());
        productProjectDTO.setProduct(ProductHelper.createProductDTO(productProject.getProduct()));
        return productProjectDTO;
    }

    @Override
    public Long updateProject(ProjectDTO projectDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        boolean newProject = projectDTO.getId() == null;
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Project project = null;
            if(newProject) {
                project = new Project();
                project.setCompany(user.getCompany());
                em.persist(project);
                user.getCompany().getProjects().add(project);
            } else {
                project = em.find(Project.class, projectDTO.getId());
                if(project == null) {
                    throw new RequestException("Could not find the project");
                }
                if(user.getCompany() != project.getCompany()) {
                    throw new RequestException("Not allowed");
                }
            }
            // update values
            project.setName(projectDTO.getName());
            project.setDescription(projectDTO.getDescription());
            project.setStartDate(projectDTO.getFrom());
            project.setStopDate(projectDTO.getUntil());
            project.setFullDescription(projectDTO.getFullDescription());

            // set project products
            ArrayList<ProductProject> productProjects = new ArrayList<ProductProject>();
            for(final ProductProjectDTO productProjectDTO : projectDTO.getProducts()) {
                ProductProject dbProductProject = null;
                if(productProjectDTO.getId() != null) {
                    dbProductProject = ListUtil.findValue(project.getProducts(), new ListUtil.CheckValue<ProductProject>() {
                        @Override
                        public boolean isValue(ProductProject value) {
                            return value.getId().equals(productProjectDTO.getId());
                        }
                    });
                }
                if(dbProductProject == null) {
                    dbProductProject = new ProductProject();
                    em.persist(dbProductProject);
                }
                Product product = em.find(Product.class, productProjectDTO.getProduct().getId());
                if(product == null) {
                    throw new RequestException("Could not find product with id " + productProjectDTO.getProduct().getId());
                }
                dbProductProject.setPitch(productProjectDTO.getPitch());
                dbProductProject.setProduct(product);
                dbProductProject.setProject(project);
                productProjects.add(dbProductProject);
            }
            project.setProducts(productProjects);

            // set project products
            ArrayList<CompanyRole> consortium = new ArrayList<CompanyRole>();
            for(final CompanyRoleDTO companyRoleDTO : projectDTO.getConsortium()) {
                CompanyRole dbCompanyRole = null;
                if(companyRoleDTO.getId() != null) {
                    dbCompanyRole = ListUtil.findValue(project.getConsortium(), new ListUtil.CheckValue<CompanyRole>() {
                        @Override
                        public boolean isValue(CompanyRole value) {
                            return value.getId().equals(companyRoleDTO.getId());
                        }
                    });
                }
                if(dbCompanyRole == null) {
                    dbCompanyRole = new CompanyRole();
                    em.persist(dbCompanyRole);
                }
                Company company = em.find(Company.class, companyRoleDTO.getCompanyDTO().getId());
                if(company == null) {
                    throw new RequestException("Could not find company with id " + companyRoleDTO.getId());
                }
                dbCompanyRole.setRole(companyRoleDTO.getRole());
                dbCompanyRole.setCompany(company);
                dbCompanyRole.setProject(project);
                consortium.add(dbCompanyRole);
            }
            project.setConsortium(consortium);

            project.setImageUrl(projectDTO.getImageUrl());
            // update the keyphrases
            Query query = em.createNativeQuery("UPDATE project SET tsv = " + DBHelper.getProjectTSV(project) +
                    ", tsvname = " + DBHelper.getProjectNameTSV(project) + " where id = " + project.getId() +
                    ";");
            query.executeUpdate();
            em.getTransaction().commit();
            // add notifications
            if(newProject) {
                // fail silently
                try {
                    em.getTransaction().begin();
                    // add event for company and for product
                    EventHelper.createAndPropagateCompanyEvent(em, user.getCompany(),
                            Category.project, Event.TYPE.OFFER,
                            "New project " + project.getName() + " created", project.getId() + "");
                    for(ProductProject productProject : project.getProducts()) {
                        EventHelper.createAndPropagateProductEvent(em, productProject.getProduct(), user.getCompany(),
                                Category.project, Event.TYPE.OFFER,
                                "New project " + project.getName() + " created", project.getId() + "");
                    }
                    em.getTransaction().commit();
                } catch (Exception e) {
                    if (em.getTransaction().isActive()) {
                        em.getTransaction().rollback();
                    }
                    logger.error(e.getMessage(), e);
                }
            }
            return project.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error saving project");
        } finally {
            em.close();
        }
    }

    @Override
    public void removeProject(Long id) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Project project = em.find(Project.class, id);
            if(project == null || user.getCompany() != project.getCompany()) {
                throw new RequestException("Not authorised");
            }
            project.getCompany().getProjects().remove(project);
            em.remove(project);
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error removing project");
        } finally {
            em.close();
        }
    }

    @Override
    public OfferDTO getOffer(Category category) throws RequestException {
        OfferDTO offerDTO = new OfferDTO();
        switch (category) {
            case companies:
                offerDTO.setCompanyDTO(getCompany());
                break;
            case productservices:
                offerDTO.setProductServiceDTOs(listProductServices());
                break;
            case productdatasets:
                offerDTO.setProductDatasetDTOs(listProductDatasets());
                break;
            case software:
                offerDTO.setSoftwareDTOs(listSoftwares());
                break;
            case project:
                offerDTO.setProjectDTOs(listProjects());
                break;
        }
        return offerDTO;
    }

}
