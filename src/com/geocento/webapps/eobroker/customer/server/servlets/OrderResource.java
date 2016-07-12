package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.Utils.KeyGenerator;
import com.geocento.webapps.eobroker.common.server.Utils.NotificationHelper;
import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.ProductService;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;
import com.geocento.webapps.eobroker.common.shared.entities.orders.ProductServiceRequest;
import com.geocento.webapps.eobroker.common.shared.entities.orders.ProductServiceSupplierRequest;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.services.OrderService;
import com.geocento.webapps.eobroker.customer.server.utils.UserUtils;
import com.geocento.webapps.eobroker.customer.shared.ImageRequestDTO;
import com.geocento.webapps.eobroker.customer.shared.OrderDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceRequestDTO;
import com.google.gwt.http.client.RequestException;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

@Path("/")
public class OrderResource implements OrderService {

    static Logger logger = Logger.getLogger(AssetsResource.class);

    static KeyGenerator keyGenerator = new KeyGenerator(16);

    // assumes service is not a singleton
    @Context
    HttpServletRequest request;

    public OrderResource() {
        logger.info("Starting order service");
    }

    @Override
    public OrderDTO getOrder(String id) {
        return null;
    }

    @Override
    public String submitImageRequest(ImageRequestDTO imageRequestDTO) {
        return null;
    }

    @Override
    public String submitProductRequest(ProductServiceRequestDTO productServiceRequestDTO) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(productServiceRequestDTO == null) {
            throw new RequestException("Product service request cannot be null");
        }
        Long productId = productServiceRequestDTO.getProductId();
        final List<Long> productServiceIds = productServiceRequestDTO.getProductServiceIds();
        List<FormElementValue> values = productServiceRequestDTO.getValues();
        if(productId == null) {
            throw new RequestException("Product id is required");
        }
        if(productServiceIds == null || productServiceIds.size() == 0) {
            throw new RequestException("At least one supplier service id is required");
        }
        if(values == null || values.size() == 0) {
            throw new RequestException("Values are required");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            // get user
            User user = em.find(User.class, userName);
            // find product
            Product product = em.find(Product.class, productId);
            if (product == null) {
                throw new RequestException("Could not find product");
            }
            List<ProductService> productServices = ListUtil.filterValues(new ArrayList<ProductService>(product.getProductServices()), new ListUtil.CheckValue<ProductService>() {
                @Override
                public boolean isValue(ProductService value) {
                    return productServiceIds.contains(value.getId());
                }
            });
            if (productServices == null || productServices.size() == 0) {
                throw new RequestException("Could not find matching services for this product");
            }
            em.getTransaction().begin();
            // store the request
            ProductServiceRequest productServiceRequest = new ProductServiceRequest();
            productServiceRequest.setId(keyGenerator.CreateKey());
            productServiceRequest.setProduct(product);
            productServiceRequest.setCustomer(user);
            // TODO - check values are legitimate and correct?
            productServiceRequest.setFormValues(values);
            productServiceRequest.setSupplierRequests(new ArrayList<ProductServiceSupplierRequest>());
            em.persist(productServiceRequest);
            for (ProductService productService : productServices) {
                ProductServiceSupplierRequest productServiceSupplierRequest = new ProductServiceSupplierRequest();
                productServiceSupplierRequest.setProductService(productService);
                productServiceSupplierRequest.setProductServiceRequest(productServiceRequest);
                productServiceRequest.getSupplierRequests().add(productServiceSupplierRequest);
                em.persist(productServiceSupplierRequest);
                // notify the supplier
                NotificationHelper.notifySupplier(em, productService.getCompany(), SupplierNotification.TYPE.REQUEST, "New request for quotation for service '" + productService.getName() + "' from user '" + user.getUsername() + "'", productServiceRequest.getId());
            }
            em.getTransaction().commit();
            return productServiceRequest.getId();
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException("Could not submit product request, server error");
        } finally {
            em.close();
        }
    }
}
