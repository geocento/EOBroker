package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.Utils.parsers.SensorQuery;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.recommendation.SelectionRule;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;
import com.geocento.webapps.eobroker.common.shared.entities.utils.ProductHelper;
import com.geocento.webapps.eobroker.common.shared.imageapi.SearchRequest;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import com.geocento.webapps.eobroker.customer.client.services.SearchService;
import com.geocento.webapps.eobroker.customer.server.imageapi.EIAPIUtil;
import com.geocento.webapps.eobroker.customer.shared.FeasibilityRequestDTO;
import com.geocento.webapps.eobroker.customer.shared.feasibility.ProductFeasibilityResponse;
import com.google.gson.*;
import com.google.gwt.http.client.RequestException;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.ws.rs.Path;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Path("/")
public class SearchResource implements SearchService {

    static Logger logger = Logger.getLogger(AssetsResource.class);

    static GsonBuilder builder = new GsonBuilder();
    static {
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
    }

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

    private List<RankedSuggestion> completeImagery(String keywordsString) {
        // try to recreate a sensor query
        SensorQuery sensorQuery = new SensorQuery(keywordsString);
        String baseQuery = sensorQuery.getQueryString();
        String error = sensorQuery.getError();
        String suggestions = sensorQuery.getSuggestions();
        List<RankedSuggestion> rankedSuggestions = new ArrayList<RankedSuggestion>();
        if(baseQuery != null && baseQuery.length() > 0) {
            for (String suggestion : suggestions.split(";")) {
                rankedSuggestions.add(new RankedSuggestion(baseQuery + " " + suggestion, Category.imagery, "image::" + baseQuery, 1.0));
            }
        } else {
            for (String suggestion : suggestions.split(";")) {
                rankedSuggestions.add(new RankedSuggestion(suggestion, Category.imagery, "image::" + baseQuery, 1.0));
            }
        }
        return rankedSuggestions;
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
            if(results.size() > 0) {
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
            }
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
    public List<com.geocento.webapps.eobroker.common.shared.imageapi.Product> queryImages(SearchQuery searchQuery) throws RequestException {
        try {
            SearchRequest searchRequest = new SearchRequest();
            searchRequest.setAoiWKT(searchQuery.getAoiWKT());
            // check the sensor query
            if(searchQuery.getSensors() != null) {
                SensorQuery sensorQuery = new SensorQuery(searchQuery.getSensors());
                searchRequest.setSensorFilters(sensorQuery.getSensorFilters());
            } else {
                // run the sensor selection for the product
                Long productId = searchQuery.getProduct();
                EntityManager em = EMF.get().createEntityManager();
                Product product = em.find(Product.class, productId);
                if(product == null) {
                    throw new RequestException("Product with id " + productId + " does not exist");
                }
                String recommendationRule = product.getRecommendationRule();
                if(recommendationRule == null) {
                    throw new RequestException("No rule defined for this product");
                }
                ScriptEngineManager factory = new ScriptEngineManager();
                ScriptEngine engine = factory.getEngineByName("JavaScript");
                engine.put("aoiWKT", searchQuery.getAoiWKT());
                SelectionRule selectionRule = null;
                try {
                    engine.eval(recommendationRule + " " +
                                    "function getSelectionRule() {return '{sensorFilters:' + JSON.stringify(sensorFilters) + ', productFilters: ' + JSON.stringify(productFilters) + '}'};"
                    );
                    String selectionRuleString = (String) ((Invocable) engine).invokeFunction("getSelectionRule");
                    selectionRule = new Gson().fromJson(selectionRuleString, SelectionRule.class);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    throw new RequestException("Failed to run the rule script");
                }
                searchRequest.setSensorFilters(selectionRule.getSensorFilters());
                searchRequest.setFilters(selectionRule.getProductFilters());
            }
            searchRequest.setStart(searchQuery.getStart());
            searchRequest.setStop(searchQuery.getStop());
            searchRequest.setCurrency("EUR");
            return EIAPIUtil.queryProducts(searchRequest);
        } catch (Exception e) {
            throw new RequestException(e.getMessage());
        }
    }

    @Override
    public ProductFeasibilityResponse callSupplierAPI(FeasibilityRequestDTO feasibilityRequest) throws RequestException {
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
                ProductFeasibilityResponse productFeasibilityResponse = builder.create().fromJson(response, ProductFeasibilityResponse.class);
/*
                ProductFeasibilityResponse productFeasibilityResponse = new ProductFeasibilityResponse();
                productFeasibilityResponse.setFeasible(feasibilityResponse.isFeasible());
                productFeasibilityResponse.setMessage(feasibilityResponse.getMessage());
                productFeasibilityResponse.setFeatures(feasibilityResponse.getFeatures());
                productFeasibilityResponse.setCoverages(ListUtil.mutate(feasibilityResponse.getCoverages().getFeatures(), new ListUtil.Mutate<Feature, CoverageFeature>() {
                    @Override
                    public CoverageFeature mutate(Feature feature) {
                        CoverageFeature coverageFeature = new CoverageFeature();
                        String wktValue = null;
                        Geometry geometry = feature.getGeometry();
                        switch(geometry.getType()) {
                            case "Polygon":
                                wktValue = "POLYGON(" + convertGeoJsonRings(((Polygon) geometry).getCoordinates()) + ")";
                                break;
                        }
                        coverageFeature.setWktValue(wktValue);
                        Map<String, Serializable> properties = feature.getProperties();
                        if(properties != null) {
                            coverageFeature.setName(properties.get("name") + "");
                            coverageFeature.setDescription(properties.get("description") + "");
                        }
                        return coverageFeature;
                    }
                }));
*/
                return productFeasibilityResponse;
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RequestException("Failed to access supplier API");
            } finally {
            }
        } finally {
            em.close();
        }
    }

    @Override
    public List<Suggestion> completeSensors(String sensors) {
        ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
        ArrayList<RankedSuggestion> rankedSuggestions = new ArrayList<RankedSuggestion>();
        // start working on sensors options
        try {
            rankedSuggestions.addAll(completeImagery(sensors));
        } catch (Exception e) {

        }
        try {
            // now look for matching products
            // check if last character is a space
            boolean partialMatch = !sensors.endsWith(" ");
            sensors.trim();
            // break down text into sub words
            String[] words = sensors.split(" ");
            String keywords = StringUtils.join(words, " | ");
            if (partialMatch) {
                keywords += ":*";
            }
            rankedSuggestions.addAll(completeProducts(keywords, null));
        } catch (Exception e) {

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

    @Override
    public List<CompanyDTO> listCompanies(String textFilter, Integer start, Integer limit, Long aoiId) {
        List<Company> companies = null;
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
                    "          FROM company, to_tsquery('" + keywords + "') AS keywords\n" +
                    "          WHERE tsv @@ keywords\n" +
                    "          ORDER BY rank DESC;";
            Query q = em.createNativeQuery(sqlStatement);
            q.setFirstResult(start);
            q.setMaxResults(limit);
            List<Object[]> results = q.getResultList();
            List<Long> companyIds = new ArrayList<Long>();
            for (Object[] result : results) {
                companyIds.add((Long) result[0]);
            }
            TypedQuery<Company> companyQuery = em.createQuery("select c from Company c where c.id IN :companyIds", Company.class);
            companyQuery.setParameter("companyIds", companyIds);
            companies = companyQuery.getResultList();
        } else {
            TypedQuery<Company> companyQuery = em.createQuery("select c from Company c order by c.name", Company.class);
            companyQuery.setFirstResult(start);
            companyQuery.setMaxResults(limit);
            companies = companyQuery.getResultList();
        }
        em.close();
        return ListUtil.mutate(companies, new ListUtil.Mutate<Company, CompanyDTO>() {
            @Override
            public CompanyDTO mutate(Company company) {
                return CompanyHelper.createCompanyDTO(company);
            }
        });
    }

    private String convertGeoJsonRings(List<List<double[]>> rings) {
        String wktCoordinates = ListUtil.toString(rings, new ListUtil.GetLabel<List<double[]>>() {
            @Override
            public String getLabel(List<double[]> ring) {
                return convertGeoJsonRing(ring);
            }
        }, ",");
        return wktCoordinates;
    }

    private String convertGeoJsonRing(List<double[]> ring) {
        String wktCoordinates = "(";
        wktCoordinates = ListUtil.toString(ring, new ListUtil.GetLabel<double[]>() {
            @Override
            public String getLabel(double[] value) {
                return value[0] + " " + value[1];
            }
        }, ",");
        wktCoordinates += ")";
        return wktCoordinates;
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
