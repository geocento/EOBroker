package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.orders.ProductServiceRequest;
import com.geocento.webapps.eobroker.common.shared.entities.orders.ProductServiceSupplierRequest;
import com.geocento.webapps.eobroker.common.shared.entities.utils.ProductHelper;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.services.OrdersService;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceSupplierRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.UserHelper;
import com.google.gwt.http.client.RequestException;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("/")
public class OrdersResource implements OrdersService {

    Logger logger = Logger.getLogger(OrdersResource.class);

    // assumes service is not a singleton
    @Context
    HttpServletRequest request;

    public OrdersResource() {
        logger.info("Starting service...");
    }

    @Override
    public ProductServiceSupplierRequestDTO getRequest(String id) throws RequestException {
        String logUserName = UserUtils.verifyUserSupplier(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            final User user = em.find(User.class, logUserName);
            ProductServiceRequest productServiceRequest = em.find(ProductServiceRequest.class, id);
            if(productServiceRequest == null) {
                throw new RequestException("Unknown product request");
            }
            ProductServiceSupplierRequestDTO productServiceSupplierRequestDTO = new ProductServiceSupplierRequestDTO();
            productServiceSupplierRequestDTO.setId(productServiceRequest.getId());
            productServiceSupplierRequestDTO.setProduct(ProductHelper.createProductDTO(productServiceRequest.getProduct()));
            productServiceSupplierRequestDTO.setCustomer(UserHelper.createUserDTO(productServiceRequest.getCustomer()));
            productServiceSupplierRequestDTO.setFormValues(productServiceRequest.getFormValues());
            // get the supplier specific request
            ProductServiceSupplierRequest productSupplierServiceRequest = ListUtil.findValue(productServiceRequest.getSupplierRequests(), new ListUtil.CheckValue<ProductServiceSupplierRequest>() {
                @Override
                public boolean isValue(ProductServiceSupplierRequest value) {
                    return value.getProductService().getCompany().getId().longValue() == user.getCompany().getId().longValue();
                }
            });
            if(productSupplierServiceRequest == null) {
                throw new RequestException("Could not find supplier service request");
            }
            productServiceSupplierRequestDTO.setSupplierResponse(productSupplierServiceRequest.getResponse());
            return productServiceSupplierRequestDTO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException("Error");
        } finally {
            em.close();
        }
    }

}
