package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.*;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;
import com.geocento.webapps.eobroker.common.shared.entities.utils.ProductHelper;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.services.AssetsService;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.google.gwt.http.client.RequestException;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.Path;
import java.util.List;

@Path("/")
public class AssetsResource implements AssetsService {

    Logger logger = Logger.getLogger(AssetsResource.class);

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
            return productDescriptionDTO;
        } finally {
            em.close();
        }
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
                    ProductServiceDTO productServiceDTO = new ProductServiceDTO();
                    productServiceDTO.setId(productService.getId());
                    productServiceDTO.setName(productService.getName());
                    productServiceDTO.setDescription(productService.getDescription());
                    productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
                    productServiceDTO.setCompanyName(productService.getCompany().getName());
                    productServiceDTO.setCompanyId(productService.getCompany().getId());
                    productServiceDTO.setServiceImage(productService.getImageUrl());
                    productServiceDTO.setHasFeasibility(productService.getApiUrl() != null && productService.getApiUrl().length() > 0);
                    return productServiceDTO;
                }
            }));
            return companyDTO;
        } catch (Exception e) {
            throw new RequestException("Server error");
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
            productServiceDescriptionDTO.setName(productService.getName());
            productServiceDescriptionDTO.setDescription(productService.getDescription());
            productServiceDescriptionDTO.setFullDescription(productService.getFullDescription());
            productServiceDescriptionDTO.setWebsite(productService.getWebsite());
            productServiceDescriptionDTO.setCompany(CompanyHelper.createCompanyDTO(productService.getCompany()));
            productServiceDescriptionDTO.setProduct(ProductHelper.createProductDTO(productService.getProduct()));
            return productServiceDescriptionDTO;
        } catch (Exception e) {
            throw new RequestException("Server error");
        } finally {
            em.close();
        }
    }

    @Override
    public ProductServiceDTO getProductService(Long id) {
        return null;
    }

    @Override
    public Long addProductService(ProductServiceDTO productService) {
        return null;
    }

    @Override
    public void updateProductService(ProductServiceDTO product) {

    }

    @Override
    public CompanyDTO getCompany(Long id) {
        return null;
    }

    @Override
    public Long addCompany(CompanyDTO company) {
        return null;
    }

    @Override
    public void updateCompany(CompanyDTO product) {

    }
}
