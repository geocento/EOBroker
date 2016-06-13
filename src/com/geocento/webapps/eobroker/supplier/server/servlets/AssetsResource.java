package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.AuthorizationException;
import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.ProductService;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.*;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;
import com.geocento.webapps.eobroker.common.shared.entities.utils.ProductHelper;
import com.geocento.webapps.eobroker.common.shared.entities.utils.ProductServiceHelper;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.services.AssetsService;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import com.google.gwt.http.client.RequestException;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
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
    public ProductServiceDTO getProductService(Long id) throws RequestException {
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
            return ProductServiceHelper.createProductServiceDTO(productService);
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
                    return ProductServiceHelper.createProductServiceDTO(productService);
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
    public void updateProductService(ProductServiceDTO productServiceDTO) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        if(productServiceDTO == null || productServiceDTO.getId() == null) {
            throw new RequestException("Product service cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        ProductService productService = em.find(ProductService.class, productServiceDTO.getId());
        if(productService == null) {
            throw new RequestException("Unknown product");
        }
        User user = em.find(User.class, userName);
        if(!user.getCompany().getName().contentEquals(productServiceDTO.getCompanyName())) {
            throw new AuthorizationException();
        }
        try {
            em.getTransaction().begin();
            productService.setName(productServiceDTO.getName());
            productService.setDescription(productServiceDTO.getDescription());
            Product product = em.find(Product.class, productServiceDTO.getProductId());
            if (product == null) {
                throw new RequestException("Product does not exist");
            }
            productService.setProduct(product);
            em.getTransaction().commit();
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException("Error updating product");
        } finally {
            em.close();
        }
    }

    @Override
    public CompanyDTO getCompany(Long id) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        if(id == null) {
            throw new RequestException("Company id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        Company company = em.find(Company.class, id);
        if(company == null) {
            throw new RequestException("Unknown company");
        }
        return CompanyHelper.createCompanyDTO(company);
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
}
