package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.MailContent;
import com.geocento.webapps.eobroker.common.server.ServerUtil;
import com.geocento.webapps.eobroker.common.server.Utils.EventHelper;
import com.geocento.webapps.eobroker.common.server.Utils.NotificationHelper;
import com.geocento.webapps.eobroker.common.server.Utils.WMSCapabilities;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.AdminNotification;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.Notification;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;
import com.geocento.webapps.eobroker.common.shared.entities.requests.FeasibilitySearch;
import com.geocento.webapps.eobroker.common.shared.entities.subscriptions.Event;
import com.geocento.webapps.eobroker.common.shared.entities.subscriptions.Following;
import com.geocento.webapps.eobroker.common.shared.entities.subscriptions.FollowingEvent;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.services.AssetsService;
import com.geocento.webapps.eobroker.customer.server.utils.StatsHelper;
import com.geocento.webapps.eobroker.customer.server.utils.UserUtils;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.geocento.webapps.eobroker.customer.shared.utils.ProductHelper;
import com.google.gwt.http.client.RequestException;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.*;

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
    public List<AoIDTO> listAoIs() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<AoI> query = em.createQuery("select a from AoI a where a.user = :user order by a.lastAccessed DESC", AoI.class);
            query.setParameter("user", user);
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<AoI, AoIDTO>() {
                @Override
                public AoIDTO mutate(AoI aoi) {
                    AoIDTO aoIDTO = new AoIDTO();
                    aoIDTO.setId(aoi.getId());
                    aoIDTO.setName(aoi.getName());
                    return aoIDTO;
                }
            });
        } catch (Exception e) {
            throw handleException(em, e, "Error loading product datasets");
        } finally {
            em.close();
        }
    }

    private AoIDTO createAoIDTO(AoI aoi) {
        AoIDTO aoIDTO = new AoIDTO();
        aoIDTO.setId(aoi.getId());
        aoIDTO.setName(aoi.getName());
        aoIDTO.setWktGeometry(aoi.getGeometry());
        return aoIDTO;
    }

    @Override
    public AoIDTO getAoI(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            if(id == null) {
                throw new RequestException("AoI not found");
            }
            AoI dbAoI = em.find(AoI.class, id);
            if(dbAoI == null) {
                throw new RequestException("Unknown AoI");
            }
            if(!dbAoI.getUser().getUsername().contentEquals(userName)) {
                throw new RequestException("Not authorised");
            }
            dbAoI.setLastAccessed(new Date());
            User user = em.find(User.class, userName);
            user.setLatestAoI(dbAoI);
            em.getTransaction().commit();
            return createAoIDTO(dbAoI);
        } catch (Exception e) {
            throw handleException(em, e, "Error saving AoI");
        } finally {
            em.close();
        }
    }

    @Override
    public AoIDTO loadLatestAoI() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            return user.getLatestAoI() == null ? null : createAoIDTO(user.getLatestAoI());
        } catch (Exception e) {
            throw handleException(em, e, "Error saving AoI");
        } finally {
            em.close();
        }
    }

    @Override
    public void removeLatestAoI() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            user.setLatestAoI(null);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e, "Error removing the latest AoI");
        } finally {
            em.close();
        }
    }

    @Override
    public AoIDTO updateAoI(AoIDTO aoi) throws RequestException{
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            AoI dbAoI = null;
            if(aoi.getId() == null) {
                dbAoI = new AoI();
                User user = em.find(User.class, userName);
                dbAoI.setUser(user);
                dbAoI.setCreationTime(new Date());
                em.persist(dbAoI);
                user.setLatestAoI(dbAoI);
            } else {
                dbAoI = em.find(AoI.class, aoi.getId());
                if(dbAoI == null) {
                    throw new RequestException("Unknown AoI");
                }
                if(!dbAoI.getUser().getUsername().contentEquals(userName)) {
                    throw new RequestException("Not authorised");
                }
            }
            dbAoI.setName(aoi.getName());
            dbAoI.setGeometry(aoi.getWktGeometry());
            dbAoI.setLastAccessed(new Date());
            em.getTransaction().commit();
            return createAoIDTO(dbAoI);
        } catch (Exception e) {
            throw handleException(em, e, "Error saving AoI");
        } finally {
            em.close();
        }
    }

    @Override
    public void updateAoIName(Long id, String name) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("No id provided");
        }
        if(name == null) {
            throw new RequestException("No name provided");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            AoI dbAoI = em.find(AoI.class, id);
            if(dbAoI == null) {
                throw new RequestException("Unknown AoI");
            }
            if(!dbAoI.getUser().getUsername().contentEquals(userName)) {
                throw new RequestException("Not authorised");
            }
            dbAoI.setName(name);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e, "Error saving AoI");
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteAoI(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("No id provided");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            AoI dbAoI = em.find(AoI.class, id);
            if(dbAoI == null) {
                throw new RequestException("Unknown AoI");
            }
            if(!dbAoI.getUser().getUsername().contentEquals(userName)) {
                throw new RequestException("Not authorised");
            }
            em.remove(dbAoI);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e, "Error saving AoI");
        } finally {
            em.close();
        }
    }

    @Override
    public ProductDTO getProduct(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Product product = em.find(Product.class, id);
            if(product == null) {
                throw new RequestException("Product does not exist");
            }
            return ProductHelper.createProductDTO(product);
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public ProductWithFiltersDTO getProductWithFilters(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Product product = em.find(Product.class, id);
            if(product == null) {
                throw new RequestException("Product does not exist");
            }
            ProductWithFiltersDTO productWithFiltersDTO = new ProductWithFiltersDTO();
            productWithFiltersDTO.setId(product.getId());
            productWithFiltersDTO.setName(product.getName());
            productWithFiltersDTO.setGeoinformation(product.getGeoinformation());
            productWithFiltersDTO.setPerformances(product.getPerformances());
            return productWithFiltersDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public ChallengeDescriptionDTO getChallengeDescription(Long challengeId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(challengeId == null) {
            throw new RequestException("Id cannot be null");
        }
        addViewCounter("challenge", challengeId + "");
        EntityManager em = EMF.get().createEntityManager();
        try {
            Challenge challenge = em.find(Challenge.class, challengeId);
            if(challenge == null) {
                throw new RequestException("Challenge does not exist");
            }
            ChallengeDescriptionDTO challengeDescriptionDTO = new ChallengeDescriptionDTO();
            challengeDescriptionDTO.setImageUrl(challenge.getImageUrl());
            challengeDescriptionDTO.setName(challenge.getName());
            challengeDescriptionDTO.setShortDescription(challenge.getShortDescription());
            challengeDescriptionDTO.setDescription(challenge.getDescription());
            challengeDescriptionDTO.setProducts(ListUtil.mutate(challenge.getProducts(), new ListUtil.Mutate<Product, ProductDTO>() {
                @Override
                public ProductDTO mutate(Product product) {
                    return ProductHelper.createProductDTO(product);
                }
            }));
            return challengeDescriptionDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public ProductFeasibilityDTO getProductFeasibility(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductService productService = em.find(ProductService.class, id);
            if(productService == null) {
                throw new RequestException("Product service does not exist");
            }
            Product product = productService.getProduct();
            ProductFeasibilityDTO productFeasibilityDTO = new ProductFeasibilityDTO();
            productFeasibilityDTO.setId(product.getId());
            productFeasibilityDTO.setName(product.getName());
            productFeasibilityDTO.setImageUrl(product.getImageUrl());
            productFeasibilityDTO.setApiFormElements(product.getApiFormFields());
            // add relevant supplier services
            TypedQuery<ProductService> query = em.createQuery("select p from ProductService p where p.product = :product and p.apiUrl is not null", ProductService.class);
            query.setParameter("product", product);
            productFeasibilityDTO.setProductServices(ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<ProductService, ProductServiceFeasibilityDTO>() {
                @Override
                public ProductServiceFeasibilityDTO mutate(ProductService productService) {
                    ProductServiceFeasibilityDTO productServiceFeasibilityDTO = new ProductServiceFeasibilityDTO();
                    productServiceFeasibilityDTO.setId(productService.getId());
                    productServiceFeasibilityDTO.setName(productService.getName());
                    productServiceFeasibilityDTO.setImageURL(productService.getImageUrl());
                    productServiceFeasibilityDTO.setCompany(CompanyHelper.createCompanyDTO(productService.getCompany()));
                    productServiceFeasibilityDTO.setApiURL(productService.getApiUrl());
                    productServiceFeasibilityDTO.setHasSamples(ProductHelper.hasWMSSamples(productService.getSamples()));
                    return productServiceFeasibilityDTO;
                }
            }));
            return productFeasibilityDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public ProductFormDTO getProductForm(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Product product = em.find(Product.class, id);
            if(product == null) {
                throw new RequestException("Product does not exist");
            }
            ProductFormDTO productFormDTO = new ProductFormDTO();
            productFormDTO.setId(product.getId());
            productFormDTO.setName(product.getName());
            productFormDTO.setDescription(product.getShortDescription());
            productFormDTO.setImageUrl(product.getImageUrl());
            productFormDTO.setSector(product.getSector());
            productFormDTO.setThematic(product.getThematic());
            productFormDTO.setFormFields(product.getFormFields());
            // add relevant supplier services
            TypedQuery<ProductService> query = em.createQuery("select p from ProductService p where p.product = :product", ProductService.class);
            query.setParameter("product", product);
            productFormDTO.setProductServices(ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<ProductService, ProductServiceDTO>() {
                @Override
                public ProductServiceDTO mutate(ProductService productService) {
                    ProductServiceDTO productServiceDTO = new ProductServiceDTO();
                    productServiceDTO.setId(productService.getId());
                    productServiceDTO.setName(productService.getName());
                    productServiceDTO.setDescription(productService.getDescription());
                    productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
                    productServiceDTO.setCompanyName(productService.getCompany().getName());
                    productServiceDTO.setCompanyId(productService.getCompany().getId());
                    productServiceDTO.setServiceImage(productService.getImageUrl());
                    productServiceDTO.setHasFeasibility(productService.getApiUrl() != null && productService.getApiUrl().length() > 0);
                    productServiceDTO.setProduct(ProductHelper.createProductDTO(productService.getProduct()));
                    return productServiceDTO;
                }
            }));
            return productFormDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public ProductDescriptionDTO getProductDescription(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        addViewCounter(Category.products.toString(), id + "");
        EntityManager em = EMF.get().createEntityManager();
        try {
            Product product = em.find(Product.class, id);
            if(product == null) {
                throw new RequestException("Product does not exist");
            }
            final ProductDescriptionDTO productDescriptionDTO = new ProductDescriptionDTO();
            productDescriptionDTO.setId(product.getId());
            productDescriptionDTO.setName(product.getName());
            productDescriptionDTO.setShortDescription(product.getShortDescription());
            productDescriptionDTO.setDescription(product.getDescription());
            productDescriptionDTO.setImageUrl(product.getImageUrl());
            productDescriptionDTO.setSector(product.getSector());
            productDescriptionDTO.setThematic(product.getThematic());
            productDescriptionDTO.setFollowers(product.getFollowers() == null ? 0 : product.getFollowers().intValue());
            // check if following
            TypedQuery<Long> followingQuery = em.createQuery("select count(f) from Following f where f.user.username = :userName and f.product is not null and f.product.id = :productId", Long.class);
            followingQuery.setParameter("userName", userName);
            followingQuery.setParameter("productId", product.getId());
            productDescriptionDTO.setFollowing(followingQuery.getSingleResult() > 0);
            // add relevant supplier services
            {
                TypedQuery<ProductService> query = em.createQuery("select p from ProductService p where p.product = :product", ProductService.class);
                query.setParameter("product", product);
                productDescriptionDTO.setProductServices(ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<ProductService, ProductServiceDTO>() {
                    @Override
                    public ProductServiceDTO mutate(ProductService productService) {
                        ProductServiceDTO productServiceDTO = new ProductServiceDTO();
                        productServiceDTO.setId(productService.getId());
                        productServiceDTO.setName(productService.getName());
                        productServiceDTO.setDescription(productService.getDescription());
                        productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
                        productServiceDTO.setCompanyName(productService.getCompany().getName());
                        productServiceDTO.setCompanyId(productService.getCompany().getId());
                        productServiceDTO.setServiceImage(productService.getImageUrl());
                        productServiceDTO.setHasFeasibility(productService.getApiUrl() != null && productService.getApiUrl().length() > 0);
                        // initiate the services with product dto
                        ProductDTO productDTO = new ProductDTO();
                        productDTO.setId(productDescriptionDTO.getId());
                        productServiceDTO.setProduct(productDTO);
                        return productServiceDTO;
                    }
                }));
            }
            // add off the shelf data
            {
                TypedQuery<ProductDataset> productDatasetQuery = em.createQuery("select p from ProductDataset p where p.product = :product", ProductDataset.class);
                productDatasetQuery.setParameter("product", product);
                productDescriptionDTO.setProductDatasets(ListUtil.mutate(productDatasetQuery.getResultList(), new ListUtil.Mutate<ProductDataset, ProductDatasetDTO>() {
                    @Override
                    public ProductDatasetDTO mutate(ProductDataset productDataset) {
                        ProductDatasetDTO productDatasetDTO = createProductDatasetDTO(productDataset);
                        ProductDTO productDTO = new ProductDTO();
                        productDTO.setId(productDescriptionDTO.getId());
                        productDatasetDTO.setProduct(productDTO);
                        return productDatasetDTO;
                    }
                }));
            }
            // add software
            {
                TypedQuery<ProductSoftware> softwareQuery = em.createQuery("select p from ProductSoftware p where p.product = :product", ProductSoftware.class);
                softwareQuery.setParameter("product", product);
                productDescriptionDTO.setSoftwares(ListUtil.mutate(softwareQuery.getResultList(), new ListUtil.Mutate<ProductSoftware, SoftwareDTO>() {
                    @Override
                    public SoftwareDTO mutate(ProductSoftware productSoftware) {
                        return createSoftwareDTO(productSoftware.getSoftware());
                    }
                }));
            }
            // add projects
            {
                TypedQuery<ProductProject> projectQuery = em.createQuery("select p from ProductProject p where p.product = :product", ProductProject.class);
                projectQuery.setParameter("product", product);
                productDescriptionDTO.setProjects(ListUtil.mutate(projectQuery.getResultList(), new ListUtil.Mutate<ProductProject, ProjectDTO>() {
                    @Override
                    public ProjectDTO mutate(ProductProject productProject) {
                        return createProjectDTO(productProject.getProject());
                    }
                }));
            }
            // add success stories
            {
                productDescriptionDTO.setSuccessStories(ListUtil.mutate(product.getSuccessStories(), new ListUtil.Mutate<SuccessStory, SuccessStoryDTO>() {
                    @Override
                    public SuccessStoryDTO mutate(SuccessStory successStory) {
                        return convertToSuccessStoryDTO(successStory, false);
                    }
                }));
            }
            // TODO - add suggestions
            {
                // search for products in the same categories and/or with similar keywords?
                productDescriptionDTO.setSuggestedProducts(new ArrayList<ProductDTO>());
            }
            return productDescriptionDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public ProductServiceDescriptionDTO getProductServiceDescription(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductService productService = em.find(ProductService.class, id);
            if (productService == null) {
                throw new RequestException("Company does not exist");
            }
            ProductServiceDescriptionDTO productServiceDescriptionDTO = new ProductServiceDescriptionDTO();
            productServiceDescriptionDTO.setId(productService.getId());
            productServiceDescriptionDTO.setServiceImage(productService.getImageUrl());
            productServiceDescriptionDTO.setName(productService.getName());
            productServiceDescriptionDTO.setDescription(productService.getDescription());
            productServiceDescriptionDTO.setFullDescription(productService.getFullDescription());
            productServiceDescriptionDTO.setWebsite(productService.getWebsite());
            productServiceDescriptionDTO.setCompany(CompanyHelper.createCompanyDTO(productService.getCompany()));
            productServiceDescriptionDTO.setProduct(ProductHelper.createProductDTO(productService.getProduct()));
            productServiceDescriptionDTO.setGeoinformation(productService.getGeoinformation());
            productServiceDescriptionDTO.setGeoinformationComment(productService.getGeoinformationComment());
            productServiceDescriptionDTO.setPerformances(productService.getPerformances());
            productServiceDescriptionDTO.setPerformancesComments(productService.getPerformancesComment());
            productServiceDescriptionDTO.setTimeToDelivery(productService.getTimeToDelivery());
            productServiceDescriptionDTO.setExtent(productService.getExtent());
            productServiceDescriptionDTO.setCoverageLayers(productService.getCoverageLayers());
            productServiceDescriptionDTO.setHasFeasibility(productService.getApiUrl() != null);
            productServiceDescriptionDTO.setSelectedAccessTypes(productService.getSelectedAccessTypes());
            productServiceDescriptionDTO.setSamples(productService.getSamples());
            productServiceDescriptionDTO.setTermsAndConditions(productService.getTermsAndConditions());
            // add suggestions
            // for now make it simple and just add the same product services
            List<ProductService> suggestedServices = new ArrayList<ProductService>(productService.getProduct().getProductServices());
            suggestedServices.remove(productService);
            productServiceDescriptionDTO.setSuggestedServices(ListUtil.mutate(suggestedServices, new ListUtil.Mutate<ProductService, ProductServiceDTO>() {
                @Override
                public ProductServiceDTO mutate(ProductService productService) {
                    return createProductServiceDTO(productService);
                }
            }));
            addCompanyViewCounter(productService.getCompany().getId(), Category.productservices.toString(), id + "");
            return productServiceDescriptionDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    private void addCompanyViewCounter(Long companyId, String category, String id) {
        addViewCounter(companyId + "." + category, id);
    }

    private void addViewCounter(String category, String id) {
        StatsHelper.addCounter("view." + category, id);
    }

    @Override
    public ProductDatasetDescriptionDTO getProductDatasetDescription(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductDataset productDataset = em.find(ProductDataset.class, id);
            if(productDataset == null) {
                throw new RequestException("Product does not exist");
            }
            // add suggestions
            // for now make it simple and just add the same product services
            ProductDatasetDescriptionDTO productDatasetDescriptionDTO = new ProductDatasetDescriptionDTO();
            productDatasetDescriptionDTO.setId(productDataset.getId());
            productDatasetDescriptionDTO.setName(productDataset.getName());
            productDatasetDescriptionDTO.setImageUrl(productDataset.getImageUrl());
            productDatasetDescriptionDTO.setDescription(productDataset.getDescription());
            productDatasetDescriptionDTO.setFullDescription(productDataset.getFullDescription());
            productDatasetDescriptionDTO.setGeoinformation(productDataset.getGeoinformation());
            productDatasetDescriptionDTO.setGeoinformationComment(productDataset.getGeoinformationComment());
            productDatasetDescriptionDTO.setPerformances(productDataset.getPerformances());
            productDatasetDescriptionDTO.setPerformancesComments(productDataset.getPerformancesComment());
            productDatasetDescriptionDTO.setExtent(productDataset.getExtent());
            productDatasetDescriptionDTO.setCoverageLayers(productDataset.getCoverageLayers());
            productDatasetDescriptionDTO.setCompany(CompanyHelper.createCompanyDTO(productDataset.getCompany()));
            productDatasetDescriptionDTO.setProduct(ProductHelper.createProductDTO(productDataset.getProduct()));
            productDatasetDescriptionDTO.setCommercial(productDataset.getServiceType() == ServiceType.commercial);
            productDatasetDescriptionDTO.setDatasetAccesses(productDataset.getDatasetAccesses());
            productDatasetDescriptionDTO.setSamples(productDataset.getSamples());
            productDatasetDescriptionDTO.setTermsAndConditions(productDataset.getTermsAndConditions());
            List<ProductDataset> suggestedDatasets = new ArrayList<ProductDataset>(productDataset.getProduct().getProductDatasets());
            suggestedDatasets.remove(productDataset);
            productDatasetDescriptionDTO.setSuggestedDatasets(ListUtil.mutate(suggestedDatasets, new ListUtil.Mutate<ProductDataset, ProductDatasetDTO>() {
                @Override
                public ProductDatasetDTO mutate(ProductDataset productDataset) {
                    return createProductDatasetDTO(productDataset);
                }
            }));
            productDatasetDescriptionDTO.setCatalogueStandard(productDataset.getDatasetStandard());
            addCompanyViewCounter(productDataset.getCompany().getId(), Category.productdatasets.toString(), id + "");
            return productDatasetDescriptionDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public ProductDatasetCatalogueDTO getProductDatasetCatalogueDTO(Long productDatasetId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(productDatasetId == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductDataset productDataset = em.find(ProductDataset.class, productDatasetId);
            if(productDataset == null) {
                throw new RequestException("Product does not exist");
            }
            // add suggestions
            // for now make it simple and just add the same product services
            ProductDatasetCatalogueDTO productDatasetCatalogueDTO = new ProductDatasetCatalogueDTO();
            productDatasetCatalogueDTO.setId(productDataset.getId());
            productDatasetCatalogueDTO.setName(productDataset.getName());
            productDatasetCatalogueDTO.setImageUrl(productDataset.getImageUrl());
            productDatasetCatalogueDTO.setDescription(productDataset.getDescription());
            productDatasetCatalogueDTO.setExtent(productDataset.getExtent());
            productDatasetCatalogueDTO.setCompany(CompanyHelper.createCompanyDTO(productDataset.getCompany()));
            productDatasetCatalogueDTO.setProduct(ProductHelper.createProductDTO(productDataset.getProduct()));
            productDatasetCatalogueDTO.setDatasetStandard(productDataset.getDatasetStandard());
            productDatasetCatalogueDTO.setDatasetURL(productDataset.getDatasetURL());
            productDatasetCatalogueDTO.setOrderable(productDataset.getServiceType() == ServiceType.commercial);
            productDatasetCatalogueDTO.setHasSamples(ProductHelper.hasWMSSamples(productDataset.getSamples()));
            // add relevant supplier services
            TypedQuery<ProductDataset> query = em.createQuery("select p from ProductDataset p where p.product = :product and p.datasetStandard is not null and p.id <> :productDatasetId", ProductDataset.class);
            query.setParameter("product", productDataset.getProduct());
            query.setParameter("productDatasetId", productDataset.getId());
            productDatasetCatalogueDTO.setOtherCatalogues(ListUtil.mutate(query.getResultList(), otherProductDataset -> {
                ProductDatasetDTO productDatasetDTO = new ProductDatasetDTO();
                productDatasetDTO.setId(otherProductDataset.getId());
                productDatasetDTO.setName(otherProductDataset.getName());
                productDatasetDTO.setCompany(CompanyHelper.createCompanyDTO(otherProductDataset.getCompany()));
                return productDatasetDTO;
            }));
            StatsHelper.addSearchCounter(productDataset.getCompany().getId(), Category.productdatasets.toString(), productDataset.getId() + "");
            return productDatasetCatalogueDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public SoftwareDescriptionDTO getSoftwareDescription(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Software software = em.find(Software.class, id);
            if(software == null) {
                throw new RequestException("Software does not exist");
            }
            // add suggestions
            // for now make it simple and just add the same product services
            SoftwareDescriptionDTO softwareDescriptionDTO = new SoftwareDescriptionDTO();
            softwareDescriptionDTO.setId(software.getId());
            softwareDescriptionDTO.setName(software.getName());
            softwareDescriptionDTO.setImageUrl(software.getImageUrl());
            softwareDescriptionDTO.setDescription(software.getDescription());
            softwareDescriptionDTO.setFullDescription(software.getFullDescription());
            softwareDescriptionDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(software.getCompany()));
            softwareDescriptionDTO.setSoftwareType(software.getSoftwareType());
            softwareDescriptionDTO.setProducts(ListUtil.mutate(software.getProducts(), productSoftware -> createProductSoftwareDTO(productSoftware)));
            // TODO - add suggestions
            {
                softwareDescriptionDTO.setSuggestedSoftware(new ArrayList<SoftwareDTO>());
            }
            softwareDescriptionDTO.setTermsAndConditions(software.getTermsAndConditions());
            addCompanyViewCounter(software.getCompany().getId(), Category.software.toString(), id + "");
            return softwareDescriptionDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public ProjectDescriptionDTO getProjectDescription(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Project project = em.find(Project.class, id);
            if(project == null) {
                throw new RequestException("Project does not exist");
            }
            // add suggestions
            // for now make it simple and just add the same product services
            ProjectDescriptionDTO projectDescriptionDTO = new ProjectDescriptionDTO();
            projectDescriptionDTO.setId(project.getId());
            projectDescriptionDTO.setName(project.getName());
            projectDescriptionDTO.setImageUrl(project.getImageUrl());
            projectDescriptionDTO.setDescription(project.getDescription());
            projectDescriptionDTO.setFullDescription(project.getFullDescription());
            projectDescriptionDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(project.getCompany()));
            projectDescriptionDTO.setProducts(ListUtil.mutate(project.getProducts(), new ListUtil.Mutate<ProductProject, ProductProjectDTO>() {
                @Override
                public ProductProjectDTO mutate(ProductProject productProject) {
                    return createProductProjectDTO(productProject);
                }
            }));
            projectDescriptionDTO.setConsortium(ListUtil.mutate(project.getConsortium(), new ListUtil.Mutate<CompanyRole, CompanyRoleDTO>() {
                @Override
                public CompanyRoleDTO mutate(CompanyRole companyRole) {
                    CompanyRoleDTO companyRoleDTO = new CompanyRoleDTO();
                    companyRoleDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(companyRole.getCompany()));
                    companyRoleDTO.setRole(companyRole.getRole());
                    return companyRoleDTO;
                }
            }));
            projectDescriptionDTO.setStartDate(project.getStartDate());
            projectDescriptionDTO.setStopDate(project.getStopDate());
            // TODO - add suggestions
            {
                projectDescriptionDTO.setSuggestedProjects(new ArrayList<ProjectDTO>());
            }
            addCompanyViewCounter(project.getCompany().getId(), Category.project.toString(), id + "");
            return projectDescriptionDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public ProductDatasetVisualisationDTO getProductDatasetVisualisation(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductDataset productDataset = em.find(ProductDataset.class, id);
            if(productDataset == null) {
                throw new RequestException("Product dataset does not exist");
            }
            addVisualisationCounter(Category.productdatasets.toString(), id + "");
            return convertToProductDatasetVisualisationDTO(productDataset);
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    private void addVisualisationCounter(String category, String id) {
        StatsHelper.addCounter("visualisation." + category, id + "");
    }

    private ProductDatasetVisualisationDTO convertToProductDatasetVisualisationDTO(ProductDataset productDataset) {
        ProductDatasetVisualisationDTO productDatasetVisualisationDTO = new ProductDatasetVisualisationDTO();
        productDatasetVisualisationDTO.setId(productDataset.getId());
        productDatasetVisualisationDTO.setName(productDataset.getName());
        productDatasetVisualisationDTO.setImageUrl(productDataset.getImageUrl());
        productDatasetVisualisationDTO.setDescription(productDataset.getDescription());
        productDatasetVisualisationDTO.setCompany(CompanyHelper.createCompanyDTO(productDataset.getCompany()));
        productDatasetVisualisationDTO.setProduct(ProductHelper.createProductDTO(productDataset.getProduct()));
        productDatasetVisualisationDTO.setDatasetAccess(ListUtil.filterValues(productDataset.getDatasetAccesses(), new ListUtil.CheckValue<DatasetAccess>() {
            @Override
            public boolean isValue(DatasetAccess value) {
                return value instanceof DatasetAccessOGC;
            }
        }));
        productDatasetVisualisationDTO.setSamples(ListUtil.filterValues(productDataset.getSamples(), new ListUtil.CheckValue<DatasetAccess>() {
            @Override
            public boolean isValue(DatasetAccess value) {
                return value instanceof DatasetAccessOGC;
            }
        }));
        return productDatasetVisualisationDTO;
    }

    @Override
    public ProductServiceVisualisationDTO getProductServiceVisualisation(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductService productService = em.find(ProductService.class, id);
            if(productService == null) {
                throw new RequestException("Product service does not exist");
            }
            addVisualisationCounter(Category.productdatasets.toString(), id + "");
            return convertToProductServiceVisualisationDTO(productService);
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    private ProductServiceVisualisationDTO convertToProductServiceVisualisationDTO(ProductService productService) {
        ProductServiceVisualisationDTO productServiceVisualisationDTO = new ProductServiceVisualisationDTO();
        productServiceVisualisationDTO.setId(productService.getId());
        productServiceVisualisationDTO.setName(productService.getName());
        productServiceVisualisationDTO.setServiceImage(productService.getImageUrl());
        productServiceVisualisationDTO.setDescription(productService.getDescription());
        productServiceVisualisationDTO.setCompany(CompanyHelper.createCompanyDTO(productService.getCompany()));
        productServiceVisualisationDTO.setProduct(ProductHelper.createProductDTO(productService.getProduct()));
        productServiceVisualisationDTO.setSamples(ListUtil.filterValues(productService.getSamples(), new ListUtil.CheckValue<DatasetAccess>() {
            @Override
            public boolean isValue(DatasetAccess value) {
                return value instanceof DatasetAccessOGC;
            }
        }));
        return productServiceVisualisationDTO;
    }

    private ProductSoftwareDTO createProductSoftwareDTO(ProductSoftware productSoftware) {
        ProductSoftwareDTO productSoftwareDTO = new ProductSoftwareDTO();
        productSoftwareDTO.setId(productSoftware.getId());
        productSoftwareDTO.setPitch(productSoftware.getPitch());
        productSoftwareDTO.setProduct(ProductHelper.createProductDTO(productSoftware.getProduct()));
        return productSoftwareDTO;
    }

    private ProductProjectDTO createProductProjectDTO(ProductProject productProject) {
        ProductProjectDTO productProjectDTO = new ProductProjectDTO();
        productProjectDTO.setId(productProject.getId());
        productProjectDTO.setPitch(productProject.getPitch());
        productProjectDTO.setProduct(ProductHelper.createProductDTO(productProject.getProduct()));
        return productProjectDTO;
    }

    @Override
    public List<ImageService> getImageServices() throws RequestException {
        EntityManager em = EMF.get().createEntityManager();
        TypedQuery<ImageService> query = em.createQuery("select i from ImageService i", ImageService.class);
        return query.getResultList();
    }

    @Override
    public List<NewsItem> getNewsItems() throws RequestException {
        EntityManager em = EMF.get().createEntityManager();
        try {
            TypedQuery<NewsItem> query = em.createQuery("select n from NewsItem n ORDER BY n.creationDate DESC", NewsItem.class);
            query.setMaxResults(5);
            return query.getResultList();
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public CompanyDescriptionDTO getCompanyDescription(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Company company = em.find(Company.class, id);
            if (company == null) {
                throw new RequestException("Company does not exist");
            }
            CompanyDescriptionDTO companyDescriptionDTO = new CompanyDescriptionDTO();
            companyDescriptionDTO.setId(company.getId());
            companyDescriptionDTO.setName(company.getName());
            companyDescriptionDTO.setDescription(company.getDescription());
            companyDescriptionDTO.setFullDescription(company.getFullDescription());
            companyDescriptionDTO.setIconURL(company.getIconURL());
            companyDescriptionDTO.setContactEmail(company.getContactEmail());
            companyDescriptionDTO.setWebsite(company.getWebsite());
            companyDescriptionDTO.setAddress(company.getAddress());
            companyDescriptionDTO.setCountryCode(company.getCountryCode());
            companyDescriptionDTO.setAwards(company.getAwards());
            companyDescriptionDTO.setFollowers(company.getFollowers() == null ? 0 : company.getFollowers().intValue());
            // check if following
            TypedQuery<Long> followingQuery = em.createQuery("select count(f) from Following f where f.user.username = :userName and f.company is not null and f.company.id = :companyId", Long.class);
            followingQuery.setParameter("userName", userName);
            followingQuery.setParameter("companyId", company.getId());
            companyDescriptionDTO.setFollowing(followingQuery.getSingleResult() > 0);
            companyDescriptionDTO.setTestimonials(ListUtil.mutate(company.getTestimonials(), new ListUtil.Mutate<Testimonial, TestimonialDTO>() {
                @Override
                public TestimonialDTO mutate(Testimonial testimonial) {
                    TestimonialDTO testimonialDTO = new TestimonialDTO();
                    testimonialDTO.setId(testimonial.getId());
                    testimonialDTO.setFromUser(UserHelper.createUserDTO(testimonial.getFromUser()));
                    testimonialDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(testimonial.getCompany()));
                    testimonialDTO.setTestimonial(testimonial.getTestimonial());
                    testimonialDTO.setCreationDate(testimonial.getCreationDate());
                    return testimonialDTO;
                }
            }));
            companyDescriptionDTO.setSuccessStories(ListUtil.mutate(company.getSuccessStories(), new ListUtil.Mutate<SuccessStory, SuccessStoryDTO>() {
                @Override
                public SuccessStoryDTO mutate(SuccessStory successStory) {
                    return convertToSuccessStoryDTO(successStory, false);
                }
            }));
            companyDescriptionDTO.setProductServices(ListUtil.mutate(company.getServices(), new ListUtil.Mutate<ProductService, ProductServiceDTO>() {
                @Override
                public ProductServiceDTO mutate(ProductService productService) {
                    return createProductServiceDTO(productService);
                }
            }));
            companyDescriptionDTO.setProductDatasets(ListUtil.mutate(company.getDatasets(), new ListUtil.Mutate<ProductDataset, ProductDatasetDTO>() {
                @Override
                public ProductDatasetDTO mutate(ProductDataset object) {
                    return createProductDatasetDTO(object);
                }
            }));
            companyDescriptionDTO.setSoftware(ListUtil.mutate(company.getSoftware(), new ListUtil.Mutate<Software, SoftwareDTO>() {
                @Override
                public SoftwareDTO mutate(Software software) {
                    return createSoftwareDTO(software);
                }
            }));
            companyDescriptionDTO.setProject(ListUtil.mutate(company.getProjects(), new ListUtil.Mutate<Project, ProjectDTO>() {
                @Override
                public ProjectDTO mutate(Project project) {
                    return createProjectDTO(project);
                }
            }));
            addCompanyViewCounter(id, Category.companies.toString(), id + "");
            return companyDescriptionDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    private ProductServiceDTO createProductServiceDTO(ProductService productService) {
        ProductServiceDTO productServiceDTO = new ProductServiceDTO();
        productServiceDTO.setId(productService.getId());
        productServiceDTO.setName(productService.getName());
        productServiceDTO.setDescription(productService.getDescription());
        productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
        productServiceDTO.setCompanyName(productService.getCompany().getName());
        productServiceDTO.setCompanyId(productService.getCompany().getId());
        productServiceDTO.setServiceImage(productService.getImageUrl());
/*
                    productServiceDTO.setProduct(ProductHelper.createProductDTO(productService.getProduct()));
*/
        productServiceDTO.setHasFeasibility(productService.getApiUrl() != null && productService.getApiUrl().length() > 0);
        return productServiceDTO;
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

    private SoftwareDTO createSoftwareDTO(Software software) {
        SoftwareDTO softwareDTO = new SoftwareDTO();
        softwareDTO.setId(software.getId());
        softwareDTO.setName(software.getName());
        softwareDTO.setImageUrl(software.getImageUrl());
        softwareDTO.setDescription(software.getDescription());
        softwareDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(software.getCompany()));
        return softwareDTO;
    }

    private ProjectDTO createProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setImageUrl(project.getImageUrl());
        projectDTO.setDescription(project.getDescription());
        projectDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(project.getCompany()));
        return projectDTO;
    }

    @Override
    public CompanyDTO getCompany(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Company company = em.find(Company.class, id);
            if (company == null) {
                throw new RequestException("Company does not exist");
            }
            return CompanyHelper.createFullCompanyDTO(company);
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public CustomerCompanyDTO getUserCompany() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            Company company = user.getCompany();
            if (company == null) {
                throw new RequestException("Company does not exist");
            }
            CustomerCompanyDTO customerCompanyDTO = new CustomerCompanyDTO();
            CompanyHelper.populateCompanyDTO(customerCompanyDTO, company, true);
            customerCompanyDTO.setAffiliates(ListUtil.mutate(company.getAffiliates(), new ListUtil.Mutate<Company, CompanyDTO>() {
                @Override
                public CompanyDTO mutate(Company company) {
                    return CompanyHelper.createCompanyDTO(company);
                }
            }));
            TypedQuery<User> query = em.createQuery("select u from users u where u.company = :company", User.class);
            query.setParameter("company", company);
            customerCompanyDTO.setUsers(ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<User, UserDTO>() {
                @Override
                public UserDTO mutate(User user) {
                    UserDTO userDTO = UserHelper.createUserDTO(user);
                    // no need to pass the company
                    userDTO.setCompanyDTO(null);
                    return userDTO;
                }
            }));
            return customerCompanyDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public void updateUserCompany(CustomerCompanyDTO companyDTO) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(companyDTO == null || companyDTO.getId() == null) {
            throw new RequestException("Invalid company");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            Company company = user.getCompany();
            if (company == null) {
                throw new RequestException("Company does not exist");
            }
            if(!company.getId().equals(companyDTO.getId())) {
                throw new RequestException("Not allowed to change the company");
            }
            em.getTransaction().begin();
            company.setName(companyDTO.getName());
            company.setStartedIn(companyDTO.getStartedIn());
            company.setCompanySize(companyDTO.getCompanySize());
            company.setAddress(companyDTO.getAddress());
            company.setCountryCode(companyDTO.getCountryCode());
            company.setContactEmail(companyDTO.getContactEmail());
            company.setDescription(companyDTO.getDescription());
            company.setFullDescription(companyDTO.getFullDescription());
            company.setIconURL(companyDTO.getIconURL());
            company.setWebsite(companyDTO.getWebsite());
            if(companyDTO.getAffiliates() != null && companyDTO.getAffiliates().size() > 0) {
                TypedQuery<Company> query = em.createQuery("select c from Company c where c.id in :companies", Company.class);
                query.setParameter("companies", ListUtil.mutate(companyDTO.getAffiliates(), new ListUtil.Mutate<CompanyDTO, Long>() {
                    @Override
                    public Long mutate(CompanyDTO companyDTO) {
                        return companyDTO.getId();
                    }
                }));
                List<Company> companies = query.getResultList();
                company.setAffiliates(companies);
            } else {
                company.setAffiliates(new ArrayList<Company>());
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<NotificationDTO> listNotifications(int start, int limit) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<Notification> query = em.createQuery("select s from Notification s where s.user = :user order by s.creationDate DESC", Notification.class);
            query.setParameter("user", user);
            query.setFirstResult(start);
            query.setMaxResults(limit);
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<Notification, NotificationDTO>() {
                @Override
                public NotificationDTO mutate(Notification notification) {
                    return NotificationHelper.createNotificationDTO(notification);
                }
            });
        } catch (Exception e) {
            throw handleException(em, e, "Error loading notifications");
        } finally {
            em.close();
        }
    }

    @Override
    public NotificationDTO getNotification(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            Notification notification = em.find(Notification.class, id);
            if(!notification.getUser().getUsername().contentEquals(userName)) {
                throw new RequestException("Not authorised");
            }
            return NotificationHelper.createNotificationDTO(notification);
        } catch (Exception e) {
            throw handleException(em, e, "Error loading notification");
        } finally {
            em.close();
        }
    }

    @Override
    public LayerInfoDTO getLayerInfo(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            final DatasetAccess datasetAccess = em.find(DatasetAccess.class, id);
            if(datasetAccess == null) {
                throw new RequestException("Dataset not found");
            }
            if(!(datasetAccess instanceof DatasetAccessOGC)) {
                throw new RequestException("Dataset not valid");
            }
            addVisualisationCounter("samples", id + "");
            // now issue the request to the map server
            DatasetAccessOGC datasetAccessOGC = (DatasetAccessOGC) datasetAccess;
            String serverUrl = datasetAccessOGC.getServerUrl();
            // manage several layers
            Set<String> layerNames = new HashSet<String>();
            String layerName = datasetAccessOGC.getLayerName();
            // overwrite if it is the internal eobroker server
            // this is to optimise the call
            if(!datasetAccessOGC.isHostedData() && layerName.contains(":")) {
                int index = serverUrl.lastIndexOf("/");
                String workspace = layerName.substring(0, layerName.indexOf(":"));
                for(String layerNameValue : layerName.split(",")) {
                    layerNames.add(layerNameValue.replace(workspace + ":", ""));
                }
                // force to 1.1.0 as there are problems with 1.3.0 and multiple layers
                serverUrl = serverUrl.substring(0, index) + "/" + workspace + serverUrl.substring(index) + "&version=1.1.1";
            } else {
                for(String layerNameValue : layerName.split(",")) {
                    layerNames.add(layerNameValue);
                }
            }
            List<WMSCapabilities.WMSLayer> layers = new ArrayList<WMSCapabilities.WMSLayer>();
            // make WMS query
            WMSCapabilities wmsCapabilities = new WMSCapabilities();
            wmsCapabilities.extractWMSXMLResources(serverUrl + "&service=WMS&request=getCapabilities");
            for(WMSCapabilities.WMSLayer wmsLayer : wmsCapabilities.getLayersList()) {
                if(layerNames.contains(wmsLayer.getLayerName())) {
                    layers.add(wmsLayer);
                }
            }
            if(layers.size() == 0) {
                throw new RequestException("Layer does not exist");
            }
            LayerInfoDTO layerInfoDTO = new LayerInfoDTO();
            layerInfoDTO.setName(datasetAccessOGC.getTitle());
            layerInfoDTO.setServerUrl(serverUrl);
            layerInfoDTO.setLayerName(ListUtil.toString(layers, new ListUtil.GetLabel<WMSCapabilities.WMSLayer>() {
                @Override
                public String getLabel(WMSCapabilities.WMSLayer value) {
                    return value.getLayerName();
                }
            }, ","));
            layerInfoDTO.setDescription(datasetAccessOGC.getPitch());
            layerInfoDTO.setStyleName(datasetAccessOGC.getStyleName());
            layerInfoDTO.setCrs(layers.get(0).getSupportedSRS());
            Extent bounds = layers.get(0).getBounds();
            for(WMSCapabilities.WMSLayer layer : layers) {
                Extent layerBounds = layer.getBounds();
                bounds.setEast(Math.max(bounds.getEast(), layerBounds.getEast()));
                bounds.setNorth(Math.max(bounds.getNorth(), layerBounds.getNorth()));
                bounds.setWest(Math.min(bounds.getWest(), layerBounds.getWest()));
                bounds.setSouth(Math.min(bounds.getSouth(), layerBounds.getSouth()));
            }
            layerInfoDTO.setExtent(bounds);
            layerInfoDTO.setVersion(layers.get(0).getVersion());
            layerInfoDTO.setQueryable(layers.get(0).isQueryable());
            layerInfoDTO.setSavedLayer(ListUtil.findValue(user.getSavedLayers(), new ListUtil.CheckValue<DatasetAccessOGC>() {
                @Override
                public boolean isValue(DatasetAccessOGC value) {
                    return id.equals(value.getId());
                }
            }) != null);
            return layerInfoDTO;
        } catch (Exception e) {
            throw handleException(em, e, "Error loading wms layer info");
        } finally {
            em.close();
        }
    }

    @Override
    public Long followCompany(Long companyId, Boolean follow) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            Company company = em.find(Company.class, companyId);
            if(company == null) {
                throw new RequestException("Could not find company");
            }
            TypedQuery<Following> query = em.createQuery("select f from Following f where f.user = :user and f.company is not null and f.company.id = :companyid", Following.class);
            query.setParameter("companyid", companyId);
            query.setParameter("user", user);
            List<Following> followings = query.getResultList();
            em.getTransaction().begin();
            if(follow) {
                // check we are not following it already
                if(followings != null && followings.size() > 0) {
                    // we are already following
                } else {
                    Following following = new Following();
                    following.setUser(user);
                    following.setCompany(company);
                    following.setCreationDate(new Date());
                    em.persist(following);
                }
            } else {
                // remove following
                if(followings != null && followings.size() > 0) {
                    for(Following following : followings) {
                        em.remove(following);
                    }
                }
            }
            // TODO - update number of followers
            // count number of followers
            TypedQuery<Long> countFollowersQuery = em.createQuery("select count(f) from Following f where f.company is not null and f.company.id = :companyId", Long.class);
            countFollowersQuery.setParameter("companyId", companyId);
            Long followers = countFollowersQuery.getSingleResult();
            company.setFollowers(followers);
            em.getTransaction().commit();
            addFollowCounter(Category.companies.toString(), companyId + "", follow);
            return followers;
        } catch (Exception e) {
            throw handleException(em, e, "Error updating following");
        } finally {
            em.close();
        }
    }

    private void addFollowCounter(String category, String id, boolean follow) {
        StatsHelper.addCounter("follow." + category + "." + id, follow ? 1 : -1);
    }

    private Boolean dummyFollow(Long companyId, Boolean follow) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            // just for testing
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return follow;
    }

    @Override
    public Long followProduct(Long productId, Boolean follow) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            Product product = em.find(Product.class, productId);
            if(product == null) {
                throw new RequestException("Could not find product");
            }
            TypedQuery<Following> query = em.createQuery("select f from Following f where f.user = :user and f.product is not null and f.product.id = :productid", Following.class);
            query.setParameter("productid", productId);
            query.setParameter("user", user);
            List<Following> followings = query.getResultList();
            em.getTransaction().begin();
            if(follow) {
                // check we are not following it already
                if(followings != null && followings.size() > 0) {
                    // we are already following
                } else {
                    Following following = new Following();
                    following.setUser(user);
                    following.setProduct(product);
                    following.setCreationDate(new Date());
                    em.persist(following);
                }
            } else {
                // remove following
                if(followings != null && followings.size() > 0) {
                    for(Following following : followings) {
                        em.remove(following);
                    }
                }
            }
            // TODO - update number of followers
            // count number of followers
            TypedQuery<Long> countFollowersQuery = em.createQuery("select count(f) from Following f where f.product is not null and f.product.id = :productid", Long.class);
            countFollowersQuery.setParameter("productid", productId);
            Long followers = countFollowersQuery.getSingleResult();
            product.setFollowers(followers);
            em.getTransaction().commit();
            addFollowCounter(Category.products.toString(), productId + "", follow);
            return followers;
        } catch (Exception e) {
            throw handleException(em, e, "Error updating following");
        } finally {
            em.close();
        }
    }

    @Override
    public Boolean followProductService(Long serviceId, Boolean follow) throws RequestException {
        return dummyFollow(serviceId, follow);
    }

    @Override
    public Boolean followProductDataset(Long productDatasetId, Boolean follow) throws RequestException {
        return dummyFollow(productDatasetId, follow);
    }

    @Override
    public Boolean followSoftware(Long softwareId, Boolean follow) throws RequestException {
        return dummyFollow(softwareId, follow);
    }

    @Override
    public Boolean followProject(Long projectId, Boolean follow) throws RequestException {
        return dummyFollow(projectId, follow);
    }

    @Override
    public List<FollowingEventDTO> getFollowingEvents(int start, int limit) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<FollowingEvent> query = em.createQuery("select f from FollowingEvent f where f.user = :user order by f.creationDate DESC", FollowingEvent.class);
            query.setParameter("user", user);
            query.setFirstResult(start);
            query.setMaxResults(limit);
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<FollowingEvent, FollowingEventDTO>() {
                @Override
                public FollowingEventDTO mutate(FollowingEvent followingEvent) {
                    FollowingEventDTO followingEventDTO = new FollowingEventDTO();
                    followingEventDTO.setCategory(followingEvent.getEvent().getCategory());
                    followingEventDTO.setType(followingEvent.getEvent().getType());
                    followingEventDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(followingEvent.getEvent().getCompany()));
                    followingEventDTO.setLinkId(followingEvent.getEvent().getLinkId());
                    followingEventDTO.setMessage(followingEvent.getEvent().getMessage());
                    // use the event creation instead
                    followingEventDTO.setCreationDate(followingEvent.getEvent().getCreationDate());
                    return followingEventDTO;
                }
            });
        } catch (Exception e) {
            throw handleException(em, e, "Error loading notifications");
        } finally {
            em.close();
        }
    }

    @Override
    public List<TestimonialDTO> listTestimonials() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<Testimonial> query = em.createQuery("select t from Testimonial t where t.fromUser = :user order by t.creationDate", Testimonial.class);
            query.setParameter("user", user);
            return ListUtil.mutate(query.getResultList(), testimonial -> {
                TestimonialDTO testimonialDTO = new TestimonialDTO();
                testimonialDTO.setCreationDate(testimonial.getCreationDate());
                testimonialDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(testimonial.getCompany()));
                // TODO - add the offerings if included
                testimonialDTO.setTestimonial(testimonial.getTestimonial());
                return testimonialDTO;
            });
        } catch (Exception e) {
            throw handleException(em, e, "Error loading testimonials");
        } finally {
            em.close();
        }
    }

    @Override
    public TestimonialDTO getTestimonial(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            Testimonial testimonial = em.find(Testimonial.class, id);
            if(!testimonial.getFromUser().getUsername().contentEquals(userName)) {
                throw new RequestException("Not authorised");
            }
            TestimonialDTO testimonialDTO = new TestimonialDTO();
            testimonialDTO.setCreationDate(testimonial.getCreationDate());
            testimonialDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(testimonial.getCompany()));
            // TODO - add the offerings if included
            testimonialDTO.setTestimonial(testimonial.getTestimonial());
            return testimonialDTO;
        } catch (Exception e) {
            throw handleException(em, e, "Error loading notifications");
        } finally {
            em.close();
        }
    }

    @Override
    public Long createTestimonial(TestimonialDTO testimonialDTO) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Company company = em.find(Company.class, testimonialDTO.getCompanyDTO().getId());
            Testimonial testimonial = new Testimonial();
            testimonial.setFromUser(user);
            testimonial.setCompany(company);
            testimonial.setTestimonial(testimonialDTO.getTestimonial());
            testimonial.setCreationDate(new Date());
            em.persist(testimonial);
            company.getTestimonials().add(testimonial);
            em.getTransaction().commit();
            // fail silently
            try {
                em.getTransaction().begin();
                NotificationHelper.notifySupplier(em, company, SupplierNotification.TYPE.TESTIMONIAL, "New testimonial from user '" + userName + "'", testimonial.getId() + "");
                // add event
                EventHelper.createAndPropagateCompanyEvent(em, company, Category.companies, Event.TYPE.TESTIMONIAL, "User " + userName + " has added a new testimonial for company " + company.getName(), testimonial.getId() + "");
                em.getTransaction().commit();
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                logger.error(e.getMessage(), e);
            }            // add notification
            return testimonial.getId();
        } catch (Exception e) {
            throw handleException(em, e, "Error creating testimonial");
        } finally {
            em.close();
        }
    }

    @Override
    public void updateTestimonial(TestimonialDTO testimonialDTO) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            Testimonial testimonial = em.find(Testimonial.class, testimonialDTO.getId());
            if(testimonial == null) {
                throw new RequestException("Testimonial doesn't exist");
            }
            if(!testimonial.getFromUser().getUsername().contentEquals(userName)) {
                throw new RequestException("Not authorised");
            }
            Company company = em.find(Company.class, testimonialDTO.getCompanyDTO().getId());
            testimonial.setCompany(company);
            testimonial.setTestimonial(testimonialDTO.getTestimonial());
            testimonial.setCreationDate(new Date());
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e, "Error updating testimonial");
        } finally {
            em.close();
        }
    }

    @Override
    public void deleteTestimonial(Long id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            Testimonial testimonial = em.find(Testimonial.class, id);
            if(testimonial == null) {
                throw new RequestException("Testimonial doesn't exist");
            }
            if(!testimonial.getFromUser().getUsername().contentEquals(userName)) {
                throw new RequestException("Not authorised");
            }
            em.remove(testimonial);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e, "Error updating testimonial");
        } finally {
            em.close();
        }
    }

    @Override
    public ProductServiceFormDTO getProductServiceForm(Long id) throws RequestException {
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductService productService = em.find(ProductService.class, id);
            if(productService == null) {
                throw new RequestException("Product does not exist");
            }
            return convertToProductServiceFormDTO(productService);
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    private ProductServiceFormDTO convertToProductServiceFormDTO(ProductService productService) {
        ProductServiceFormDTO productServiceFormDTO = new ProductServiceFormDTO();
        productServiceFormDTO.setId(productService.getId());
        productServiceFormDTO.setName(productService.getName());
        productServiceFormDTO.setDescription(productService.getDescription());
        productServiceFormDTO.setServiceImage(productService.getImageUrl());
        productServiceFormDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(productService.getCompany()));
        productServiceFormDTO.setProduct(ProductHelper.createProductDTO(productService.getProduct()));
        productServiceFormDTO.setFormFields(productService.getProduct().getFormFields());
        return productServiceFormDTO;
    }

    @Override
    public ProductServiceSearchFormDTO getProductServiceSearchForm(String searchId) throws RequestException {
        if(searchId == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            FeasibilitySearch feasibilitySearch = em.find(FeasibilitySearch.class, searchId);
            if(feasibilitySearch == null) {
                throw new RequestException("Search does not exist");
            }
            ProductServiceSearchFormDTO productServiceSearchFormDTO = new ProductServiceSearchFormDTO();
            productServiceSearchFormDTO.setSearchId(searchId);
            productServiceSearchFormDTO.setAoiWKT(feasibilitySearch.getSelectionGeometry());
            productServiceSearchFormDTO.setProductServiceFormDTO(convertToProductServiceFormDTO(feasibilitySearch.getProductService()));
            productServiceSearchFormDTO.setValues(feasibilitySearch.getFormValues());
            return productServiceSearchFormDTO;
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public CompanyDTO createCompany(CreateCompanyDTO createCompanyDTO) throws RequestException {
        EntityManager em = EMF.get().createEntityManager();
        try {
            com.geocento.webapps.eobroker.customer.shared.utils.CompanyHelper.checkCompanyValues(createCompanyDTO);
            em.getTransaction().begin();
            // TODO - check a few values
            TypedQuery<Long> query = em.createQuery("select count(c) from Company c where c.name = :companyName", Long.class);
            query.setParameter("companyName", createCompanyDTO.getName());
            if(query.getSingleResult() > 0) {
                throw new RequestException("Company name is already taken");
            }
            Company company = new Company();
            company.setName(createCompanyDTO.getName());
            company.setDescription(createCompanyDTO.getDescription());
            company.setAddress(createCompanyDTO.getAddress());
            company.setCountryCode(createCompanyDTO.getCountryCode());
            company.setStatus(REGISTRATION_STATUS.PENDING);
            // TODO - manage companies which are not suppliers!
            if(createCompanyDTO.isSupplier()) {
                company.setSupplier(createCompanyDTO.isSupplier());
            }
            em.persist(company);
            // add notification
            try {
                NotificationHelper.notifyAdmin(em, AdminNotification.TYPE.COMPANY,
                        "New company '" + company.getName() + "' has been created, please validate", company.getId() + "");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            em.getTransaction().commit();
            return CompanyHelper.createCompanyDTO(company);
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public void createUser(CreateUserDTO createUserDTO) throws RequestException {
        EntityManager em = EMF.get().createEntityManager();
        try {
            try {
                com.geocento.webapps.eobroker.customer.shared.utils.UserHelper.checkUserValues(createUserDTO);
            } catch (Exception e) {
                throw new RequestException(e.getMessage());
            }
            if(em.find(User.class, createUserDTO.getUserName()) != null) {
                throw new RequestException("User name is already taken");
            }
            TypedQuery<User> query = em.createQuery("select u from users u WHERE u.email = :email", User.class);
            query.setParameter("email", createUserDTO.getEmail());
            List<User> results = query.getResultList();
            if(results != null && results.size() > 0) {
                throw new RequestException("Email address is already used");
            }
            em.getTransaction().begin();
            // TODO - check a few values
            User user = new User();
            user.setStatus(REGISTRATION_STATUS.PENDING);
            user.setUsername(createUserDTO.getUserName());
            user.setPassword(com.geocento.webapps.eobroker.common.server.Utils.UserUtils.createPasswordHash(createUserDTO.getUserPassword()));
            user.setEmail(createUserDTO.getEmail());
            user.setRole(User.USER_ROLE.customer);
            // find company
            Company dbCompany = em.find(Company.class, createUserDTO.getCompanyId());
            if(dbCompany == null) {
                throw new RequestException("Missing company");
            }
            user.setCompany(dbCompany);
            user.setCreationDate(new Date());
/*
            // TODO - manage companies which are not suppliers!
            if(dbCompany.isSupplier()) {
                //company.setSupplier(companyDTO.isSupplier());
            }
*/
            em.persist(user);
            em.getTransaction().commit();
            // add notification
            em.getTransaction().begin();
            try {
                NotificationHelper.notifyAdmin(em, AdminNotification.TYPE.USER, "New user '" + user.getUsername() + "' has registered, please validate", user.getUsername());
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            em.getTransaction().commit();
            StatsHelper.addCounter("users.signup", 1);
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public SettingsDTO getUserSettings() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            SettingsDTO settingsDTO = new SettingsDTO();
            Company company = user.getCompany();
            CustomerSettings customerSettings = user.getCustomerSettings();
            settingsDTO.setUserIconUrl(user.getUserIcon());
            settingsDTO.setFullName(user.getFullName());
            settingsDTO.setEmail(user.getEmail());
            settingsDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(company));
            return settingsDTO;
        } catch (Exception e) {
            throw handleException(em, e, "Error loading settings");
        } finally {
            em.close();
        }
    }

    @Override
    public void saveUserSettings(SettingsDTO settingsDTO) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            CustomerSettings customerSettings = user.getCustomerSettings();
            user.setUserIcon(settingsDTO.getUserIconUrl());
            user.setFullName(settingsDTO.getFullName());
            user.setEmail(settingsDTO.getEmail());
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e, "Error updating settings");
        } finally {
            em.close();
        }
    }

    @Override
    public List<DatasetAccessOGC> getSelectedLayers() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            return user.getSelectedLayers();
        } catch (Exception e) {
            throw handleException(em, e, "Error getting user selected layers");
        } finally {
            em.close();
        }
    }

    @Override
    public void addSelectedLayer(Long layerId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            DatasetAccessOGC datasetAccessOGC = em.find(DatasetAccessOGC.class, layerId);
            if(datasetAccessOGC == null) {
                throw new RequestException("Layer with id " + layerId + " does not exist");
            }
            if(user.getSelectedLayers().contains(datasetAccessOGC)) {
                return;
            }
            user.getSelectedLayers().add(datasetAccessOGC);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public void removeSelectedLayer(Long layerId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            List<DatasetAccessOGC> selectedLayers = user.getSelectedLayers();
            DatasetAccessOGC selectedLayer = ListUtil.findValue(selectedLayers, new ListUtil.CheckValue<DatasetAccessOGC>() {
                @Override
                public boolean isValue(DatasetAccessOGC value) {
                    return value.getId().equals(layerId);
                }
            });
            selectedLayers.remove(selectedLayer);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<DatasetAccessOGC> getCompanySavedLayers() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            return user.getCompany().getSavedLayers();
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

/*
    @Override
    public void addCompanySavedLayer(DatasetAccessOGC datasetAccessOGC) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            if(datasetAccessOGC.getId() != null) {

            }
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public void removeCompanyLayer(Long layerId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            List<DatasetAccessOGC> selectedLayers = user.getSelectedLayers();
            DatasetAccessOGC selectedLayer = ListUtil.findValue(selectedLayers, new ListUtil.CheckValue<DatasetAccessOGC>() {
                @Override
                public boolean isValue(DatasetAccessOGC value) {
                    return value.getId().equals(layerId);
                }
            });
            selectedLayers.remove(selectedLayer);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }
*/

    @Override
    public List<DatasetAccessOGC> getApplicationLayers() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        return ServerUtil.getSettings().getBaseLayers();
    }

    @Override
    public List<DatasetAccessOGC> getSavedLayers() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            return user.getSavedLayers();
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public void addSavedLayer(Long layerId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            DatasetAccessOGC datasetAccessOGC = em.find(DatasetAccessOGC.class, layerId);
            if(datasetAccessOGC == null) {
                throw new RequestException("Layer with id " + layerId + " does not exist");
            }
            if(user.getSavedLayers().contains(datasetAccessOGC)) {
                return;
            }
            user.getSavedLayers().add(datasetAccessOGC);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public void removeSavedLayer(Long layerId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            List<DatasetAccessOGC> savedLayers = user.getSavedLayers();
            DatasetAccessOGC savedLayer = ListUtil.findValue(savedLayers, new ListUtil.CheckValue<DatasetAccessOGC>() {
                @Override
                public boolean isValue(DatasetAccessOGC value) {
                    return value.getId().equals(layerId);
                }
            });
            savedLayers.remove(savedLayer);
            em.getTransaction().commit();
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public SuccessStoryDTO getSuccessStory(Long successStoryId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            SuccessStory successStory = em.find(SuccessStory.class, successStoryId);
            if(successStory == null) {
                throw new RequestException("Could not find success story");
            }
            return convertToSuccessStoryDTO(successStory, true);
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public void inviteColleague(String email) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            String websiteUrl = ServerUtil.getSettings().getWebsiteUrl();
            String linkUrl = websiteUrl + "#requestAccess:companyId=" + user.getCompany().getId();
            MailContent mailContent = new MailContent(MailContent.EMAIL_TYPE.CONSUMER);
            mailContent.addTitle(user.getFullName() + " has invited you to join the EO Broker platform!");
            mailContent.addLine("Use the button below to join your " + user.getCompany().getName() + " colleagues and the other members of the EO Broker community.");
            mailContent.addAction("Register with the EO Broker", null, linkUrl);
            mailContent.sendEmail(user.getEmail(), "Your invitation to the EO Broker platform", false);
        } catch (Exception e) {
            throw handleException(em, e);
        } finally {
            em.close();
        }
    }

    @Override
    public Boolean subscribeCompanyMessages(String conversationId) throws RequestException {
/*
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            Conversation conversation = em.find(Conversation.class, conversationId);
            if(conversation == null) {
                throw new RequestException("Could not find conversation with id " + conversationId);
            }
            return SupplierNotificationSocket.subscribeConversation(userName, conversation);
        } catch (Exception e) {
            throw handleException(e);
        } finally {
            em.close();
        }
*/
        return false;
    }

    private SuccessStoryDTO convertToSuccessStoryDTO(SuccessStory successStory, boolean full) {
        SuccessStoryDTO successStoryDTO = new SuccessStoryDTO();
        successStoryDTO.setId(successStory.getId());
        successStoryDTO.setImageUrl(successStory.getImageUrl());
        successStoryDTO.setName(successStory.getName());
        successStoryDTO.setCompany(CompanyHelper.createCompanyDTO(successStory.getSupplier()));
        successStoryDTO.setCustomer(CompanyHelper.createCompanyDTO(successStory.getCustomer()));
        if(full) {
            successStoryDTO.setDescription(successStory.getFullDescription());
            successStoryDTO.setEndorsements(ListUtil.mutate(successStory.getEndorsements(), new ListUtil.Mutate<Endorsement, EndorsementDTO>() {
                @Override
                public EndorsementDTO mutate(Endorsement endorsement) {
                    return createEndorsementDTO(endorsement);
                }
            }));
        } else {
            successStoryDTO.setDescription(successStory.getDescription());
        }
        successStoryDTO.setProductDTO(ProductHelper.createProductDTO(successStory.getProduct()));
        successStoryDTO.setDatasets(ListUtil.mutate(successStory.getProductDatasets(), productDataset -> createProductDatasetDTO(productDataset)));
        successStoryDTO.setServices(ListUtil.mutate(successStory.getProductServices(), productService -> createProductServiceDTO(productService)));
        successStoryDTO.setSoftware(ListUtil.mutate(successStory.getSoftware(), software -> createSoftwareDTO(software)));
        successStoryDTO.setPeriod(successStory.getPeriod());
        return successStoryDTO;
    }

    private EndorsementDTO createEndorsementDTO(Endorsement endorsement) {
        EndorsementDTO endorsementDTO = new EndorsementDTO();
        endorsementDTO.setId(endorsement.getId());
        endorsementDTO.setFromUser(UserHelper.createUserDTO(endorsement.getFromUser()));
        endorsementDTO.setTestimonial(endorsement.getTestimonial());
        endorsementDTO.setCreationDate(endorsement.getCreationDate());
        return endorsementDTO;
    }

    private RequestException handleException(Exception e) {
        return handleException(null, e, "Server side error");
    }

    private RequestException handleException(EntityManager em, Exception e) {
        return handleException(em, e, "Server side error");
    }

    private RequestException handleException(EntityManager em, Exception e, String message) {
        if(em != null && em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        logger.error(e.getMessage(), e);
        if(e instanceof RequestException) {
            return (RequestException) e;
        } else {
            return new RequestException(message);
        }
    }

}
