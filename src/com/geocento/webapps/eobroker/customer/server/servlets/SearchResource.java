package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.feasibility.FeasibilityResponse;
import com.geocento.webapps.eobroker.common.shared.entities.feasibility.SupplierAPIResponse;
import com.geocento.webapps.eobroker.customer.shared.FeasibilityRequestDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.utils.ProductHelper;
import com.geocento.webapps.eobroker.common.shared.imageapi.ImageProductDTO;
import com.geocento.webapps.eobroker.common.shared.imageapi.SearchRequest;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import com.geocento.webapps.eobroker.customer.client.services.SearchService;
import com.geocento.webapps.eobroker.customer.server.imageapi.EIAPIUtil;
import com.google.gson.Gson;
import com.google.gwt.http.client.RequestException;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.Path;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Path("/")
public class SearchResource implements SearchService {

    Logger logger = Logger.getLogger(AssetsResource.class);

    public SearchResource() {
        logger.info("Starting search service");
    }

    private static class RankedSuggestion extends Suggestion {

        double rank;

        public RankedSuggestion(String name, Category category, String uri, double rank) {
            super(name, category, uri);
            this.rank = rank;
        }

        public double getRank() {
            return rank;
        }

        public void setRank(double rank) {
            this.rank = rank;
        }
    }

    @Override
    public List<Suggestion> complete(String text, Category category, String aoi) {
        ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
        // check if last character is a space
        boolean partialMatch = !text.endsWith(" ");
        text.trim();
        // break down text into sub words
        String[] words = text.split(" ");
        String keywords = StringUtils.join(words, " | ");
        if(partialMatch) {
            keywords += ":*";
        }
        // make sure words together get better ranking than when they are far apart
        // optimise based on the ratio of matching words and the total number of words of the search phrase
        // prioritise based on matching word position, ie if starts the same give higher priority
        // analyse words and look for common words such as image, product, theme
        ArrayList<Category> categories = new ArrayList<Category>();
        if(category != null) {
            categories.add(category);
        } else {
            categories.addAll(Arrays.asList(Category.values()));
        }
        ArrayList<RankedSuggestion> rankedSuggestions = new ArrayList<RankedSuggestion>();
        for(Category searchCategory : categories) {
            switch(searchCategory) {
                case products:
                    rankedSuggestions.addAll(completeProducts(keywords, aoi));
                    break;
            }
        }
        // now sort and filter
        Collections.sort(rankedSuggestions, new Comparator<RankedSuggestion>() {
            @Override
            public int compare(RankedSuggestion o1, RankedSuggestion o2) {
                return new Double(o2.rank).compareTo(new Double(o1.rank));
            }
        });
        for(RankedSuggestion rankedSuggestion : rankedSuggestions.subList(0, Math.min(rankedSuggestions.size(), 10))) {
            suggestions.add(new Suggestion(rankedSuggestion.getName(), rankedSuggestion.getCategory(), rankedSuggestion.getUri()));
        }
        return suggestions;
    }

    private List<RankedSuggestion> completeProducts(String keywords, String aoiWKT) {
        // change the last word so that it allows for partial match
        String sqlStatement = "SELECT \"name\", ts_rank(tsvname, keywords, 8) AS rank, id\n" +
                "          FROM product, to_tsquery('" + keywords + "') AS keywords\n" +
                "          WHERE tsvname @@ keywords\n" +
                "          ORDER BY rank DESC\n" +
                "          LIMIT 10;";
        EntityManager em = EMF.get().createEntityManager();
        Query q = em.createNativeQuery(sqlStatement);
        List<Object[]> results = q.getResultList();
        List<RankedSuggestion> suggestions = new ArrayList<RankedSuggestion>();
        for(Object[] result : results) {
            suggestions.add(new RankedSuggestion((String) result[0], Category.products, "product::" + ((Long) result[2]) + "", (double) ((float) result[1])));
        }
        return suggestions;
    }

