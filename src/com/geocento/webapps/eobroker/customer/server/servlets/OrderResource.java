package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.Utils.KeyGenerator;
import com.geocento.webapps.eobroker.common.server.Utils.NotificationHelper;
import com.geocento.webapps.eobroker.common.shared.entities.ImageService;
import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.ProductService;
import com.geocento.webapps.eobroker.common.shared.entities.User;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;
import com.geocento.webapps.eobroker.common.shared.entities.orders.*;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.services.OrderService;
import com.geocento.webapps.eobroker.customer.server.utils.UserUtils;
import com.geocento.webapps.eobroker.customer.shared.ImageRequestDTO;
import com.geocento.webapps.eobroker.customer.shared.ImagesRequestDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceRequestDTO;
import com.geocento.webapps.eobroker.customer.shared.RequestDTO;
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
    public List<RequestDTO> getRequests() throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        try {
            List<RequestDTO> requestDTOs = new ArrayList<RequestDTO>();
            // get user
            User user = em.find(User.class, userName);
            {
                TypedQuery<ImageryRequest> query = em.createQuery("select i from ImageryRequest i where i.customer = :user", ImageryRequest.class);
                query.setParameter("user", user);
                List<ImageryRequest> requests = query.getResultList();
                requestDTOs.addAll(ListUtil.mutate(requests, new ListUtil.Mutate<ImageryRequest, RequestDTO>() {
                    @Override
                    public RequestDTO mutate(ImageryRequest imageryRequest) {
                        return createRequestDTO(imageryRequest);
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
        requestDTO.setDescription("Form request for imagery service");
        return requestDTO;
    }

    private RequestDTO createRequestDTO(ImageryRequest imageryRequest) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(imageryRequest.getId());
        requestDTO.setType(RequestDTO.TYPE.image);
        requestDTO.setDescription("Request for " + imageryRequest.getProductRequests().size() + " products");
        return requestDTO;
    }

    private RequestDTO createRequestDTO(ProductServiceRequest productServiceRequest) {
        RequestDTO requestDTO = new RequestDTO();
        requestDTO.setId(productServiceRequest.getId());
        requestDTO.setType(RequestDTO.TYPE.product);
        requestDTO.setDescription("Request for product '" + productServiceRequest.getProduct().getName() + "'");
        return requestDTO;
    }

    @Override
    public RequestDTO getRequest(String id) {
        return null;
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
            imageryFormRequest.setAdditionalInformation(additionalInformation);
            imageryFormRequest.setImageServiceRequests(new ArrayList<ImageServiceFormRequest>());
            // store the request
            em.persist(imageryFormRequest);
            for(ImageService imageService : imageServices) {
                ImageServiceFormRequest imageServiceFormRequest = new ImageServiceFormRequest();
                imageServiceFormRequest.setImageService(imageService);
                imageServiceFormRequest.setImageryFormRequest(imageryFormRequest);
                imageryFormRequest.getImageServiceRequests().add(imageServiceFormRequest);
                em.persist(imageServiceFormRequest);
                // notify the supplier
                NotificationHelper.notifySupplier(em, imageService.getCompany(), SupplierNotification.TYPE.REQUEST,
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
            ImageryRequest imageryRequest = new ImageryRequest();
            imageryRequest.setId(keyGenerator.CreateKey());
            imageryRequest.setCustomer(user);
            ImageService imageService = em.find(ImageService.class, imageServiceId);
            if(imageService == null) {
                throw new RequestException("Could not find matching service");
            }
            imageryRequest.setImageService(imageService);
            imageryRequest.setAoiWKT(imagesRequestDTO.getAoiWKT());
            imageryRequest.setProductRequests(ListUtil.mutate(products, new ListUtil.Mutate<com.geocento.webapps.eobroker.common.shared.imageapi.Product, ImageProductEntity>() {
                @Override
                public ImageProductEntity mutate(com.geocento.webapps.eobroker.common.shared.imageapi.Product product) {
                    ImageProductEntity imageProductEntity = new ImageProductEntity();
                    imageProductEntity.setType(product.getType());
                    imageProductEntity.setCoordinatesWKT(product.getCoordinatesWKT());
                    imageProductEntity.setSatelliteName(product.getSatelliteName());
                    imageProductEntity.setInstrumentName(product.getInstrumentName());
                    imageProductEntity.setModeName(product.getModeName());
                    return imageProductEntity;
                }
            }));
            em.getTransaction().begin();
            em.persist(imageryRequest);
            // notify the supplier
            NotificationHelper.notifySupplier(em, imageService.getCompany(), SupplierNotification.TYPE.REQUEST, "New request for quotation for imagery from user '" + user.getUsername() + "'", imageryRequest.getId());
            em.getTransaction().commit();
            return createRequestDTO(imageryRequest);
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
