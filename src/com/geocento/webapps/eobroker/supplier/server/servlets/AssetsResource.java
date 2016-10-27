package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.AuthorizationException;
import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.ProductService;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.DatasetProvider;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.*;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;
import com.geocento.webapps.eobroker.common.shared.entities.utils.ProductHelper;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import com.geocento.webapps.eobroker.supplier.client.services.AssetsService;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import com.geocento.webapps.eobroker.supplier.shared.dtos.DatasetProviderDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceEditDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierNotificationDTO;
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
            productServiceDTO.setProduct(productService.getProduct() == null ? null : ProductHelper.createProductDTO(productService.getProduct()));
            productServiceDTO.setApiURL(productService.getApiUrl());
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
    public Long addProductService(ProductServiceDTO productService) {
        return null;
    }

    @Override
    public void updateProductService(ProductServiceEditDTO productServiceDTO) throws RequestException {
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
            }
            productService.setName(productServiceDTO.getName());
            productService.setDescription(productServiceDTO.getDescription());
            productService.setImageUrl(productServiceDTO.getServiceImage());
            Product product = em.find(Product.class, productServiceDTO.getProduct().getId());
            if (product == null) {
                throw new RequestException("Product does not exist");
            }
            productService.setProduct(product);
            product.getProductServices().add(productService);
            productService.setEmail(productServiceDTO.getEmail());
            productService.setWebsite(productServiceDTO.getWebsite());
            productService.setFullDescription(productServiceDTO.getFullDescription());
            productService.getCompany().getServices().add(productService);
            productService.setApiUrl(productServiceDTO.getApiURL());
            productService.setSampleWmsUrl(productServiceDTO.getSampleWmsUrl());
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

    @Override
    public CompanyDTO getCompany(Long id) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            Company company = null;
            if (id == null) {
                User user = em.find(User.class, userName);
                company = user.getCompany();
                if (company == null) {
                    em.getTransaction().begin();
                    company = new Company();
                    em.persist(company);
                    user.setCompany(company);
                    em.getTransaction().commit();
                }
            } else {
                company = em.find(Company.class, id);
            }
            if (company == null) {
                throw new RequestException("Unknown company");
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

}
