package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.orders.*;
import com.geocento.webapps.eobroker.common.shared.entities.utils.ProductHelper;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.services.OrdersService;
import com.geocento.webapps.eobroker.supplier.server.util.MessageHelper;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ImageryServiceRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ImagesRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductServiceSupplierRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.UserHelper;
import com.google.gwt.http.client.RequestException;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.List;

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
    public ProductServiceSupplierRequestDTO getProductRequest(String id) throws RequestException {
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
            productServiceSupplierRequestDTO.setAoIWKT(productServiceRequest.getAoIWKT());
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
            productServiceSupplierRequestDTO.setMessages(MessageHelper.convertToDTO(productSupplierServiceRequest.getMessages()));
            return productServiceSupplierRequestDTO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException("Error");
        } finally {
            em.close();
        }
    }

    @Override
    public ImageryServiceRequestDTO getImageryRequest(String id) throws RequestException {
        String logUserName = UserUtils.verifyUserSupplier(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            final User user = em.find(User.class, logUserName);
            ImageryFormRequest imageryFormRequest = em.find(ImageryFormRequest.class, id);
            if(imageryFormRequest == null) {
                throw new RequestException("Unknown imagery request");
            }
            ImageryServiceRequestDTO imageryServiceRequestDTO = new ImageryServiceRequestDTO();
            imageryServiceRequestDTO.setId(imageryFormRequest.getId());
            imageryServiceRequestDTO.setImageType(imageryFormRequest.getImageType());
            imageryServiceRequestDTO.setAoiWKT(imageryFormRequest.getAoiWKT());
            imageryServiceRequestDTO.setStart(imageryFormRequest.getStart());
            imageryServiceRequestDTO.setStop(imageryFormRequest.getStop());
            imageryServiceRequestDTO.setAdditionalInformation(imageryFormRequest.getAdditionalInformation());
            imageryServiceRequestDTO.setCreationDate(imageryFormRequest.getCreationDate());
            imageryServiceRequestDTO.setCustomer(UserHelper.createUserDTO(imageryFormRequest.getCustomer()));
            // get the supplier specific request
            ImageServiceFormRequest imageServiceFormRequest = ListUtil.findValue(imageryFormRequest.getImageServiceRequests(), new ListUtil.CheckValue<ImageServiceFormRequest>() {
                @Override
                public boolean isValue(ImageServiceFormRequest value) {
                    return value.getImageService().getCompany().getId().longValue() == user.getCompany().getId().longValue();
                }
            });
            if(imageServiceFormRequest == null) {
                throw new RequestException("Could not find supplier service request");
            }
            imageryServiceRequestDTO.setSupplierResponse(imageServiceFormRequest.getResponse());
            imageryServiceRequestDTO.setMessages(MessageHelper.convertToDTO(imageServiceFormRequest.getMessages()));
            return imageryServiceRequestDTO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException("Error");
        } finally {
            em.close();
        }
    }

    @Override
    public ImagesRequestDTO getImagesRequest(String id) throws RequestException {
        String logUserName = UserUtils.verifyUserSupplier(request);
        if(id == null) {
            throw new RequestException("Id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            final User user = em.find(User.class, logUserName);
            ImageryRequest imageryRequest = em.find(ImageryRequest.class, id);
            if(imageryRequest == null) {
                throw new RequestException("Unknown imagery request");
            }
            ImagesRequestDTO imagesRequestDTO = new ImagesRequestDTO();
            imagesRequestDTO.setId(imageryRequest.getId());
            imagesRequestDTO.setAoiWKT(imageryRequest.getAoiWKT());
            //imagesRequestDTO.setProducts(imageryRequest.getProductRequests());
            imagesRequestDTO.setCreationDate(imageryRequest.getCreationDate());
            imagesRequestDTO.setCustomer(UserHelper.createUserDTO(imageryRequest.getCustomer()));
            imagesRequestDTO.setResponse(imageryRequest.getResponse());
            imagesRequestDTO.setMessages(MessageHelper.convertToDTO(imageryRequest.getMessages()));
            return imagesRequestDTO;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException("Error");
        } finally {
            em.close();
        }
    }

    @Override
    public List<RequestDTO> getRequests() throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            List<RequestDTO> requestDTOs = new ArrayList<RequestDTO>();
            // get user
            User user = em.find(User.class, userName);
            Company company = user.getCompany();
            {
                TypedQuery<ImageryRequest> query = em.createQuery("select i from ImageryRequest i where i.imageService.company = :company", ImageryRequest.class);
                query.setParameter("company", company);
                List<ImageryRequest> requests = query.getResultList();
                requestDTOs.addAll(ListUtil.mutate(requests, new ListUtil.Mutate<ImageryRequest, RequestDTO>() {
                    @Override
                    public RequestDTO mutate(ImageryRequest imageryRequest) {
                        return createRequestDTO(imageryRequest);
                    }
                }));
            }
            {
                TypedQuery<ImageServiceFormRequest> imageryFormQuery = em.createQuery("select i from ImageServiceFormRequest i where i.imageService.company = :company", ImageServiceFormRequest.class);
                imageryFormQuery.setParameter("company", company);
                List<ImageServiceFormRequest> imageServiceFormRequests = imageryFormQuery.getResultList();
                requestDTOs.addAll(ListUtil.mutate(imageServiceFormRequests, new ListUtil.Mutate<ImageServiceFormRequest, RequestDTO>() {
                    @Override
                    public RequestDTO mutate(ImageServiceFormRequest imageServiceFormRequest) {
                        return createRequestDTO(imageServiceFormRequest.getImageryFormRequest());
                    }
                }));
            }
            {
                TypedQuery<ProductServiceSupplierRequest> productFormQuery = em.createQuery("select p from ProductServiceSupplierRequest p where p.productService.company = :company", ProductServiceSupplierRequest.class);
                productFormQuery.setParameter("company", company);
                List<ProductServiceSupplierRequest> productServiceRequests = productFormQuery.getResultList();
                requestDTOs.addAll(ListUtil.mutate(productServiceRequests, new ListUtil.Mutate<ProductServiceSupplierRequest, RequestDTO>() {
                    @Override
                    public RequestDTO mutate(ProductServiceSupplierRequest productServiceRequest) {
                        return createRequestDTO(productServiceRequest.getProductServiceRequest());
                    }
                }));
            }
            return requestDTOs;
        } catch (Exception e) {
            throw new RequestException("Issue when accessing list of requests");
        }
    }

    private RequestDTO createRequestDTO(ImageryFormRequest imageryFormRequest) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(imageryFormRequest.getId());
        requestDTO.setType(RequestDTO.TYPE.imageservice);
        requestDTO.setDescription("User '" + imageryFormRequest.getCustomer().getUsername() + "' - Form request for imagery service");
        return requestDTO;
    }

    private RequestDTO createRequestDTO(ImageryRequest imageryRequest) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(imageryRequest.getId());
        requestDTO.setType(RequestDTO.TYPE.image);
        requestDTO.setDescription("User '" + imageryRequest.getCustomer().getUsername() + "' - Request for " + imageryRequest.getProductRequests().size() + " products");
        return requestDTO;
    }

    private RequestDTO createRequestDTO(ProductServiceRequest productServiceRequest) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(productServiceRequest.getId());
        requestDTO.setType(RequestDTO.TYPE.product);
        requestDTO.setDescription("User '" + productServiceRequest.getCustomer().getUsername() + "' - Request for product '" + productServiceRequest.getProduct().getName() + "'");
        return requestDTO;
    }

}