    @Override
    public SearchResult getMatchingServices(String text, Category category, Long aoiId) throws RequestException {
        SearchResult searchResult = new SearchResult();
        // check if last character is a space
        boolean partialMatch = !text.endsWith(" ");
        text.trim();
        // break down text into sub words
        String[] words = text.split(" ");
        String keywords = StringUtils.join(words, " | ");
        if(partialMatch) {
            keywords += ":*";
        }
        // change the last word so that it allows for partial match
        String sqlStatement = "SELECT id, ts_rank(tsvname, keywords, 8) AS rank\n" +
                "          FROM product, to_tsquery('" + keywords + "') AS keywords\n" +
                "          WHERE tsvname @@ keywords\n" +
                "          ORDER BY rank DESC\n" +
                "          LIMIT 10;";
        EntityManager em = EMF.get().createEntityManager();
        try {
            Query q = em.createNativeQuery(sqlStatement);
            List<Object[]> results = q.getResultList();
            List<Long> productIds = new ArrayList<Long>();
            for (Object[] result : results) {
                productIds.add((Long) result[0]);
            }
            TypedQuery<Product> productQuery = em.createQuery("select p from Product p where p.id IN :productIds", Product.class);
            productQuery.setParameter("productIds", productIds);
            List<Product> products = productQuery.getResultList();
            List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
            List<ProductServiceDTO> productServices = new ArrayList<ProductServiceDTO>();
            for (Product product : products) {
                ProductDTO productDTO = ProductHelper.createProductDTO(product);
                productDTOs.add(productDTO);
                for (ProductService productService : product.getProductServices()) {
                    ProductServiceDTO productServiceDTO = new ProductServiceDTO();
                    productServiceDTO.setId(productService.getId());
                    productServiceDTO.setName(productService.getName());
                    productServiceDTO.setDescription(productService.getDescription());
                    productServiceDTO.setServiceImage(productService.getImageUrl());
                    productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
                    productServiceDTO.setCompanyName(productService.getCompany().getName());
                    productServiceDTO.setCompanyId(productService.getCompany().getId());
                    productServiceDTO.setHasFeasibility(productService.getApiUrl() != null && productService.getApiUrl().length() > 0);
                    productServiceDTO.setProduct(productDTO);
                    productServices.addAll(ListUtil.toList(new ProductServiceDTO[]{
                            productServiceDTO
                    }));
                }
            }
            searchResult.setProducts(productDTOs);
            searchResult.setProductServices(productServices);
            // now get the product suppliers for each one of them

            return searchResult;
        } finally {
            em.close();
        }
    }

    @Override
    public SearchResult getMatchingServicesForProduct(Long productId, Long aoiId) throws RequestException {
        SearchResult searchResult = new SearchResult();
        EntityManager em = EMF.get().createEntityManager();
        Product product = em.find(Product.class, productId);
        if(product == null) {
            throw new RequestException("Product does not exist");
        }
        ProductDTO productDTO = ProductHelper.createProductDTO(product);
        List<ProductServiceDTO> productServices = new ArrayList<ProductServiceDTO>();
        for(ProductService productService : product.getProductServices()) {
            ProductServiceDTO productServiceDTO = new ProductServiceDTO();
            productServiceDTO.setId(productService.getId());
            productServiceDTO.setName(productService.getName());
            productServiceDTO.setDescription(productService.getDescription());
            productServiceDTO.setServiceImage(productService.getImageUrl());
            productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
            productServiceDTO.setCompanyName(productService.getCompany().getName());
            productServiceDTO.setCompanyId(productService.getCompany().getId());
            productServiceDTO.setHasFeasibility(productService.getApiUrl() != null && productService.getApiUrl().length() > 0);
            productServiceDTO.setProduct(productDTO);
            productServices.addAll(ListUtil.toList(new ProductServiceDTO[]{
                    productServiceDTO
            }));
        }
        searchResult.setProducts(ListUtil.toList(productDTO));
        searchResult.setProductServices(productServices);
        // now get the product suppliers for each one of them

        return searchResult;
    }

    @Override
    public List<ProductDTO> listProducts(String textFilter, Integer start, Integer limit, Long aoiId) throws RequestException {
        List<Product> products = null;
        EntityManager em = EMF.get().createEntityManager();
        if(textFilter != null) {
            // check if last character is a space
            boolean partialMatch = !textFilter.endsWith(" ");
            textFilter.trim();
            // break down text into sub words
            String[] words = textFilter.split(" ");
            String keywords = StringUtils.join(words, " | ");
            if (partialMatch) {
                keywords += ":*";
            }
            // change the last word so that it allows for partial match
            String sqlStatement = "SELECT id, ts_rank(tsv, keywords, 8) AS rank\n" +
                    "          FROM product, to_tsquery('" + keywords + "') AS keywords\n" +
                    "          WHERE tsv @@ keywords\n" +
                    "          ORDER BY rank DESC;";
            Query q = em.createNativeQuery(sqlStatement);
            q.setFirstResult(start);
            q.setMaxResults(limit);
            List<Object[]> results = q.getResultList();
            List<Long> productIds = new ArrayList<Long>();
            for (Object[] result : results) {
                productIds.add((Long) result[0]);
            }
            TypedQuery<Product> productQuery = em.createQuery("select p from Product p where p.id IN :productIds", Product.class);
            productQuery.setParameter("productIds", productIds);
            products = productQuery.getResultList();
        } else {
            TypedQuery<Product> productQuery = em.createQuery("select p from Product p order by p.name", Product.class);
            productQuery.setFirstResult(start);
            productQuery.setMaxResults(limit);
            products = productQuery.getResultList();
        }
        em.close();
        return ListUtil.mutate(products, new ListUtil.Mutate<Product, ProductDTO>() {
            @Override
            public ProductDTO mutate(Product product) {
                return ProductHelper.createProductDTO(product);
            }
        });
    }

