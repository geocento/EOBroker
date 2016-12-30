package com.geocento.webapps.eobroker.supplier.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.Utils.NotificationHelper;
import com.geocento.webapps.eobroker.common.shared.entities.Company;
import com.geocento.webapps.eobroker.common.shared.entities.Conversation;
import com.geocento.webapps.eobroker.common.shared.entities.Message;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.Notification;
import com.geocento.webapps.eobroker.common.shared.entities.requests.*;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.server.imageapi.EIAPIUtil;
import com.geocento.webapps.eobroker.supplier.shared.utils.ProductHelper;
import com.geocento.webapps.eobroker.supplier.client.services.OrdersService;
import com.geocento.webapps.eobroker.supplier.server.util.MessageHelper;
import com.geocento.webapps.eobroker.supplier.server.util.UserUtils;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ConversationDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ImagesRequestDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.MessageDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.UserHelper;
import com.google.gwt.http.client.RequestException;
import org.apache.log4j.Logger;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.util.ArrayList;
import java.util.Date;
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
            imageryServiceRequestDTO.setApplication(imageryFormRequest.getApplication());
            imageryServiceRequestDTO.setAdditionalInformation(imageryFormRequest.getAdditionalInformation());
            imageryServiceRequestDTO.setCreationDate(imageryFormRequest.getCreationDate());
            imageryServiceRequestDTO.setCustomer(UserHelper.createUserDTO(imageryFormRequest.getCustomer()));
            // get the supplier specific request
            ImageryFormSupplierRequest imageryFormSupplierRequest = ListUtil.findValue(imageryFormRequest.getImageServiceRequests(), new ListUtil.CheckValue<ImageryFormSupplierRequest>() {
                @Override
                public boolean isValue(ImageryFormSupplierRequest value) {
                    return value.getImageService().getCompany().getId().longValue() == user.getCompany().getId().longValue();
                }
            });
            if(imageryFormSupplierRequest == null) {
                throw new RequestException("Could not find supplier service request");
            }
            imageryServiceRequestDTO.setSupplierResponse(imageryFormSupplierRequest.getResponse());
            imageryServiceRequestDTO.setMessages(MessageHelper.convertToDTO(imageryFormSupplierRequest.getMessages()));
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
            ImagesRequest imagesRequest = em.find(ImagesRequest.class, id);
            if(imagesRequest == null) {
                throw new RequestException("Unknown imagery request");
            }
            ImagesRequestDTO imagesRequestDTO = new ImagesRequestDTO();
            imagesRequestDTO.setId(imagesRequest.getId());
            imagesRequestDTO.setAoiWKT(imagesRequest.getAoiWKT());
            imagesRequestDTO.setProducts(ListUtil.mutate(imagesRequest.getProductRequests(), new ListUtil.Mutate<ImageProductEntity, Product>() {
                @Override
                public Product mutate(ImageProductEntity imageProductEntity) {
                    Product product = new Product();
                    product.setProductId(imageProductEntity.getProductId());
                    product.setType(imageProductEntity.getType());
                    product.setCoordinatesWKT(imageProductEntity.getCoordinatesWKT());
                    product.setSatelliteName(imageProductEntity.getSatelliteName());
                    product.setInstrumentName(imageProductEntity.getInstrumentName());
                    product.setModeName(imageProductEntity.getModeName());
                    product.setStart(imageProductEntity.getStart());
                    product.setStop(imageProductEntity.getStop());
                    return product;
                }
            }));
            String productIds = ListUtil.toString(imagesRequest.getProductRequests(), new ListUtil.GetLabel<ImageProductEntity>() {
                @Override
                public String getLabel(ImageProductEntity imageProductEntity) {
                    return imageProductEntity.getType() == Product.TYPE.ARCHIVE ? imageProductEntity.getProductId() : "";
                }
            }, ",");
            try {
                List<Product> products = EIAPIUtil.queryProductsById(productIds);
                for (final Product product : imagesRequestDTO.getProducts()) {
                    Product matchingProduct = ListUtil.findValue(products, new ListUtil.CheckValue<Product>() {
                        @Override
                        public boolean isValue(Product value) {
                            return value.getProductId().contentEquals(product.getProductId());
                        }
                    });
                    if (matchingProduct != null) {
                        // fill in the missing fields
                        product.setQl(matchingProduct.getQl());
                        product.setThumbnail(matchingProduct.getThumbnail());
                    }
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            imagesRequestDTO.setCreationDate(imagesRequest.getCreationDate());
            imagesRequestDTO.setCustomer(UserHelper.createUserDTO(imagesRequest.getCustomer()));
            imagesRequestDTO.setResponse(imagesRequest.getResponse());
            imagesRequestDTO.setMessages(MessageHelper.convertToDTO(imagesRequest.getMessages()));
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
                TypedQuery<ImagesRequest> query = em.createQuery("select i from ImagesRequest i where i.imageService.company = :company", ImagesRequest.class);
                query.setParameter("company", company);
                List<ImagesRequest> requests = query.getResultList();
                requestDTOs.addAll(ListUtil.mutate(requests, new ListUtil.Mutate<ImagesRequest, RequestDTO>() {
                    @Override
                    public RequestDTO mutate(ImagesRequest imagesRequest) {
                        return createRequestDTO(imagesRequest);
                    }
                }));
            }
            {
                TypedQuery<ImageryFormSupplierRequest> imageryFormQuery = em.createQuery("select i from ImageryFormSupplierRequest i where i.imageService.company = :company", ImageryFormSupplierRequest.class);
                imageryFormQuery.setParameter("company", company);
                List<ImageryFormSupplierRequest> imageryFormSupplierRequests = imageryFormQuery.getResultList();
                requestDTOs.addAll(ListUtil.mutate(imageryFormSupplierRequests, new ListUtil.Mutate<ImageryFormSupplierRequest, RequestDTO>() {
                    @Override
                    public RequestDTO mutate(ImageryFormSupplierRequest imageryFormSupplierRequest) {
                        return createRequestDTO(imageryFormSupplierRequest.getImageryFormRequest());
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

    @Override
    public void submitRequestResponse(RequestDTO.TYPE type, String id, String response) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            final User user = em.find(User.class, userName);
            switch (type) {
                case image: {
                    ImagesRequest imagesRequest = em.find(ImagesRequest.class, id);
                    if (imagesRequest == null) {
                        throw new RequestException("No imagery request with id " + id);
                    }
                    imagesRequest.setResponse(response);
                    NotificationHelper.notifyCustomer(em, user, Notification.TYPE.IMAGEREQUEST, "New response from " + user.getCompany().getName() + " on your images request", imagesRequest.getId());
                } break;
                case imageservice: {
                    ImageryFormRequest imageryFormRequest = em.find(ImageryFormRequest.class, id);
                    if (imageryFormRequest == null) {
                        throw new RequestException("No images request with id " + id);
                    }
                    ImageryFormSupplierRequest imageryFormSupplierRequest = ListUtil.findValue(imageryFormRequest.getImageServiceRequests(), new ListUtil.CheckValue<ImageryFormSupplierRequest>() {
                        @Override
                        public boolean isValue(ImageryFormSupplierRequest value) {
                            return value.getImageService().getCompany() == user.getCompany();
                        }
                    });
                    imageryFormSupplierRequest.setResponse(response);
                    NotificationHelper.notifyCustomer(em, user, Notification.TYPE.IMAGESERVICEREQUEST, "New response from " + user.getCompany().getName() + " on your imagery request", imageryFormRequest.getId());
                } break;
                case product: {
                    ProductServiceRequest productServiceRequest = em.find(ProductServiceRequest.class, id);
                    if (productServiceRequest == null) {
                        throw new RequestException("No product request with id " + id);
                    }
                    ProductServiceSupplierRequest productServiceSupplierRequest = ListUtil.findValue(productServiceRequest.getSupplierRequests(), new ListUtil.CheckValue<ProductServiceSupplierRequest>() {
                        @Override
                        public boolean isValue(ProductServiceSupplierRequest value) {
                            return value.getProductService().getCompany() == user.getCompany();
                        }
                    });
                    productServiceSupplierRequest.setResponse(response);
                    NotificationHelper.notifyCustomer(em, user, Notification.TYPE.PRODUCTREQUEST, "New response from " + user.getCompany().getName() + " on your product request", productServiceRequest.getId());
                } break;
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error");
        } finally {
            em.close();
        }
    }

    @Override
    public MessageDTO addRequestMessage(RequestDTO.TYPE type, String id, String text) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            final User user = em.find(User.class, userName);
            Message message = new Message();
            message.setFrom(user);
            message.setMessage(text);
            message.setCreationDate(new Date());
            em.persist(message);
            switch (type) {
                case image: {
                    ImagesRequest imagesRequest = em.find(ImagesRequest.class, id);
                    if (imagesRequest == null) {
                        throw new RequestException("No imagery request with id " + id);
                    }
                    imagesRequest.getMessages().add(message);
                } break;
                case imageservice: {
                    ImageryFormRequest imageryFormRequest = em.find(ImageryFormRequest.class, id);
                    if (imageryFormRequest == null) {
                        throw new RequestException("No images request with id " + id);
                    }
                    ImageryFormSupplierRequest imageryFormSupplierRequest = ListUtil.findValue(imageryFormRequest.getImageServiceRequests(), new ListUtil.CheckValue<ImageryFormSupplierRequest>() {
                        @Override
                        public boolean isValue(ImageryFormSupplierRequest value) {
                            return value.getImageService().getCompany() == user.getCompany();
                        }
                    });
                    imageryFormSupplierRequest.getMessages().add(message);
                } break;
                case product: {
                    ProductServiceRequest productServiceRequest = em.find(ProductServiceRequest.class, id);
                    if (productServiceRequest == null) {
                        throw new RequestException("No product request with id " + id);
                    }
                    ProductServiceSupplierRequest productServiceSupplierRequest = ListUtil.findValue(productServiceRequest.getSupplierRequests(), new ListUtil.CheckValue<ProductServiceSupplierRequest>() {
                        @Override
                        public boolean isValue(ProductServiceSupplierRequest value) {
                            return value.getProductService().getCompany() == user.getCompany();
                        }
                    });
                    productServiceSupplierRequest.getMessages().add(message);
                } break;
            }
            em.getTransaction().commit();
            return MessageHelper.convertToDTO(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Error");
        } finally {
            em.close();
        }
    }

    private RequestDTO createRequestDTO(ImageryFormRequest imageryFormRequest) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(imageryFormRequest.getId());
        requestDTO.setType(RequestDTO.TYPE.imageservice);
        requestDTO.setDescription("User '" + imageryFormRequest.getCustomer().getUsername() + "' - Form request for imagery service");
        requestDTO.setCreationTime(imageryFormRequest.getCreationDate());
        return requestDTO;
    }

    private RequestDTO createRequestDTO(ImagesRequest imagesRequest) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(imagesRequest.getId());
        requestDTO.setType(RequestDTO.TYPE.image);
        requestDTO.setDescription("User '" + imagesRequest.getCustomer().getUsername() + "' - Request for " + imagesRequest.getProductRequests().size() + " products");
        requestDTO.setCreationTime(imagesRequest.getCreationDate());
        return requestDTO;
    }

    private RequestDTO createRequestDTO(ProductServiceRequest productServiceRequest) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(productServiceRequest.getId());
        requestDTO.setType(RequestDTO.TYPE.product);
        requestDTO.setDescription("User '" + productServiceRequest.getCustomer().getUsername() + "' - Request for product '" + productServiceRequest.getProduct().getName() + "'");
        requestDTO.setCreationTime(productServiceRequest.getCreationDate());
        return requestDTO;
    }


    @Override
    public ConversationDTO getConversation(String id) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        if(id == null) {
            throw new RequestException("Conversation id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            Conversation conversation = em.find(Conversation.class, id);
            if(conversation == null) {
                throw new RequestException("No conversation with this id");
            }
            if(conversation.getCompany() != user.getCompany()) {
                throw new RequestException("Not allowed");
            }
            return createConversationDTO(conversation);
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Could not add message to request, server error");
        } finally {
            em.close();
        }
    }

    @Override
    public MessageDTO addConversationMessage(String id, String text) throws RequestException {
        String userName = UserUtils.verifyUserSupplier(request);
        if(id == null) {
            throw new RequestException("Conversation id cannot be null");
        }
        if(text == null || text.length() == 0) {
            throw new RequestException("No message provided");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Conversation conversation = em.find(Conversation.class, id);
            if(conversation == null) {
                throw new RequestException("No conversation with this id");
            }
            if(conversation.getCompany() != user.getCompany()) {
                throw new RequestException("Not allowed");
            }
            Message message = new Message();
            message.setFrom(user);
            message.setMessage(text);
            message.setCreationDate(new Date());
            em.persist(message);
            conversation.getMessages().add(message);
            em.getTransaction().commit();
            return MessageHelper.convertToDTO(message);
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Could not add message to request, server error");
        } finally {
            em.close();
        }
    }

    private ConversationDTO createConversationDTO(Conversation conversation) {
        ConversationDTO conversationDTO = new ConversationDTO();
        conversationDTO.setId(conversation.getId());
        conversationDTO.setUser(UserHelper.createUserDTO(conversation.getCustomer()));
        conversationDTO.setTopic(conversation.getTopic());
        conversationDTO.setMessages(MessageHelper.convertToDTO(conversation.getMessages()));
        conversationDTO.setCreationDate(conversation.getCreationDate());
        return conversationDTO;
    }

}
