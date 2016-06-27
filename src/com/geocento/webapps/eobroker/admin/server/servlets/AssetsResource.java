package com.geocento.webapps.eobroker.admin.server.servlets;

import com.geocento.webapps.eobroker.admin.client.services.AssetsService;
import com.geocento.webapps.eobroker.admin.server.util.UserUtils;
import com.geocento.webapps.eobroker.admin.shared.dtos.EditProductDTO;
import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.AuthorizationException;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.*;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;
import com.geocento.webapps.eobroker.common.shared.entities.utils.ProductHelper;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.google.gwt.http.client.RequestException;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
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
    public Long updateProduct(EditProductDTO productDTO) throws RequestException {
        UserUtils.verifyUserAdmin(request);
        if(productDTO == null) {
            throw new RequestException("Product is required");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            Product product = null;
            if (productDTO.getId() != null) {
                product = em.find(Product.class, productDTO.getId());
                if (product == null) {
                    throw new RequestException("Could not find product with id '" + productDTO.getId() + "'");
                }
            } else {
                product = new Product();
            }
            product.setName(productDTO.getName());
            product.setImageUrl(productDTO.getImageUrl());
            product.setShortDescription(productDTO.getShortDescription());
            product.setDescription(productDTO.getDescription());
            product.setSector(productDTO.getSector());
            product.setThematic(productDTO.getThematic());
            product.setFormFields(productDTO.getFormFields());
            for(FormElement formElement : product.getFormFields()) {
                if(formElement.getId() == null) {
                    em.persist(formElement);
                }
            }
            if(product.getId() == null) {
                em.persist(product);
            } else {
                em.merge(product);
            }
            // update the keyphrases
            Query query = em.createNativeQuery("UPDATE product SET tsv = " + getProductTSV(product) +
                    ", tsvname = " + getProductNameTSV(product) + " where id = " + product.getId() +
                    ";");
            query.executeUpdate();
            em.getTransaction().commit();
            return product.getId();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RequestException("Server error");
        } finally {
            em.close();
        }
    }

    private static String getProductNameTSV(Product product) {
        return "setweight(to_tsvector('english','product'), 'A') || setweight(to_tsvector('english','" + product.getName() + "'), 'B')";
    }

    private static String getProductTSV(Product product) {
        return "setweight(to_tsvector('english','product " + product.getSector().toString() + " " + product.getThematic().toString() + " " + product.getName() + "'), 'A') " +
                "|| setweight(to_tsvector('english','" + product.getShortDescription() + "'), 'B')";
    }

    @Override
    public EditProductDTO getProduct(Long id) throws RequestException {
        UserUtils.verifyUserAdmin(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            Product product = em.find(Product.class, id);
            if(product == null) {
                throw new RequestException("Unknown product");
            }
            EditProductDTO productDTO = new EditProductDTO();
            productDTO.setId(product.getId());
            productDTO.setName(product.getName());
            productDTO.setShortDescription(product.getShortDescription());
            productDTO.setDescription(product.getDescription());
            productDTO.setImageUrl(product.getImageUrl());
            productDTO.setSector(product.getSector());
            productDTO.setThematic(product.getThematic());
            productDTO.setFormFields(product.getFormFields());
            return productDTO;
        } catch (Exception e) {
            throw new RequestException("Error");
        } finally {
            em.close();
        }
    }

    @Override
    public List<ProductDTO> listProducts(int start, int limit, String orderBy) throws RequestException {
        UserUtils.verifyUserAdmin(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            if(orderBy == null) {
                orderBy = "p.name";
            } else {
                switch(orderBy) {
                    case "name":
                        orderBy = "p.name";
                        break;
                    default:
                        orderBy = "p.name";
                }
            }
            TypedQuery<Product> query = em.createQuery("select p from Product p order by " + orderBy, Product.class);
            query.setFirstResult(start);
            query.setMaxResults(limit);
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
        UserUtils.verifyUserAdmin(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductService productService = em.find(ProductService.class, id);
            if(productService == null) {
                throw new RequestException("Unknown product");
            }
            ProductServiceDTO productServiceDTO = new ProductServiceDTO();
            productServiceDTO.setId(productService.getId());
            productServiceDTO.setName(productService.getName());
            productServiceDTO.setDescription(productService.getDescription());
            productServiceDTO.setEmail(productService.getEmail());
            productServiceDTO.setWebsite(productService.getWebsite());
            productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
            productServiceDTO.setCompanyName(productService.getCompany().getName());
            productServiceDTO.setServiceImage(productService.getImageUrl());
            return productServiceDTO;
        } catch (Exception e) {
            throw new RequestException("Error");
        } finally {
            em.close();
        }
    }

    @Override
    public List<ProductServiceDTO> listProductServices() throws RequestException {
        String userName = UserUtils.verifyUserAdmin(request);
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
                    productServiceDTO.setEmail(productService.getEmail());
                    productServiceDTO.setWebsite(productService.getWebsite());
                    productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
                    productServiceDTO.setCompanyName(productService.getCompany().getName());
                    productServiceDTO.setServiceImage(productService.getImageUrl());
                    productServiceDTO.setProduct(ProductHelper.createProductDTO(productService.getProduct()));
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
    public void updateProductService(ProductServiceDTO productServiceDTO) throws RequestException {
        String userName = UserUtils.verifyUserAdmin(request);
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
            Product product = em.find(Product.class, productServiceDTO.getProduct().getId());
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
        String userName = UserUtils.verifyUserAdmin(request);
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
    public List<CompanyDTO> listCompanies() throws RequestException {
        String userName = UserUtils.verifyUserAdmin(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            TypedQuery<Company> query = em.createQuery("select c from Company c", Company.class);
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<Company, CompanyDTO>() {
                @Override
                public CompanyDTO mutate(Company company) {
                    return CompanyHelper.createCompanyDTO(company);
                }
            });
        } catch (Exception e) {
            throw new RequestException("Server error");
        } finally {
            em.close();
        }
    }

    @Override
    public void updateCompany(CompanyDTO companyDTO) throws RequestException {
        String userName = UserUtils.verifyUserAdmin(request);
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
}