    @Override
    public List<ImageProductDTO> queryImages(SearchRequest searchRequest) throws RequestException {
        try {
            return EIAPIUtil.queryProducts(searchRequest);
        } catch (Exception e) {
            throw new RequestException(e.getMessage());
        }
    }

    @Override
    public SupplierAPIResponse callSupplierAPI(FeasibilityRequestDTO feasibilityRequest) throws RequestException {
        if(feasibilityRequest == null) {
            throw new RequestException("Feasibility request cannot be null");
        }
        Long productServiceId = feasibilityRequest.getProductServiceId();
        if(productServiceId == null) {
            throw new RequestException("Product service id cannot be null");
        }
        String aoiWKT = feasibilityRequest.getAoiWKT();
        if(aoiWKT == null) {
            throw new RequestException("AoI id cannot be null");
        }
        Date start = feasibilityRequest.getStart();
        if(start == null) {
            throw new RequestException("Start date cannot be null");
        }
        Date stop = feasibilityRequest.getStop();
        if(stop == null) {
            throw new RequestException("Stop date cannot be null");
        }
        List<FormElementValue> formElementValues = feasibilityRequest.getFormElementValues();
        if(formElementValues == null) {
            // TODO - allow empty form values?
            formElementValues = new ArrayList<FormElementValue>();
        }
        EntityManager em = EMF.get().createEntityManager();
        try {
            ProductService productService = em.find(ProductService.class, productServiceId);
            if(productService == null) {
                throw new RequestException("Unknown product service");
            }
/*
            AoI aoi = em.find(AoI.class, aoiId);
            if(aoi == null) {
                throw new RequestException("Unknown AoI");
            }
*/
            // now call the API with the parameters
            JSONObject json = new JSONObject();
            try {
                json.put("aoiWKT", aoiWKT); //AoIUtil.toWKT(aoi));
                json.put("start", start.getTime());
                json.put("stop", stop.getTime());
                // TODO - check they match the actual API definition?
                for(FormElementValue formElementValue : formElementValues) {
                    json.put(formElementValue.getFormid(), formElementValue.getValue());
                }
                String response = sendAPIRequest(productService.getApiUrl(), json.toString());
                FeasibilityResponse feasibilityResponse = new Gson().fromJson(response, FeasibilityResponse.class);
                SupplierAPIResponse supplierAPIResponse = new SupplierAPIResponse();
                supplierAPIResponse.setFeasible(feasibilityResponse.isFeasible());
                supplierAPIResponse.setMessage(feasibilityResponse.getMessage());
                supplierAPIResponse.setFeatures(feasibilityResponse.getFeatures());
                return supplierAPIResponse;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RequestException("Failed to access supplier API");
            } finally {
            }
        } finally {
            em.close();
        }
    }


    private static String sendAPIRequest(String apiURL, String payload) throws Exception {

        String response = "";

        HttpURLConnection connection = null;
        InputStream connectionIstream = null;

        // add some debug log information
        try {
            //Send request
            URL targetURL = new URL(apiURL);
            connection = (HttpURLConnection) targetURL.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", payload.length() + "");
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(payload);
            wr.flush();
            wr.close();
            //Get Response
            connectionIstream = connection.getInputStream();
            if(connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception("Error submitting query to API");
            }
            int bufferSize = 4*4*1024;//same buffer size as in Jetty utils (2*8192)
            byte[] buffer = new byte[bufferSize];
            int read = 0;
            while ((read = connectionIstream.read(buffer)) != -1) {
                response += new String(buffer, 0, read);
            }
        } catch (Exception e){
            throw e;
        } finally {
            if(connectionIstream != null) { connectionIstream.close(); }
        }
        return response;
    }
}
