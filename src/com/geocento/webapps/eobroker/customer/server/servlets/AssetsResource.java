package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIPolygonDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.Notification;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.services.AssetsService;
import com.geocento.webapps.eobroker.customer.server.utils.RankedOffer;
import com.geocento.webapps.eobroker.customer.server.utils.UserUtils;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.geocento.webapps.eobroker.customer.shared.utils.ProductHelper;
import com.google.gwt.http.client.RequestException;
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
        } finally {
            em.close();
        }
    }

    @Override
    public ProductFeasibilityDTO getProductFeasibility(Long id) throws RequestException {
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
                    productServiceFeasibilityDTO.setCompanyName(productService.getCompany().getName());
                    productServiceFeasibilityDTO.setApiURL(productService.getApiUrl());
                    return productServiceFeasibilityDTO;
                }
            }));
            return productFeasibilityDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public ProductFormDTO getProductForm(Long id) throws RequestException {
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
        } finally {
            em.close();
        }
    }

    @Override
    public ProductDescriptionDTO getProductDescription(Long id) throws RequestException {
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
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
            // add relevant supplier services
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
            return productDescriptionDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public ProductServiceDescriptionDTO getProductServiceDescription(Long id) throws RequestException {
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
            productServiceDescriptionDTO.setHasFeasibility(productService.getApiUrl() != null);
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
            return productServiceDescriptionDTO;
        } catch (Exception e) {
            throw new RequestException("Server error");
        } finally {
            em.close();
        }
    }

    @Override
    public ProductDatasetDescriptionDTO getProductDatasetDescription(Long id) throws RequestException {
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
            productDatasetDescriptionDTO.setExtent(productDataset.getExtent());
            productDatasetDescriptionDTO.setCompany(CompanyHelper.createCompanyDTO(productDataset.getCompany()));
            productDatasetDescriptionDTO.setProduct(ProductHelper.createProductDTO(productDataset.getProduct()));
            productDatasetDescriptionDTO.setDatasetAccesses(productDataset.getDatasetAccesses());
            List<ProductDataset> suggestedDatasets = new ArrayList<ProductDataset>(productDataset.getProduct().getProductDatasets());
            suggestedDatasets.remove(productDataset);
            productDatasetDescriptionDTO.setSuggestedDatasets(ListUtil.mutate(suggestedDatasets, new ListUtil.Mutate<ProductDataset, ProductDatasetDTO>() {
                @Override
                public ProductDatasetDTO mutate(ProductDataset productDataset) {
                    return createProductDatasetDTO(productDataset);
                }
            }));
            return productDatasetDescriptionDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public SoftwareDescriptionDTO getSoftwareDescription(Long id) throws RequestException {
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
            softwareDescriptionDTO.setProducts(ListUtil.mutate(software.getProducts(), new ListUtil.Mutate<ProductSoftware, ProductSoftwareDTO>() {
                @Override
                public ProductSoftwareDTO mutate(ProductSoftware productSoftware) {
                    return createProductSoftwareDTO(productSoftware);
                }
            }));
            return softwareDescriptionDTO;
        } finally {
            em.close();
        }
    }

    @Override
    public ProjectDescriptionDTO getProjectDescription(Long id) throws RequestException {
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
            return projectDescriptionDTO;
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
    public List<NewsItem> getNewsItems() {
        EntityManager em = EMF.get().createEntityManager();
        TypedQuery<NewsItem> query = em.createQuery("select n from NewsItem n ORDER BY n.creationDate", NewsItem.class);
        query.setMaxResults(5);
        return query.getResultList();
    }

    @Override
    public List<Offer> getRecommendations() {
        List<RankedOffer> offers = new ArrayList<RankedOffer>();
        EntityManager em = EMF.get().createEntityManager();
        try {
            // TODO - find a recommendation system
            Query q = em.createNativeQuery("SELECT id, category FROM textsearch WHERE category not in ('product') LIMIT 5;");
            List<Object[]> results = q.getResultList();
            if(results.size() > 0) {
                final HashMap<Long, Double> rankings = new HashMap<Long, Double>();
                List<Long> productServiceIds = new ArrayList<Long>();
                List<Long> productDatasetIds = new ArrayList<Long>();
                List<Long> softwareIds = new ArrayList<Long>();
                for (Object[] result : results) {
                    Long id = (Long) result[0];
                    // TODO - provide a ranking value
                    Double ranking = Math.random();
                    rankings.put(id, ranking);
                    switch((String) result[1]) {
                        case "productservice":
                            productServiceIds.add(id);
                            break;
                        case "productdataset":
                            productDatasetIds.add(id);
                            break;
                        case "software":
                            softwareIds.add(id);
                            break;
                    }
                }
                // now fetch the actual entities
                // start with product
                // then product services
                if(productServiceIds.size() > 0) {
                    TypedQuery<ProductService> productServiceQuery = em.createQuery("select p from ProductService p where p.id IN :productIds", ProductService.class);
                    productServiceQuery.setParameter("productIds", productServiceIds);
                    offers.addAll(ListUtil.mutate(productServiceQuery.getResultList(), new ListUtil.Mutate<ProductService, RankedOffer>() {
                        @Override
                        public RankedOffer mutate(ProductService productService) {
                            return new RankedOffer(rankings.get(productService.getId()), createProductServiceDTO(productService));
                        }
                    }));
                }
                // then product datasets
                if(productDatasetIds.size() > 0) {
                    TypedQuery<ProductDataset> productDatasetQuery = em.createQuery("select p from ProductDataset p where p.id IN :productIds", ProductDataset.class);
                    productDatasetQuery.setParameter("productIds", productServiceIds);
                    offers.addAll(ListUtil.mutate(productDatasetQuery.getResultList(), new ListUtil.Mutate<ProductDataset, RankedOffer>() {
                        @Override
                        public RankedOffer mutate(ProductDataset productDataset) {
                            return new RankedOffer(rankings.get(productDataset.getId()), createProductDatasetDTO(productDataset));
                        }
                    }));
                }
                if(softwareIds.size() > 0) {
                    TypedQuery<Software> softwareQuery = em.createQuery("select s from Software s where s.id IN :softwareIds", Software.class);
                    softwareQuery.setParameter("softwareIds", softwareIds);
                    List<Software> softwares = softwareQuery.getResultList();
                    offers.addAll(ListUtil.mutate(softwares, new ListUtil.Mutate<Software, RankedOffer>() {
                        @Override
                        public RankedOffer mutate(Software software) {
                            return new RankedOffer(rankings.get(software.getId()), createSoftwareDTO(software));
                        }
                    }));
                }
            }
            if(offers.size() > 4) {
                offers = offers.subList(0, 4);
            }
            return ListUtil.mutate(offers, new ListUtil.Mutate<RankedOffer, Offer>() {
                @Override
                public Offer mutate(RankedOffer object) {
                    return object.getOffer();
                }
            });
        } finally {
            em.close();
        }
    }

    @Override
    public CompanyDescriptionDTO getCompanyDescription(Long id) throws RequestException {
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Company company = em.find(Company.class, id);
            if (company == null) {
                throw new RequestException("Company does not exist");
            }
            CompanyDescriptionDTO companyDTO = new CompanyDescriptionDTO();
            companyDTO.setId(company.getId());
            companyDTO.setName(company.getName());
            companyDTO.setDescription(company.getDescription());
            companyDTO.setFullDescription(company.getFullDescription());
            companyDTO.setIconURL(company.getIconURL());
            companyDTO.setContactEmail(company.getContactEmail());
            companyDTO.setWebsite(company.getWebsite());
            companyDTO.setProductServices(ListUtil.mutate(company.getServices(), new ListUtil.Mutate<ProductService, ProductServiceDTO>() {
                @Override
                public ProductServiceDTO mutate(ProductService productService) {
                    return createProductServiceDTO(productService);
                }
            }));
            companyDTO.setProductDatasets(ListUtil.mutate(company.getDatasets(), new ListUtil.Mutate<ProductDataset, ProductDatasetDTO>() {
                @Override
                public ProductDatasetDTO mutate(ProductDataset object) {
                    return createProductDatasetDTO(object);
                }
            }));
            companyDTO.setSoftware(ListUtil.mutate(company.getSoftware(), new ListUtil.Mutate<Software, SoftwareDTO>() {
                @Override
                public SoftwareDTO mutate(Software software) {
                    return createSoftwareDTO(software);
                }
            }));
            companyDTO.setProject(ListUtil.mutate(company.getProjects(), new ListUtil.Mutate<Project, ProjectDTO>() {
                @Override
                public ProjectDTO mutate(Project project) {
                    return createProjectDTO(project);
                }
            }));
            return companyDTO;
        } catch (Exception e) {
            throw new RequestException("Server error");
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
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Company company = em.find(Company.class, id);
            if (company == null) {
                throw new RequestException("Company does not exist");
            }
            return CompanyHelper.createCompanyDTO(company);
        } catch (Exception e) {
            throw new RequestException("Server error");
        } finally {
            em.close();
        }
    }

    @Override
    public List<NotificationDTO> getNotifications() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<Notification> query = em.createQuery("select s from Notification s where s.user = :user order by s.creationDate", Notification.class);
            query.setParameter("user", user);
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<Notification, NotificationDTO>() {
                @Override
                public NotificationDTO mutate(Notification notification) {
                    NotificationDTO notificationDTO = new NotificationDTO();
                    notificationDTO.setType(notification.getType());
                    notificationDTO.setMessage(notification.getMessage());
                    notificationDTO.setLinkId(notification.getLinkId());
                    notificationDTO.setCreationDate(notification.getCreationDate());
                    return notificationDTO;
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

}
