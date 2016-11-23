package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.AuthorizationException;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.DatasetProvider;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIPolygonDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import com.geocento.webapps.eobroker.supplier.client.services.AssetsService;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.geocento.webapps.eobroker.supplier.shared.utils.ProductHelper;
import com.google.gwt.http.client.RequestException;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

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
    public List<FeatureDescription> getProductGeoinformation(Long productId) throws RequestException {
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
            return product.getGeoinformation();
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
            productServiceDTO.setProduct(productService.getProduct() == null ? null : ProductHelper.createProductDTO(productService.getProduct()));
            productServiceDTO.setProductFeatures(productService.getProduct() == null ? null : productService.getProduct().getGeoinformation());
            productServiceDTO.setSelectedFeatures(ListUtil.mutate(productService.getGeoinformation(), new ListUtil.Mutate<FeatureDescription, Long>() {
                @Override
                public Long mutate(FeatureDescription featureDescription) {
                    return featureDescription.getId();
                }
            }));
            productServiceDTO.setApiURL(productService.getApiUrl());
            productServiceDTO.setSelectedDataAccessTypes(productService.getSelectedAccessTypes());
            productServiceDTO.setSamples(productService.getSamples());
            productServiceDTO.setSampleWmsUrl(productService.getSampleWmsUrl());
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
    public void updateProductService(final ProductServiceEditDTO productServiceDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        if(productServiceDTO == null ) {
            throw new RequestException("Product service cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            ProductService productService = null;
            User user = em.find(User.class, userName);
            if(productServiceDTO.getId() != null) {
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
            productService.setGeoinformation(ListUtil.filterValues(productService.getProduct().getGeoinformation(), new ListUtil.CheckValue<FeatureDescription>() {
                @Override
                public boolean isValue(FeatureDescription value) {
                    return productServiceDTO.getSelectedFeatures().contains(value.getId());
                }
            }));
            productService.setSelectedAccessTypes(productServiceDTO.getSelectedDataAccessTypes());
            productService.setApiUrl(productServiceDTO.getApiURL());
            // update the sample access
            {
                List<DatasetAccess> samples = productServiceDTO.getSamples();
                List<DatasetAccess> dbSamples = new ArrayList<DatasetAccess>();
                if (samples != null && samples.size() > 0) {
                    for (final DatasetAccess datasetAccess : samples) {
                        DatasetAccess dbDatasetAccess = null;
                        if (datasetAccess.getId() != null) {
                            dbDatasetAccess = ListUtil.findValue(productService.getSamples(), new ListUtil.CheckValue<DatasetAccess>() {
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
                        dbSamples.add(dbDatasetAccess);
                    }
                }
                productService.setSamples(dbSamples);
            }
            // update the keyphrases
            Query query = em.createNativeQuery("UPDATE productservice SET tsv = " + getProductServiceTSV(productService) +
                    ", tsvname = " + getProductServiceNameTSV(productService) + " where id = " + productService.getId() +
                    ";");
            query.executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException("Error updating product service");
        } finally {
            em.close();
        }
    }

    private static String getProductServiceNameTSV(ProductService productService) {
        return "setweight(to_tsvector('english','service on-demand'), 'A') || setweight(to_tsvector('english','" + productService.getName() + "'), 'B')";
    }

    private static String getProductServiceTSV(ProductService productService) {
        return "setweight(to_tsvector('english','service on-demand " + productService.getName() + "'), 'A') " +
                "|| setweight(to_tsvector('english','" + productService.getDescription() + "'), 'B')";
    }

    @Override
    public CompanyDTO getCompany() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            Company company = null;
            User user = em.find(User.class, userName);
            company = user.getCompany();
            if (company == null) {
                throw new RequestException("No company assigned!");
            }
            return CompanyHelper.createCompanyDTO(company);
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
            company.setDescription(companyDTO.getDescription());
            company.setWebsite(companyDTO.getWebsite());
            company.setContactEmail(companyDTO.getContactEmail());
            company.setFullDescription(companyDTO.getFullDescription());
            company.setIconURL(companyDTO.getIconURL());
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
    public List<ProductDTO> findProducts(String text) {
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
        // change the last word so that it allows for partial match
        String sqlStatement = "SELECT id, \"name\", ts_rank(tsvname, keywords, 8) AS rank, id\n" +
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
            suggestions.add(productDTO);
        }
        return suggestions;
    }

    @Override
    public List<SupplierNotificationDTO> getNotifications() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<SupplierNotification> query = em.createQuery("select s from SupplierNotification s where s.company = :company", SupplierNotification.class);
            query.setParameter("company", user.getCompany());
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<SupplierNotification, SupplierNotificationDTO>() {
                @Override
                public SupplierNotificationDTO mutate(SupplierNotification supplierNotification) {
                    SupplierNotificationDTO supplierNotificationDTO = new SupplierNotificationDTO();
                    supplierNotificationDTO.setType(supplierNotification.getType());
                    supplierNotificationDTO.setMessage(supplierNotification.getMessage());
                    supplierNotificationDTO.setLinkId(supplierNotification.getLinkId());
                    supplierNotificationDTO.setCreationDate(supplierNotification.getCreationDate());
                    return supplierNotificationDTO;
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
            productDatasetDTO.setProduct(ProductHelper.createProductDTO(productDataset.getProduct()));
            productDatasetDTO.setProductFeatures(productDataset.getProduct() == null ? null : productDataset.getProduct().getGeoinformation());
            productDatasetDTO.setSelectedFeatures(ListUtil.mutate(productDataset.getGeoinformation(), new ListUtil.Mutate<FeatureDescription, Long>() {
                @Override
                public Long mutate(FeatureDescription featureDescription) {
                    return featureDescription.getId();
                }
            }));
            productDatasetDTO.setExtent(productDataset.getExtent());
            productDatasetDTO.setDatasetAccesses(productDataset.getDatasetAccesses());
            productDatasetDTO.setSamples(productDataset.getSamples());
            return productDatasetDTO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error loading notifications");
        } finally {
            em.close();
        }
    }

    @Override
    public Long saveProductDataset(final ProductDatasetDTO productDatasetDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            ProductDataset productDataset = null;
            if(productDatasetDTO.getId() == null) {
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
            productDataset.setExtent(extentWKT);
            // set selected features
            productDataset.setGeoinformation(ListUtil.filterValues(productDataset.getProduct().getGeoinformation(), new ListUtil.CheckValue<FeatureDescription>() {
                @Override
                public boolean isValue(FeatureDescription value) {
                    return productDatasetDTO.getSelectedFeatures().contains(value.getId());
                }
            }));
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
                        dbDatasetAccesses.add(dbDatasetAccess);
                    }
                }
                productDataset.setDatasetAccesses(dbDatasetAccesses);
            }
            // update the sample access
            {
                List<DatasetAccess> samples = productDatasetDTO.getSamples();
                List<DatasetAccess> dbSamples = new ArrayList<DatasetAccess>();
                if (samples != null && samples.size() > 0) {
                    for (final DatasetAccess datasetAccess : samples) {
                        DatasetAccess dbDatasetAccess = null;
                        if (datasetAccess.getId() != null) {
                            dbDatasetAccess = ListUtil.findValue(productDataset.getSamples(), new ListUtil.CheckValue<DatasetAccess>() {
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
                        dbSamples.add(dbDatasetAccess);
                    }
                }
                productDataset.setSamples(dbSamples);
            }
            // update the keyphrases
            Query query = em.createNativeQuery("UPDATE productdataset SET tsv = " + getProductDatasetTSV(productDataset) +
                    ", tsvname = " + getProductDatasetNameTSV(productDataset) + " where id = " + productDataset.getId() +
                    ";");
            query.executeUpdate();
            em.getTransaction().commit();
            return productDataset.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error saving dataset");
        } finally {
            em.close();
        }
    }

    private static String getProductDatasetNameTSV(ProductDataset productDataset) {
        return "setweight(to_tsvector('english','dataset off the shelf'), 'A') || setweight(to_tsvector('english','" + productDataset.getName() + "'), 'B')";
    }

    private static String getProductDatasetTSV(ProductDataset productDataset) {
        return "setweight(to_tsvector('english','dataset off the shelf " + productDataset.getName() + "'), 'A') " +
                "|| setweight(to_tsvector('english','" + productDataset.getDescription() + "'), 'B')";
    }

    private SoftwareDTO createSoftwareDTO(Software software) {
        SoftwareDTO softwareDTO = new SoftwareDTO();
        softwareDTO.setId(software.getId());
        softwareDTO.setName(software.getName());
        softwareDTO.setImageUrl(software.getImageUrl());
        softwareDTO.setDescription(software.getDescription());
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
    public Long saveSoftware(SoftwareDTO softwareDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Software software = null;
            if(softwareDTO.getId() == null) {
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
            // update the keyphrases
            Query query = em.createNativeQuery("UPDATE software SET tsv = " + getSoftwareTSV(software) +
                    ", tsvname = " + getSoftwareNameTSV(software) + " where id = " + software.getId() +
                    ";");
            query.executeUpdate();
            em.getTransaction().commit();
            return software.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error saving software");
        } finally {
            em.close();
        }
    }

    private static String getSoftwareNameTSV(Software software) {
        return "setweight(to_tsvector('english','software'), 'A') || setweight(to_tsvector('english','" + software.getName() + "'), 'B')";
    }

    private static String getSoftwareTSV(Software software) {
        return "setweight(to_tsvector('english','software " + software.getName() + "'), 'A') " +
                "|| setweight(to_tsvector('english','" + software.getDescription() + "'), 'B')";
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
            projectDTO.setFullDescription(project.getFullDescription());
            projectDTO.setProducts(ListUtil.mutate(project.getProducts(), new ListUtil.Mutate<ProductProject, ProductProjectDTO>() {
                @Override
                public ProductProjectDTO mutate(ProductProject productProject) {
                    return createProductProjectDTO(productProject);
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
    public Long saveProject(ProjectDTO projectDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Project project = null;
            if(projectDTO.getId() == null) {
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
            project.setFullDescription(projectDTO.getFullDescription());
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
                productProjects.add(dbProductProject);
            }
            project.setProducts(productProjects);
            project.setImageUrl(projectDTO.getImageUrl());
            // update the keyphrases
            Query query = em.createNativeQuery("UPDATE project SET tsv = " + getProjectTSV(project) +
                    ", tsvname = " + getProjectNameTSV(project) + " where id = " + project.getId() +
                    ";");
            query.executeUpdate();
            em.getTransaction().commit();
            return project.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error saving project");
        } finally {
            em.close();
        }
    }

    @Override
    public OfferDTO getOffer() throws RequestException {
        OfferDTO offerDTO = new OfferDTO();
        offerDTO.setCompanyDTO(getCompany());
        offerDTO.setProductServiceDTOs(listProductServices());
        offerDTO.setProductDatasetDTOs(listProductDatasets());
        offerDTO.setSoftwareDTOs(listSoftwares());
        offerDTO.setProjectDTOs(listProjects());
        return offerDTO;
    }

    private static String getProjectNameTSV(Project project) {
        return "setweight(to_tsvector('english','dataset off the shelf'), 'A') || setweight(to_tsvector('english','" + project.getName() + "'), 'B')";
    }

    private static String getProjectTSV(Project project) {
        return "setweight(to_tsvector('english','dataset off the shelf " + project.getName() + "'), 'A') " +
                "|| setweight(to_tsvector('english','" + project.getDescription() + "'), 'B')";
    }

}
