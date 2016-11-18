package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.Utils.KeyGenerator;
import com.geocento.webapps.eobroker.common.server.Utils.NotificationHelper;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;
import com.geocento.webapps.eobroker.common.shared.entities.orders.*;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;
import com.geocento.webapps.eobroker.customer.shared.utils.ProductHelper;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.services.OrdersService;
import com.geocento.webapps.eobroker.customer.server.utils.UserUtils;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.geocento.webapps.eobroker.customer.shared.requests.*;
import com.geocento.webapps.eobroker.customer.shared.utils.MessageHelper;
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

    static Logger logger = Logger.getLogger(AssetsResource.class);

    static KeyGenerator keyGenerator = new KeyGenerator(16);

    // assumes service is not a singleton
    @Context
    HttpServletRequest request;

    public OrdersResource() {
        logger.info("Starting order service");
    }

    @Override
    public List<RequestDTO> getRequests() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            List<RequestDTO> requestDTOs = new ArrayList<RequestDTO>();
            // get user
            User user = em.find(User.class, userName);
            {
                TypedQuery<ImagesRequest> query = em.createQuery("select i from ImagesRequest i where i.customer = :user", ImagesRequest.class);
                query.setParameter("user", user);
                List<ImagesRequest> requests = query.getResultList();
                requestDTOs.addAll(ListUtil.mutate(requests, new ListUtil.Mutate<ImagesRequest, RequestDTO>() {
                    @Override
                    public RequestDTO mutate(ImagesRequest imagesRequest) {
                        return createRequestDTO(imagesRequest);
                    }
                }));
            }
            {
                TypedQuery<ImageryFormRequest> imageryFormQuery = em.createQuery("select i from ImageryFormRequest i where i.customer = :user", ImageryFormRequest.class);
                imageryFormQuery.setParameter("user", user);
                List<ImageryFormRequest> imageryFormRequests = imageryFormQuery.getResultList();
                requestDTOs.addAll(ListUtil.mutate(imageryFormRequests, new ListUtil.Mutate<ImageryFormRequest, RequestDTO>() {
                    @Override
                    public RequestDTO mutate(ImageryFormRequest imageryFormRequest) {
                        return createRequestDTO(imageryFormRequest);
                    }
                }));
            }
            {
                TypedQuery<ProductServiceRequest> productFormQuery = em.createQuery("select p from ProductServiceRequest p where p.customer = :user", ProductServiceRequest.class);
                productFormQuery.setParameter("user", user);
                List<ProductServiceRequest> productServiceRequests = productFormQuery.getResultList();
                requestDTOs.addAll(ListUtil.mutate(productServiceRequests, new ListUtil.Mutate<ProductServiceRequest, RequestDTO>() {
                    @Override
                    public RequestDTO mutate(ProductServiceRequest productServiceRequest) {
                        return createRequestDTO(productServiceRequest);
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
        boolean severalCompanies = imageryFormRequest.getImageServiceRequests().size() > 1;
        requestDTO.setDescription("Form request for imagery service - " +
            (severalCompanies ? (" (" + imageryFormRequest.getImageServiceRequests().size() + " companies)") :
                    " (company '" + imageryFormRequest.getImageServiceRequests().get(0).getImageService().getCompany().getName() + "')")
        );
        requestDTO.setCreationTime(imageryFormRequest.getCreationDate());
        return requestDTO;
    }

    private RequestDTO createRequestDTO(ImagesRequest imagesRequest) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(imagesRequest.getId());
        requestDTO.setType(RequestDTO.TYPE.image);
        requestDTO.setDescription("Request for " + imagesRequest.getProductRequests().size() + " products (company '" + imagesRequest.getImageService().getCompany().getName() + "')");
        requestDTO.setCreationTime(imagesRequest.getCreationDate());
        return requestDTO;
    }

    private RequestDTO createRequestDTO(ProductServiceRequest productServiceRequest) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(productServiceRequest.getId());
        requestDTO.setType(RequestDTO.TYPE.product);
        boolean severalCompanies = productServiceRequest.getSupplierRequests().size() > 1;
        requestDTO.setDescription("Request for product '" + productServiceRequest.getProduct().getName() + "'" +
                (severalCompanies ? (" (" + productServiceRequest.getSupplierRequests().size() + " companies)") :
                        " (company '" + productServiceRequest.getSupplierRequests().get(0).getProductService().getCompany().getName() + "')")
        );
        requestDTO.setCreationTime(productServiceRequest.getCreationDate());
        return requestDTO;
    }

    @Override
    public RequestDTO submitImageRequest(ImageRequestDTO imageRequestDTO) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(imageRequestDTO == null) {
            throw new RequestException("Image request cannot be null");
        }
        String aoiWKT = imageRequestDTO.getAoiWKT();
        String imageType = imageRequestDTO.getImageType();
        Date start = imageRequestDTO.getStart();
        Date stop = imageRequestDTO.getStop();
        String additionalInformation = imageRequestDTO.getAdditionalInformation();
        if(aoiWKT == null || imageType == null || start == null || stop == null) {
            throw new RequestException("Missing parameter values in request");
        }
        final List<Long> serviceIds = imageRequestDTO.getImageServiceIds();
        if(serviceIds == null || serviceIds.size() == 0) {
            throw new RequestException("At least one supplier service id is required");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            // get user
            User user = em.find(User.class, userName);
            // find product
            TypedQuery<ImageService> query = em.createQuery("select i from ImageService i where i.id IN :serviceIds", ImageService.class);
            query.setParameter("serviceIds", serviceIds);
            List<ImageService> imageServices = query.getResultList();

            em.getTransaction().begin();
            // create image request
            ImageryFormRequest imageryFormRequest = new ImageryFormRequest();
            imageryFormRequest.setId(keyGenerator.CreateKey());
            imageryFormRequest.setCustomer(user);
            imageryFormRequest.setAoiWKT(aoiWKT);
            imageryFormRequest.setStart(start);
            imageryFormRequest.setStop(stop);
            imageryFormRequest.setImageType(imageType);
            imageryFormRequest.setApplication(imageRequestDTO.getApplication());
            imageryFormRequest.setAdditionalInformation(additionalInformation);
            imageryFormRequest.setImageServiceRequests(new ArrayList<ImageryFormSupplierRequest>());
            imageryFormRequest.setCreationDate(new Date());
            // store the request
            em.persist(imageryFormRequest);
            for(ImageService imageService : imageServices) {
                ImageryFormSupplierRequest imageryFormSupplierRequest = new ImageryFormSupplierRequest();
                imageryFormSupplierRequest.setImageService(imageService);
                imageryFormSupplierRequest.setImageryFormRequest(imageryFormRequest);
                imageryFormRequest.getImageServiceRequests().add(imageryFormSupplierRequest);
                imageryFormRequest.setCreationDate(new Date());
                em.persist(imageryFormSupplierRequest);
                // notify the supplier
                NotificationHelper.notifySupplier(em, imageService.getCompany(), SupplierNotification.TYPE.IMAGESERVICEREQUEST,
                        "New imagery request for service '" + imageService.getName() + "' from user '" + user.getUsername() + "'",
                        imageryFormRequest.getId());
            }
            em.getTransaction().commit();
            return createRequestDTO(imageryFormRequest);
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException("Could not submit image quotation request, server error");
        } finally {
            em.close();
        }
    }

    @Override
    public RequestDTO submitProductRequest(ProductServiceRequestDTO productServiceRequestDTO) throws RequestException {
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
            productServiceRequest.setAoIWKT(productServiceRequestDTO.getAoIWKT());
            productServiceRequest.setFormValues(values);
            productServiceRequest.setCreationDate(new Date());
            productServiceRequest.setSupplierRequests(new ArrayList<ProductServiceSupplierRequest>());
            em.persist(productServiceRequest);
            for (ProductService productService : productServices) {
                ProductServiceSupplierRequest productServiceSupplierRequest = new ProductServiceSupplierRequest();
                productServiceSupplierRequest.setProductService(productService);
                productServiceSupplierRequest.setProductServiceRequest(productServiceRequest);
                productServiceRequest.getSupplierRequests().add(productServiceSupplierRequest);
                em.persist(productServiceSupplierRequest);
                // notify the supplier
                NotificationHelper.notifySupplier(em, productService.getCompany(), SupplierNotification.TYPE.PRODUCTREQUEST, "New request for quotation for service '" + productService.getName() + "' from user '" + user.getUsername() + "'", productServiceRequest.getId());
            }
            em.getTransaction().commit();
            return createRequestDTO(productServiceRequest);
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

    @Override
    public RequestDTO submitImagesRequest(ImagesRequestDTO imagesRequestDTO) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(imagesRequestDTO == null) {
            throw new RequestException("Images request cannot be empty");
        }
        Long imageServiceId = imagesRequestDTO.getImageServiceId();
        String aoiWKT = imagesRequestDTO.getAoiWKT();
        List<com.geocento.webapps.eobroker.common.shared.imageapi.Product> products = imagesRequestDTO.getProducts();
        if(imageServiceId == null || aoiWKT == null || products == null || products.size() == 0) {
            throw new RequestException("Missing parameters");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            // get user
            User user = em.find(User.class, userName);
            ImagesRequest imagesRequest = new ImagesRequest();
            imagesRequest.setId(keyGenerator.CreateKey());
            imagesRequest.setCustomer(user);
            imagesRequest.setCreationDate(new Date());
            ImageService imageService = em.find(ImageService.class, imageServiceId);
            if(imageService == null) {
                throw new RequestException("Could not find matching service");
            }
            imagesRequest.setImageService(imageService);
            imagesRequest.setAoiWKT(imagesRequestDTO.getAoiWKT());
            imagesRequest.setProductRequests(ListUtil.mutate(products, new ListUtil.Mutate<com.geocento.webapps.eobroker.common.shared.imageapi.Product, ImageProductEntity>() {
                @Override
                public ImageProductEntity mutate(com.geocento.webapps.eobroker.common.shared.imageapi.Product product) {
                    ImageProductEntity imageProductEntity = new ImageProductEntity();
                    imageProductEntity.setProductId(product.getProductId());
                    imageProductEntity.setType(product.getType());
                    imageProductEntity.setCoordinatesWKT(product.getCoordinatesWKT());
                    imageProductEntity.setSatelliteName(product.getSatelliteName());
                    imageProductEntity.setInstrumentName(product.getInstrumentName());
                    imageProductEntity.setModeName(product.getModeName());
                    imageProductEntity.setStart(product.getStart());
                    imageProductEntity.setStop(product.getStop());
                    imageProductEntity.setOrbitDirection(product.getOrbitDirection() == null ?
                            "none" : product.getOrbitDirection().toString());
                    return imageProductEntity;
                }
            }));
            em.getTransaction().begin();
            em.persist(imagesRequest);
            // notify the supplier
            NotificationHelper.notifySupplier(em, imageService.getCompany(), SupplierNotification.TYPE.IMAGEREQUEST, "New request for quotation for imagery from user '" + user.getUsername() + "'", imagesRequest.getId());
            em.getTransaction().commit();
            return createRequestDTO(imagesRequest);
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

    @Override
    public ProductServiceResponseDTO getProductResponse(String id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Request id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductServiceRequest productServiceRequest = em.find(ProductServiceRequest.class, id);
            if(!productServiceRequest.getCustomer().getUsername().contentEquals(userName)) {
                throw new RequestException("Not allowed");
            }
            ProductServiceResponseDTO productServiceResponseDTO = new ProductServiceResponseDTO();
            productServiceResponseDTO.setId(productServiceRequest.getId());
            productServiceResponseDTO.setAoIWKT(productServiceRequest.getAoIWKT());
            productServiceResponseDTO.setFormValues(productServiceRequest.getFormValues());
            productServiceResponseDTO.setProduct(ProductHelper.createProductDTO(productServiceRequest.getProduct()));
            productServiceResponseDTO.setSupplierResponses(ListUtil.mutate(productServiceRequest.getSupplierRequests(), new ListUtil.Mutate<ProductServiceSupplierRequest, ProductServiceSupplierResponseDTO>() {
                @Override
                public ProductServiceSupplierResponseDTO mutate(ProductServiceSupplierRequest productServiceSupplierRequest) {
                    ProductServiceSupplierResponseDTO productServiceSupplierResponseDTO = new ProductServiceSupplierResponseDTO();
                    productServiceSupplierResponseDTO.setId(productServiceSupplierRequest.getId());
                    productServiceSupplierResponseDTO.setCompany(CompanyHelper.createCompanyDTO(productServiceSupplierRequest.getProductService().getCompany()));
                    productServiceSupplierResponseDTO.setServiceName(productServiceSupplierRequest.getProductService().getName());
                    productServiceSupplierResponseDTO.setResponse(productServiceSupplierRequest.getResponse());
                    productServiceSupplierResponseDTO.setMessages(MessageHelper.convertToDTO(productServiceSupplierRequest.getMessages()));
                    productServiceSupplierResponseDTO.setResponseDate(productServiceSupplierRequest.getLastModifiedDate());
                    return productServiceSupplierResponseDTO;
                }
            }));
            return productServiceResponseDTO;
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Could not load product request, server error");
        } finally {
            em.close();
        }
    }

    @Override
    public ImageryResponseDTO getImageResponse(String id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Request id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            final ImageryFormRequest imageryFormRequest = em.find(ImageryFormRequest.class, id);
            if(!imageryFormRequest.getCustomer().getUsername().contentEquals(userName)) {
                throw new RequestException("Not allowed");
            }
            ImageryResponseDTO imageryResponseDTO = new ImageryResponseDTO();
            imageryResponseDTO.setId(imageryFormRequest.getId());
            imageryResponseDTO.setAoiWKT(imageryFormRequest.getAoiWKT());
            imageryResponseDTO.setImageType(imageryFormRequest.getImageType());
            imageryResponseDTO.setApplication(imageryFormRequest.getApplication());
            imageryResponseDTO.setStart(imageryFormRequest.getStart());
            imageryResponseDTO.setStop(imageryFormRequest.getStop());
            imageryResponseDTO.setAdditionalInformation(imageryFormRequest.getAdditionalInformation());
            imageryResponseDTO.setCreationDate(imageryFormRequest.getCreationDate());
            imageryResponseDTO.setSupplierResponses(ListUtil.mutate(imageryFormRequest.getImageServiceRequests(), new ListUtil.Mutate<ImageryFormSupplierRequest, ImagerySupplierResponseDTO>() {
                @Override
                public ImagerySupplierResponseDTO mutate(ImageryFormSupplierRequest imageryFormSupplierRequest) {
                    ImagerySupplierResponseDTO imagerySupplierResponseDTO = new ImagerySupplierResponseDTO();
                    imagerySupplierResponseDTO.setId(imageryFormSupplierRequest.getId());
                    imagerySupplierResponseDTO.setCompany(CompanyHelper.createCompanyDTO(imageryFormSupplierRequest.getImageService().getCompany()));
                    imagerySupplierResponseDTO.setServiceName(imageryFormSupplierRequest.getImageService().getName());
                    imagerySupplierResponseDTO.setResponse(imageryFormSupplierRequest.getResponse());
                    imagerySupplierResponseDTO.setMessages(MessageHelper.convertToDTO(imageryFormSupplierRequest.getMessages()));
                    imagerySupplierResponseDTO.setResponseDate(imageryFormSupplierRequest.getLastModifiedDate());
                    return imagerySupplierResponseDTO;
                }
            }));
            return imageryResponseDTO;
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Could not load imagery request, server error");
        } finally {
            em.close();
        }
    }

    @Override
    public ImagesServiceResponseDTO getImagesResponse(String id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Request id cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            final ImagesRequest imagesRequest = em.find(ImagesRequest.class, id);
            if(!imagesRequest.getCustomer().getUsername().contentEquals(userName)) {
                throw new RequestException("Not allowed");
            }
            ImagesServiceResponseDTO imagesServiceResponseDTO = new ImagesServiceResponseDTO();
            imagesServiceResponseDTO.setId(imagesRequest.getId());
            imagesServiceResponseDTO.setAoiWKT(imagesRequest.getAoiWKT());
            imagesServiceResponseDTO.setProducts(ListUtil.mutate(imagesRequest.getProductRequests(), new ListUtil.Mutate<ImageProductEntity, com.geocento.webapps.eobroker.common.shared.imageapi.Product>() {
                @Override
                public com.geocento.webapps.eobroker.common.shared.imageapi.Product mutate(ImageProductEntity imageProductEntity) {
                    com.geocento.webapps.eobroker.common.shared.imageapi.Product product = new com.geocento.webapps.eobroker.common.shared.imageapi.Product();
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
            imagesServiceResponseDTO.setCompany(CompanyHelper.createCompanyDTO(imagesRequest.getImageService().getCompany()));
            imagesServiceResponseDTO.setServiceName(imagesRequest.getImageService().getName());
            imagesServiceResponseDTO.setResponse(imagesRequest.getResponse());
            imagesServiceResponseDTO.setMessages(MessageHelper.convertToDTO(imagesRequest.getMessages()));
            imagesServiceResponseDTO.setResponseDate(imagesRequest.getLastModifiedDate());
            return imagesServiceResponseDTO;
        } catch (Exception e) {
            if(em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Could not load images request, server error");
        } finally {
            em.close();
        }
    }

    @Override
    public MessageDTO addProductResponseMessage(Long id, String text) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Request id cannot be null");
        }
        if(text == null || text.length() == 0) {
            throw new RequestException("No message provided");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            ProductServiceSupplierRequest productServiceSupplierRequest = em.find(ProductServiceSupplierRequest.class, id);
            if(productServiceSupplierRequest.getProductServiceRequest().getCustomer() != user) {
                throw new RequestException("Not allowed");
            }
            Message message = new Message();
            message.setFrom(user);
            message.setMessage(text);
            message.setCreationDate(new Date());
            em.persist(message);
            productServiceSupplierRequest.getMessages().add(message);
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

    @Override
    public MessageDTO addImageryResponseMessage(Long id, String text) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Request id cannot be null");
        }
        if(text == null || text.length() == 0) {
            throw new RequestException("No message provided");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            ImageryFormSupplierRequest imageryFormSupplierRequest = em.find(ImageryFormSupplierRequest.class, id);
            if(imageryFormSupplierRequest.getImageryFormRequest().getCustomer() != user) {
                throw new RequestException("Not allowed");
            }
            Message message = new Message();
            message.setFrom(user);
            message.setMessage(text);
            message.setCreationDate(new Date());
            em.persist(message);
            imageryFormSupplierRequest.getMessages().add(message);
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

    @Override
    public MessageDTO addImagesResponseMessage(String id, String text) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(id == null) {
            throw new RequestException("Request id cannot be null");
        }
        if(text == null || text.length() == 0) {
            throw new RequestException("No message provided");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            ImagesRequest imagesRequest = em.find(ImagesRequest.class, id);
            if(imagesRequest.getCustomer() != user) {
                throw new RequestException("Not allowed");
            }
            Message message = new Message();
            message.setFrom(user);
            message.setMessage(text);
            message.setCreationDate(new Date());
            em.persist(message);
            imagesRequest.getMessages().add(message);
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

    @Override
    public List<ConversationDTO> listConversations(Long companyId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<Conversation> query = em.createQuery("select c from Conversation c where c.company.id = :companyId and c.customer = :user order by c.creationDate desc", Conversation.class);
            query.setParameter("companyId", companyId);
            query.setParameter("user", user);
            query.setFirstResult(0);
            query.setMaxResults(10);
            return ListUtil.mutate(query.getResultList(), new ListUtil.Mutate<Conversation, ConversationDTO>() {
                @Override
                public ConversationDTO mutate(Conversation conversation) {
                    ConversationDTO conversationDTO = new ConversationDTO();
                    conversationDTO.setId(conversation.getId());
                    conversationDTO.setTopic(conversation.getTopic());
                    conversationDTO.setCreationDate(conversation.getCreationDate());
                    return conversationDTO;
                }
            });
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
    public ConversationDTO getConversation(String id) throws RequestException {
        String userName = UserUtils.verifyUser(request);
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
            if(conversation.getCustomer() != user) {
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
    public ConversationDTO createConversation(CreateConversationDTO conversationDTO) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(conversationDTO == null) {
            throw new RequestException("Conversation cannot be null");
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            Company company = em.find(Company.class, conversationDTO.getCompanyId());
            if(company == null) {
                throw new RequestException("Company does not exist");
            }
            Conversation conversation = new Conversation();
            conversation.setId(keyGenerator.CreateKey());
            conversation.setTopic(conversationDTO.getTopic());
            conversation.setCustomer(user);
            conversation.setCompany(company);
            conversation.setCreationDate(new Date());
            em.persist(conversation);
            NotificationHelper.notifySupplier(em, company, SupplierNotification.TYPE.MESSAGE, "User " + userName + " has started a new conversation", conversation.getId());
            em.getTransaction().commit();
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
        String userName = UserUtils.verifyUser(request);
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
            if(conversation.getCustomer() != user) {
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
        conversationDTO.setCompany(CompanyHelper.createCompanyDTO(conversation.getCompany()));
        conversationDTO.setTopic(conversation.getTopic());
        conversationDTO.setMessages(MessageHelper.convertToDTO(conversation.getMessages()));
        conversationDTO.setCreationDate(conversation.getCreationDate());
        return conversationDTO;
    }

}
