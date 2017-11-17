package com.geocento.webapps.eobroker.customer.server.servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.common.server.Utils.DBHelper;
import com.geocento.webapps.eobroker.common.server.Utils.KeyGenerator;
import com.geocento.webapps.eobroker.common.server.Utils.XMLUtil;
import com.geocento.webapps.eobroker.common.server.Utils.parsers.SensorQuery;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.*;
import com.geocento.webapps.eobroker.common.shared.entities.datasets.*;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.recommendation.SelectionRule;
import com.geocento.webapps.eobroker.common.shared.entities.requests.FeasibilitySearch;
import com.geocento.webapps.eobroker.common.shared.entities.utils.CompanyHelper;
import com.geocento.webapps.eobroker.common.shared.feasibility.FeasibilityRequest;
import com.geocento.webapps.eobroker.common.shared.feasibility.FeasibilityResponse;
import com.geocento.webapps.eobroker.common.shared.feasibility.Parameter;
import com.geocento.webapps.eobroker.common.shared.feasibility.UserInformation;
import com.geocento.webapps.eobroker.common.shared.imageapi.SearchRequest;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import com.geocento.webapps.eobroker.customer.client.services.SearchService;
import com.geocento.webapps.eobroker.customer.client.utils.CSWUtils;
import com.geocento.webapps.eobroker.customer.server.imageapi.EIAPIUtil;
import com.geocento.webapps.eobroker.customer.server.utils.RankedOffer;
import com.geocento.webapps.eobroker.customer.server.utils.UserUtils;
import com.geocento.webapps.eobroker.customer.shared.*;
import com.geocento.webapps.eobroker.customer.shared.utils.ProductHelper;
import com.google.gson.*;
import com.google.gwt.http.client.RequestException;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    static KeyGenerator keyGenerator = new KeyGenerator(16);

    @Context
    HttpServletRequest request;

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
    public List<Suggestion> complete(String text, Category category) throws RequestException {
        if(category == null) {
            String keywords = DBHelper.generateKeywords(text);
            // make sure words together get better ranking than when they are far apart
            // optimise based on the ratio of matching words and the total number of words of the search phrase
            // prioritise based on matching word position, ie if starts the same give higher priority
            // analyse words and look for common words such as image, product, theme
            EntityManager em = EMF.get().createEntityManager();
            try {
                // look in the generic view table
                // change the last word so that it allows for partial match
                Query q = em.createNativeQuery("SELECT id, " +
                        "category, " +
                        "ts_rank(tsvname, keywords, 8) AS rank, " +
                        "name " +
                        "          FROM textsearch, to_tsquery('" + keywords + "') AS keywords\n" +
                        "          WHERE tsvname @@ keywords\n" +
                        "          ORDER BY rank DESC\n" +
                        "          LIMIT 10;");
                List<Object[]> results = q.getResultList();
                return ListUtil.mutate(results, new ListUtil.Mutate<Object[], Suggestion>() {
                    @Override
                    public Suggestion mutate(Object[] result) {
                        Long id = (Long) result[0];
                        String category = (String) result[1];
                        String name = (String) result[3];
                        return new Suggestion(name, Category.valueOf(category), "access" + "::" + id);
                    }
                });
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                throw new RequestException(e instanceof RequestException ? e.getMessage() : "Server error");
            } finally {
                em.close();
            }
        } else {
            return completeCategory(category, text,
                    category == Category.companies ? "          AND supplier = '1' AND status = 'APPROVED'" : "");
        }
    }

    @Override
    public List<Suggestion> completeAllCompanies(String text) throws RequestException {
        return completeCategory(Category.companies, text, null);
    }

    private List<Suggestion> completeCategory(Category category, String keywords, String additionalStatement) throws RequestException {
        // change the last word so that it allows for partial match
        EntityManager em = EMF.get().createEntityManager();
        try {
            Query q = em.createNativeQuery("SELECT id, " +
                    "category, " +
                    "ts_rank(tsvname, keywords, 8) AS rank\n" +
                    "name, " +
                    "          FROM " + category.toString() + ", to_tsquery('" + keywords + "') AS keywords\n" +
                    "          WHERE tsvname @@ keywords\n" +
                    (additionalStatement != null ? additionalStatement : "") +
                    "          ORDER BY rank DESC\n" +
                    "          LIMIT 10;");
            List<Object[]> results = q.getResultList();
            return ListUtil.mutate(results, new ListUtil.Mutate<Object[], Suggestion>() {
                @Override
                public Suggestion mutate(Object[] result) {
                    Long id = (Long) result[0];
                    String category = (String) result[1];
                    String name = (String) result[3];
                    return new Suggestion(name, Category.valueOf(category), "access" + "::" + id);
                }
            });
        } catch (Exception e) {
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Server error");
        } finally {
            em.close();
        }
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
    public SearchResult getMatchingServices(String text) throws RequestException {
        SearchResult searchResult = new SearchResult();
        // start with products
        {
            List<ProductDTO> products = listProducts(text, 0, 5, null, Sector.all, Thematic.all);
            boolean more = products.size() > 4;
            if (more) {
                products = products.subList(0, 4);
            }
            searchResult.setProducts(products);
            searchResult.setMoreProducts(more);
        }
        // now search for services
        {
            List<ProductServiceDTO> productServiceDTOs = listProductServices(text, 0, 5, null, null, false, null, null);
            boolean more = productServiceDTOs.size() > 4;
            if (more) {
                productServiceDTOs = productServiceDTOs.subList(0, 4);
            }
            searchResult.setProductServices(productServiceDTOs);
            searchResult.setMoreProductServices(more);
        }
        // now search for datasets
        {
            List<ProductDatasetDTO> productDatasetDTOs = listProductDatasets(text, 0, 5, null, null, null, null, null, false, null, null);
            boolean more = productDatasetDTOs.size() > 4;
            if (more) {
                productDatasetDTOs = productDatasetDTOs.subList(0, 4);
            }
            searchResult.setProductDatasets(productDatasetDTOs);
            searchResult.setMoreProductDatasets(more);
        }
        // now search for software
        {
            List<SoftwareDTO> softwareDTOs = listSoftware(text, 0, 5, null, null, false, null, null);
            boolean more = softwareDTOs.size() > 4;
            if (more) {
                softwareDTOs = softwareDTOs.subList(0, 4);
            }
            searchResult.setSoftwares(softwareDTOs);
            searchResult.setMoreSoftware(more);
        }
        // now search for projects
        {
            List<ProjectDTO> projectDTOs = listProjects(text, 0, 5, null, null, null, false, null, null);
            boolean more = projectDTOs.size() > 4;
            if (more) {
                projectDTOs = projectDTOs.subList(0, 4);
            }
            searchResult.setProjects(projectDTOs);
            searchResult.setMoreProjects(more);
        }
        // now search for companies
        {
            List<CompanyDTO> companyDTOs = listCompanies(text, 0, 5, null, null, 0, null);
            boolean more = companyDTOs.size() > 4;
            if (more) {
                companyDTOs = companyDTOs.subList(0, 4);
            }
            searchResult.setCompanies(companyDTOs);
            searchResult.setMoreCompanies(more);
        }
        return searchResult;
    }

    private ProductServiceDTO createProductServiceDTO(ProductService productService) {
        ProductServiceDTO productServiceDTO = new ProductServiceDTO();
        productServiceDTO.setId(productService.getId());
        productServiceDTO.setName(productService.getName());
        productServiceDTO.setDescription(productService.getDescription());
        productServiceDTO.setServiceImage(productService.getImageUrl());
        productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
        productServiceDTO.setCompanyName(productService.getCompany().getName());
        productServiceDTO.setCompanyId(productService.getCompany().getId());
        productServiceDTO.setHasFeasibility(productService.getApiUrl() != null && productService.getApiUrl().length() > 0);
        productServiceDTO.setHasSamples(ProductHelper.hasSamples(productService.getSamples()));
        productServiceDTO.setProduct(ProductHelper.createProductDTO(productService.getProduct()));
        return productServiceDTO;
    }

    @Override
    public List<Offer> getMatchingOffer(String textFilter, Category category, Long aoiId) throws RequestException {
        ArrayList<RankedOffer> offers = new ArrayList<RankedOffer>();
        String keywords = DBHelper.generateKeywords(textFilter);
        EntityManager em = EMF.get().createEntityManager();
        try {
            // change the last word so that it allows for partial match
            Query q = em.createNativeQuery("SELECT id, category, ts_rank(tsvname, keywords, 8) AS rank\n" +
                    "          FROM textsearch, to_tsquery('" + keywords + "') AS keywords\n" +
                    "          WHERE tsvname @@ keywords\n" +
                    "          ORDER BY rank DESC\n" +
                    "          LIMIT 50;");
            List<Object[]> results = q.getResultList();
            if(results.size() > 0) {
                final HashMap<Long, Double> rankings = new HashMap<Long, Double>();
                List<Long> productIds = new ArrayList<Long>();
                List<Long> productServiceIds = new ArrayList<Long>();
                List<Long> productDatasetIds = new ArrayList<Long>();
                for (Object[] result : results) {
                    Long id = (Long) result[0];
                    Double ranking = (Double) result[2];
                    rankings.put(id, ranking);
                    switch(Category.valueOf((String) result[1])) {
                        case products:
                            productIds.add(id);
                            break;
                        case productservices:
                            productServiceIds.add(id);
                            break;
                        case productdatasets:
                            productDatasetIds.add(id);
                            break;
                        case software:
                    }
                }
                // now fetch the actual entities
                // start with product
                if(productIds.size() > 0) {
                    TypedQuery<Product> productQuery = em.createQuery("select p from Product p where p.id IN :productIds", Product.class);
                    productQuery.setParameter("productIds", productIds);
                    List<Product> products = productQuery.getResultList();
                    offers.addAll(ListUtil.mutate(products, new ListUtil.Mutate<Product, RankedOffer>() {
                        @Override
                        public RankedOffer mutate(Product product) {
                            return new RankedOffer(rankings.get(product.getId()), ProductHelper.createProductDTO(product));
                        }
                    }));
                }
                // then product services
                if(productDatasetIds.size() > 0) {
                    TypedQuery<ProductService> productServiceQuery = em.createQuery("select p from ProductService p where p.id IN :productIds", ProductService.class);
                    productServiceQuery.setParameter("productIds", productServiceIds);
                    offers.addAll(ListUtil.mutate(productServiceQuery.getResultList(), new ListUtil.Mutate<ProductService, RankedOffer>() {
                        @Override
                        public RankedOffer mutate(ProductService productService) {
                            return new RankedOffer(rankings.get(productService.getId()), createProductServiceDTO(productService));
                        }
                    }));
                }
                // then product datasets
                if(productDatasetIds.size() > 0) {
                    TypedQuery<ProductDataset> productDatasetQuery = em.createQuery("select p from ProductDataset p where p.id IN :productIds", ProductDataset.class);
                    productDatasetQuery.setParameter("productIds", productServiceIds);
                    offers.addAll(ListUtil.mutate(productDatasetQuery.getResultList(), new ListUtil.Mutate<ProductDataset, RankedOffer>() {
                        @Override
                        public RankedOffer mutate(ProductDataset productDataset) {
                            return new RankedOffer(rankings.get(productDataset.getId()), createProductDatasetDTO(productDataset));
                        }
                    }));
                }
            }
            return ListUtil.mutate(offers, new ListUtil.Mutate<RankedOffer, Offer>() {
                @Override
                public Offer mutate(RankedOffer object) {
                    return object.getOffer();
                }
            });
        } finally {
            em.close();
        }
    }

    private ProductDatasetDTO createProductDatasetDTO(ProductDataset productDataset) {
        ProductDatasetDTO productDatasetDTO = new ProductDatasetDTO();
        productDatasetDTO.setId(productDataset.getId());
        productDatasetDTO.setName(productDataset.getName());
        productDatasetDTO.setImageUrl(productDataset.getImageUrl());
        productDatasetDTO.setDescription(productDataset.getDescription());
        productDatasetDTO.setCompany(CompanyHelper.createCompanyDTO(productDataset.getCompany()));
        productDatasetDTO.setHasExplore(productDataset.getDatasetStandard() != null);
        productDatasetDTO.setCommercial(productDataset.getServiceType() == ServiceType.commercial);
        productDatasetDTO.setHasSamples(ProductHelper.hasSamples(productDataset.getSamples()));
        return productDatasetDTO;
    }

    private List<DatasetProviderDTO> getDatasetProviders(String textFilter, int start, int limit) {
        List<DatasetProvider> datasetProviders = null;
        EntityManager em = EMF.get().createEntityManager();
        if(textFilter != null) {
/*
            // check if last character is a space
            boolean partialMatch = !textFilter.endsWith(" ");
            textFilter.trim();
            // break down text into sub words
            String[] words = textFilter.split("\\s+");
            String keywords = StringUtils.join(words, " | ");
            if (partialMatch) {
                keywords += ":*";
            }
*/
            String keywords = DBHelper.generateKeywords(textFilter);
            // change the last word so that it allows for partial match
            String sqlStatement = "SELECT id, ts_rank(tsv, keywords, 8) AS rank\n" +
                    "          FROM datasetprovider, to_tsquery('" + keywords + "') AS keywords\n" +
                    "          WHERE tsv @@ keywords\n" +
                    "          ORDER BY rank DESC;";
            Query q = em.createNativeQuery(sqlStatement);
            q.setFirstResult(start);
            q.setMaxResults(limit);
            List<Object[]> results = q.getResultList();
            List<Long> datasetIds = new ArrayList<Long>();
            for (Object[] result : results) {
                datasetIds.add((Long) result[0]);
            }
            TypedQuery<DatasetProvider> datasetProviderTypedQuery = em.createQuery("select d from DatasetProvider d where d.id IN :datasetIds", DatasetProvider.class);
            datasetProviderTypedQuery.setParameter("datasetIds", datasetIds);
            datasetProviders = datasetProviderTypedQuery.getResultList();
        } else {
            TypedQuery<DatasetProvider> datasetProviderTypedQuery = em.createQuery("select d from DatasetProvider d order by d.name", DatasetProvider.class);
            datasetProviderTypedQuery.setFirstResult(start);
            datasetProviderTypedQuery.setMaxResults(limit);
            datasetProviders = datasetProviderTypedQuery.getResultList();
        }
        em.close();
        return ListUtil.mutate(datasetProviders, new ListUtil.Mutate<DatasetProvider, DatasetProviderDTO>() {
            @Override
            public DatasetProviderDTO mutate(DatasetProvider datasetProvider) {
                return createDatasetProviderDTO(datasetProvider);
            }
        });
    }

    private DatasetProviderDTO createDatasetProviderDTO(DatasetProvider datasetProvider) {
        DatasetProviderDTO datasetProviderDTO = new DatasetProviderDTO();
        datasetProviderDTO.setName(datasetProvider.getName());
        datasetProviderDTO.setIconURL(datasetProvider.getIconUrl());
        datasetProviderDTO.setUri(datasetProvider.getUri());
        datasetProviderDTO.setExtent(datasetProvider.getExtent());
        datasetProviderDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(datasetProvider.getCompany()));
        return datasetProviderDTO;
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

    private List<Long> getFilteredIds(EntityManager em, String tableName, String textFilter, Integer start, Integer limit, String additionalStatement) {
        return getFilteredIds(em, tableName, textFilter, start, limit, additionalStatement, null);
    }
    private List<Long> getFilteredIds(EntityManager em, String tableName, String textFilter, Integer start, Integer limit, String additionalStatement, String joinStatement) {
        String sqlStatement = "";
        if(textFilter != null && textFilter.length() > 0) {
            String keywords = DBHelper.generateKeywords(textFilter);
            // change the last word so that it allows for partial match
            sqlStatement = "SELECT t.id, ts_rank(tsv, keywords, 8) AS rank\n" +
                    "          FROM " + tableName + " t, to_tsquery('" + keywords + "') AS keywords\n" +
                    (joinStatement == null ? "" : " " + joinStatement) +
                    "          WHERE tsv @@ keywords\n" +
                    (additionalStatement == null ? "" : (" AND " + additionalStatement)) +
                    "          ORDER BY rank DESC;";
        } else {
            sqlStatement = "SELECT t.id, 'dummy'\n" +
                    "          FROM " + tableName + " t " +
                    (joinStatement == null ? "" : " " + joinStatement) +
                    (additionalStatement == null ? "" : (" WHERE " + additionalStatement)) +
                    " ORDER BY name ASC";
/*
                    (alternateOrder == null ? "" : " ORDER BY " + alternateOrder);
*/
        }
        Query q = em.createNativeQuery(sqlStatement);
        q.setFirstResult(start);
        q.setMaxResults(limit);
        List<Object[]> results = q.getResultList();
        List<Long> productIds = new ArrayList<Long>();
        for (Object[] result : results) {
            productIds.add((Long) result[0]);
        }
        return productIds;
    }

    @Override
    public List<ProductDTO> listProducts(String textFilter, Integer start, Integer limit, Long aoiId, Sector sector, Thematic thematic) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        List<Product> products = null;
        EntityManager em = EMF.get().createEntityManager();
        String additionalStatement = null;
        boolean hasSector = sector != null && sector != Sector.all;
        boolean hasThematic = thematic != null && thematic != Thematic.all;
        if(hasSector) {
            additionalStatement = "sector = '" + sector.toString() + "'";
        }
        if(hasThematic) {
            additionalStatement = (additionalStatement == null ? "" : additionalStatement + " AND ") +
                    "thematic = '" + thematic.toString() + "'";
        }
        List<Long> productIds = getFilteredIds(em, "product", textFilter, start, limit, additionalStatement);
        if(productIds.size() > 0) {
            TypedQuery<Product> productQuery = em.createQuery("select p from Product p where p.id IN :productIds", Product.class);
            productQuery.setParameter("productIds", productIds);
            products = productQuery.getResultList();
        } else {
            products = new ArrayList<Product>();
        }
        // TODO - faster way of doing this?
        TypedQuery<Long> query = em.createQuery("select f.product.id from Following f where f.user.username = :userName and f.product is not null", Long.class);
        query.setParameter("userName", userName);
        List<Long> followings = query.getResultList();
        em.close();
        // make sure order has stayed the same
        List<Product> sortedProducts = new ArrayList<Product>();
        for(final Long id : productIds) {
            sortedProducts.add(ListUtil.findValue(products, new ListUtil.CheckValue<Product>() {
                @Override
                public boolean isValue(Product value) {
                    return value.getId().equals(id);
                }
            }));
        }
        return ListUtil.mutate(sortedProducts, new ListUtil.Mutate<Product, ProductDTO>() {
            @Override
            public ProductDTO mutate(Product product) {
                ProductDTO productDTO = ProductHelper.createProductDTO(product);
                productDTO.setFollowing(followings.contains(product.getId()));
                return productDTO;
            }
        });
    }

    @Override
    public List<ProductServiceDTO> listProductServices(String textFilter, Integer start, Integer limit,
                                                       Long aoiId, String aoiWKT,
                                                       boolean affiliatesOnly, Long companyId, Long productId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        List<ProductService> productServices = null;
        EntityManager em = EMF.get().createEntityManager();
        List<String> additionalStatements = new ArrayList<String>();
        if(aoiId != null) {
            AoI aoi = em.find(AoI.class, aoiId);
            if(aoi != null) {
                additionalStatements.add("(extent is NULL OR ST_Intersects(extent, '" + aoi.getGeometry() + "'::geometry) = 't')");
            }
        } else if(aoiWKT != null) {
            additionalStatements.add("(extent is NULL OR ST_Intersects(extent, '" + aoiWKT + "'::geometry) = 't')");
        }
        addAffiliatesStatement(em, additionalStatements, userName, affiliatesOnly, companyId);
        if(productId != null) {
            additionalStatements.add("product_id = " + productId);
        }
        List<Long> productIds = getFilteredIds(em, "productservice", textFilter, start, limit,
                additionalStatements.size() == 0 ? null : StringUtils.join(additionalStatements, " AND "));
        if(productIds.size() > 0) {
            TypedQuery<ProductService> productQuery = em.createQuery("select p from ProductService p where p.id IN :productIds", ProductService.class);
            productQuery.setParameter("productIds", productIds);
            productServices = productQuery.getResultList();
        } else {
            productServices = new ArrayList<ProductService>();
        }
        em.close();
        // make sure order has stayed the same
                List<ProductService> sortedItems = new ArrayList<ProductService>();
        for(final Long id : productIds) {
            sortedItems.add(ListUtil.findValue(productServices, new ListUtil.CheckValue<ProductService>() {
                @Override
                public boolean isValue(ProductService value) {
                    return value.getId().equals(id);
                }
            }));
        }
        return ListUtil.mutate(sortedItems, new ListUtil.Mutate<ProductService, ProductServiceDTO>() {
            @Override
            public ProductServiceDTO mutate(ProductService productService) {
                return createProductServiceDTO(productService);
            }
        });
    }

    @Override
    public List<ProductDatasetDTO> listProductDatasets(String textFilter, Integer start, Integer limit,
                                                       Long aoiId, String aoiWKT,
                                                       ServiceType serviceType, Long startTimeFrame, Long stopTimeFrame,
                                                       boolean affiliatesOnly, Long companyId, Long productId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        List<ProductDataset> productDatasets = null;
        EntityManager em = EMF.get().createEntityManager();
        List<String> additionalStatements = new ArrayList<String>();
        if(aoiId != null) {
            AoI aoi = em.find(AoI.class, aoiId);
            if(aoi != null) {
                additionalStatements.add("(extent is NULL OR ST_Intersects(extent, '" + aoi.getGeometry() + "'::geometry) = 't')");
            }
        } else if(aoiWKT != null) {
            additionalStatements.add("(extent is NULL OR ST_Intersects(extent, '" + aoiWKT + "'::geometry) = 't')");
        }
        if(serviceType != null) {
            additionalStatements.add("servicetype = '" + serviceType.toString() + "'");
        }
        // TODO - add filter for inclusiveness or not of time frame
/*
        SimpleDateFormat fmt = new SimpleDateFormat("YYYY-MM-dd");
*/
        if(startTimeFrame != null && stopTimeFrame != null) {
            additionalStatements.add("startdate < to_timestamp(" + stopTimeFrame + ") AND " +
                    "(stopdate is null OR stopdate > to_timestamp(" + startTimeFrame + "))");
        } else if(startTimeFrame != null) {
            additionalStatements.add("(stopdate is null OR stopdate > to_timestamp(" + startTimeFrame + "))");
        } else if(stopTimeFrame != null) {
            additionalStatements.add("startdate < to_timestamp(" + stopTimeFrame + ")");
        }
        addAffiliatesStatement(em, additionalStatements, userName, affiliatesOnly, companyId);
        if(productId != null) {
            additionalStatements.add("product_id = " + productId);
        }
        List<Long> productIds = getFilteredIds(em, "productdataset", textFilter, start, limit,
                additionalStatements.size() == 0 ? null : StringUtils.join(additionalStatements, " AND "));
        if(productIds.size() > 0) {
            TypedQuery<ProductDataset> productQuery = em.createQuery("select p from ProductDataset p where p.id IN :productIds", ProductDataset.class);
            productQuery.setParameter("productIds", productIds);
            productDatasets = productQuery.getResultList();
        } else {
            productDatasets = new ArrayList<ProductDataset>();
        }
        em.close();
        // make sure order has stayed the same
        List<ProductDataset> sortedItems = new ArrayList<ProductDataset>();
        for(final Long id : productIds) {
            sortedItems.add(ListUtil.findValue(productDatasets, new ListUtil.CheckValue<ProductDataset>() {
                @Override
                public boolean isValue(ProductDataset value) {
                    return value.getId().equals(id);
                }
            }));
        }
        return ListUtil.mutate(sortedItems, new ListUtil.Mutate<ProductDataset, ProductDatasetDTO>() {
            @Override
            public ProductDatasetDTO mutate(ProductDataset productDataset) {
                return createProductDatasetDTO(productDataset);
            }
        });
    }

    private void addAffiliatesStatement(EntityManager em, List<String> additionalStatements, String userName, boolean affiliatesOnly, Long companyId) throws RequestException {
        if(affiliatesOnly) {
            String companyIds = getAffiliatesIds(em, userName);
            additionalStatements.add("company_id IN (" + companyIds + ")");
        } else if(companyId != null) {
            additionalStatements.add("company_id = " + companyId);
        }
    }

    private String getAffiliatesIds(EntityManager em, String userName) throws RequestException {
        User user = em.find(User.class, userName);
        List<Company> companies = user.getCompany().getAffiliates();
        if(companies.size() == 0) {
            throw new RequestException("No affiliate company defined for your company");
        }
        String companyIds = ListUtil.toString(companies, new ListUtil.GetLabel<Company>() {
            @Override
            public String getLabel(Company value) {
                return value.getId() + "";
            }
        }, ",");
        return companyIds;
    }

    @Override
    public List<SoftwareDTO> listSoftware(String textFilter, Integer start, Integer limit, Long aoiId, SoftwareType softwareType,
                                          boolean affiliatesOnly, Long companyId, Long productId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        List<Software> softwares = null;
        EntityManager em = EMF.get().createEntityManager();
        List<String> additionalStatements = new ArrayList<String>();
        if(softwareType != null) {
            additionalStatements.add("softwaretype = '" + softwareType.toString() + "'");
        }
        addAffiliatesStatement(em, additionalStatements, userName, affiliatesOnly, companyId);
        String joinStatement = null;
        if(productId != null) {
            joinStatement = "left join software_productsoftware p on p.software_id = t.id ";
            // search for productsoftware with the matching products
            additionalStatements.add("p.products_id IN ( select id from productsoftware  where product_id  = " + productId + ")");
        }
        List<Long> softwareIds = getFilteredIds(em, "software", textFilter, start, limit,
                additionalStatements.size() == 0 ? null : StringUtils.join(additionalStatements, " AND "),
                joinStatement);
        if(softwareIds.size() > 0) {
            TypedQuery<Software> softwareQuery = em.createQuery("select p from Software p where p.id IN :softwareIds", Software.class);
            softwareQuery.setParameter("softwareIds", softwareIds);
            softwares = softwareQuery.getResultList();
        } else {
            softwares = new ArrayList<Software>();
        }
        em.close();
        // make sure order has stayed the same
        List<Software> sortedItems = new ArrayList<Software>();
        for(final Long id : softwareIds) {
            sortedItems.add(ListUtil.findValue(softwares, new ListUtil.CheckValue<Software>() {
                @Override
                public boolean isValue(Software value) {
                    return value.getId().equals(id);
                }
            }));
        }
        return ListUtil.mutate(sortedItems, new ListUtil.Mutate<Software, SoftwareDTO>() {
            @Override
            public SoftwareDTO mutate(Software software) {
                return createSoftwareDTO(software);
            }
        });
    }

    private SoftwareDTO createSoftwareDTO(Software software) {
        SoftwareDTO softwareDTO = new SoftwareDTO();
        softwareDTO.setId(software.getId());
        softwareDTO.setName(software.getName());
        softwareDTO.setImageUrl(software.getImageUrl());
        softwareDTO.setDescription(software.getDescription());
        softwareDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(software.getCompany()));
        return softwareDTO;
    }

    private ProjectDTO createProjectDTO(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setName(project.getName());
        projectDTO.setImageUrl(project.getImageUrl());
        projectDTO.setDescription(project.getDescription());
        projectDTO.setCompanyDTO(CompanyHelper.createCompanyDTO(project.getCompany()));
        return projectDTO;
    }

    @Override
    public List<ProjectDTO> listProjects(String textFilter, Integer start, Integer limit, Long aoiId,
                                         Date startDate, Date stopDate,
                                         boolean affiliatesOnly, Long companyId, Long productId) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        List<Project> projects = null;
        EntityManager em = EMF.get().createEntityManager();
        List<String> additionalStatements = new ArrayList<String>();
        List<String> joinStatements = new ArrayList<String>();
        // project time frame
        if(startDate != null && stopDate != null) {
            additionalStatements.add("startdate < to_timestamp(" + stopDate + ") AND " +
                    "(stopdate is null OR stopdate > to_timestamp(" + startDate + "))");
        } else if(startDate != null) {
            additionalStatements.add("(stopdate is null OR stopdate > to_timestamp(" + startDate + "))");
        } else if(stopDate != null) {
            additionalStatements.add("startdate < to_timestamp(" + stopDate + ")");
        }
        // companies filter
        if(affiliatesOnly) {
            joinStatements.add("left join companyrole c on c.project_id = t.id ");
            String companyIds = getAffiliatesIds(em, userName);
            additionalStatements.add("c.company_id  IN (" + companyIds + ")");
        } else if(companyId != null) {
            joinStatements.add("left join companyrole c on c.project_id = t.id ");
            // search for company role with the matching company
            additionalStatements.add("c.company_id  = " + companyId);
        }
        if(productId != null) {
            joinStatements.add("left join productproject p on p.project_id = t.id ");
            // search for productsoftware with the matching products
            additionalStatements.add("p.product_id  = " + productId);
        }
        List<Long> projectIds = getFilteredIds(em, "project", textFilter, start, limit,
                additionalStatements.size() == 0 ? null : StringUtils.join(additionalStatements, " AND "),
                joinStatements.size() == 0 ? null : StringUtils.join(joinStatements, " ")
        );

        if(projectIds.size() > 0) {
            TypedQuery<Project> projectQuery = em.createQuery("select p from Project p where p.id IN :projectIds", Project.class);
            projectQuery.setParameter("projectIds", projectIds);
            projects = projectQuery.getResultList();
        } else {
            projects = new ArrayList<Project>();
        }
        em.close();
        // make sure order has stayed the same
        List<Project> sortedItems = new ArrayList<Project>();
        for(final Long id : projectIds) {
            sortedItems.add(ListUtil.findValue(projects, new ListUtil.CheckValue<Project>() {
                @Override
                public boolean isValue(Project value) {
                    return value.getId().equals(id);
                }
            }));
        }
        return ListUtil.mutate(sortedItems, new ListUtil.Mutate<Project, ProjectDTO>() {
            @Override
            public ProjectDTO mutate(Project project) {
                return createProjectDTO(project);
            }
        });
    }

    @Override
    public List<com.geocento.webapps.eobroker.common.shared.imageapi.Product> queryImages(SearchQuery searchQuery) throws RequestException {
        String userName = UserUtils.verifyUser(request);
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
    public FeasibilityResponseDTO callSupplierAPI(FeasibilityRequestDTO feasibilityRequestDTO) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        if(feasibilityRequestDTO == null) {
            throw new RequestException("Feasibility request cannot be null");
        }
        Long productServiceId = feasibilityRequestDTO.getProductServiceId();
        if(productServiceId == null) {
            throw new RequestException("Product service id cannot be null");
        }
        String aoiWKT = feasibilityRequestDTO.getAoiWKT();
        if(aoiWKT == null) {
            throw new RequestException("AoI id cannot be null");
        }
        Date start = feasibilityRequestDTO.getStart();
        if(start == null) {
            throw new RequestException("Start date cannot be null");
        }
        Date stop = feasibilityRequestDTO.getStop();
        if(stop == null) {
            throw new RequestException("Stop date cannot be null");
        }
        List<FormElementValue> formElementValues = feasibilityRequestDTO.getFormElementValues();
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
            try {
                FeasibilityRequest feasibilityRequest = new FeasibilityRequest();
                User user = em.find(User.class, userName);
                UserInformation userInformation = new UserInformation();
                userInformation.setUserName(userName);
                userInformation.setUserCompany(user.getCompany() == null ? null : user.getCompany().getName());
                feasibilityRequest.setId(new KeyGenerator(10).CreateKey());
                feasibilityRequest.setUserInformation(userInformation);
                feasibilityRequest.setAoiWKT(aoiWKT);
                feasibilityRequest.setStart(start);
                feasibilityRequest.setStop(stop);
                feasibilityRequest.setServiceParameters(ListUtil.mutate(formElementValues, new ListUtil.Mutate<FormElementValue, Parameter>() {
                    @Override
                    public Parameter mutate(FormElementValue formElementValue) {
                        Parameter parameter = new Parameter();
                        parameter.setName(formElementValue.getFormid());
                        parameter.setValue(formElementValue.getValue());
                        return parameter;
                    }
                }));
                // store search
                em.getTransaction().begin();
                FeasibilitySearch feasibilitySearch = new FeasibilitySearch();
                feasibilitySearch.setId(keyGenerator.CreateKey());
                feasibilitySearch.setSelectionGeometry(feasibilityRequest.getAoiWKT());
                feasibilitySearch.setProductService(productService);
                feasibilitySearch.setFormValues(formElementValues);
                em.persist(feasibilitySearch);
                em.getTransaction().commit();
                // call the supplier
                ObjectMapper objectMapper = new ObjectMapper();
                String response = sendAPIRequest(productService.getApiUrl(), objectMapper.writeValueAsString(feasibilityRequest));
                FeasibilityResponse feasibilityResponse = objectMapper.readValue(response, FeasibilityResponse.class);
                // TODO - store response id and statistics
                // generate response
                FeasibilityResponseDTO feasibilityResponseDTO = new FeasibilityResponseDTO();
                feasibilityResponseDTO.setSearchId(feasibilitySearch.getId());
                feasibilityResponseDTO.setFeasibilityResponse(feasibilityResponse);
                return feasibilityResponseDTO;
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
        return null;
    }

    @Override
    public List<CompanyDTO> listCompanies(String textFilter, Integer start, Integer limit, Long aoiId, COMPANY_SIZE companySize, int minYears, String countryCode) throws RequestException {
        String userName = UserUtils.verifyUser(request);
        EntityManager em = EMF.get().createEntityManager();
        List<String> additionalStatements = new ArrayList<String>();
        additionalStatements.add("supplier = '1'");
        additionalStatements.add("status = 'APPROVED'");
        if(companySize != null) {
            additionalStatements.add("companysize = '" + companySize.toString() + "'");
        }
        if(minYears > 0) {
            additionalStatements.add("startedin < date 'today' - interval '" + minYears +" year'");
        }
        if(countryCode != null) {
            additionalStatements.add("countrycode = '" + countryCode + "'");
        }
        List<Long> companyIds = getFilteredIds(em, "company", textFilter, start, limit,
                additionalStatements.size() > 0 ? StringUtils.join(additionalStatements, " AND ") : null);
        List<Company> companies = null;
        if(companyIds.size() > 0) {
            TypedQuery<Company> companyQuery = em.createQuery("select c from Company c where c.id IN :companyIds", Company.class);
            companyQuery.setParameter("companyIds", companyIds);
            companies = companyQuery.getResultList();
        } else {
            companies = new ArrayList<Company>();
        }
        TypedQuery<Long> query = em.createQuery("select f.company.id from Following f where f.user.username = :userName and f.company is not null", Long.class);
        query.setParameter("userName", userName);
        List<Long> followings = query.getResultList();
        em.close();
        // make sure order has stayed the same
        List<Company> sortedItems = new ArrayList<Company>();
        for(final Long id : companyIds) {
            sortedItems.add(ListUtil.findValue(companies, value -> value.getId().equals(id)));
        }
        return ListUtil.mutate(sortedItems, new ListUtil.Mutate<Company, CompanyDTO>() {
            @Override
            public CompanyDTO mutate(Company company) {
                CompanyDTO companyDTO = CompanyHelper.createCompanyDTO(company);
                companyDTO.setFollowing(followings.contains(company.getId()));
                return companyDTO;
            }
        });
    }

    @Override
    public CSWGetRecordsResponse getRecordsResponse(CSWGetRecordsRequestDTO requestDTO) throws RequestException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(requestDTO.getUri());
        StringEntity params = null;
        String encoding = "UTF-8";
        try {
            params = new StringEntity(CSWUtils.getRequestData(requestDTO.getText(), requestDTO.getExtent()), encoding);
            params.setContentType("application/xml");
            httpPost.setEntity(params);
            HttpResponse response = httpClient.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();
            if(responseCode == 200 || responseCode == 204) {
                String responseValue = EntityUtils.toString(response.getEntity(), encoding);
                CSWGetRecordsResponse records = new CSWGetRecordsResponse();
                try {
                    Document doc = XMLUtil.getDocument(responseValue);
                    Node rootNode = doc.getDocumentElement();
                    Node searchResultsNode = XMLUtil.getUniqueNode((Element) rootNode, "csw:SearchResults");
                    List<CSWBriefRecord> briefRecords = new ArrayList<CSWBriefRecord>();
                    for(Node node : XMLUtil.getNodes(searchResultsNode, "csw:BriefRecord")) {
                        CSWBriefRecord briefRecord = new CSWBriefRecord();
                        briefRecord.setId(XMLUtil.getUniqueValue(node, "dc:identifier"));
                        briefRecord.setTitle(XMLUtil.getUniqueValue(node, "dc:title"));
                        briefRecord.setType(CSWRecordType.valueOf(XMLUtil.getUniqueValue(node, "dc:type")));
                        String[] ll = XMLUtil.getUniqueValue(node, "ows:LowerCorner").split(" ");
                        String[] ur = XMLUtil.getUniqueValue(node, "ows:UpperCorner").split(" ");
                        Extent extent = new Extent();
                        extent.setNorth(Double.parseDouble(ur[1]));
/*
                        extent.setWest(((Double.parseDouble(ur[0]) % 180) + 180.0) % 180.0);
*/
                        extent.setWest(Double.parseDouble(ur[0]));
                        extent.setSouth(Double.parseDouble(ll[1]));
                        extent.setEast(Double.parseDouble(ll[0]));
                        briefRecord.setExtent(extent);
                        briefRecords.add(briefRecord);
                    }
                    records.setRecords(briefRecords);
                    records.setNextRecord(Integer.parseInt(((Element) searchResultsNode).getAttribute("nextRecord")));
                    records.setNumberOfRecordsMatched(Integer.parseInt(((Element) searchResultsNode).getAttribute("numberOfRecordsMatched")));
                    records.setNumberOfRecordsReturned(Integer.parseInt(((Element) searchResultsNode).getAttribute("numberOfRecordsReturned")));
                    return records;
                } catch (Exception e) {
                    throw new RequestException("Error parsing response");
                }

            }
            else{
                logger.error(response.getStatusLine().getStatusCode());

                throw new RequestException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RequestException(e instanceof RequestException ? e.getMessage() : "Server error");
        } finally {

        }
    }

    @Override
    public OSQueryResponse getOSQueryResponse(OSQueryRequest requestDTO) throws RequestException {
        return null;
    }

    // TODO - replace by product dataset id?
    @Override
    public OSDescriptionResponse getOSDescriptionResponse(String requestUrl) throws Exception {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(requestUrl);
        StringEntity params = null;
        String encoding = "UTF-8";
        try {
            HttpResponse response = httpClient.execute(httpGet);
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == 200 || responseCode == 204) {
                String responseValue = EntityUtils.toString(response.getEntity(), encoding);
                OSDescriptionResponse osDescriptionResponse = new OSDescriptionResponse();
                Document doc = XMLUtil.getDocument(responseValue);
                Node rootNode = doc.getDocumentElement();
                Node searchResultsNode = XMLUtil.getUniqueNode((Element) rootNode, "OpenSearchDescription");
                List<CSWBriefRecord> briefRecords = new ArrayList<CSWBriefRecord>();
                for(Node node : XMLUtil.getNodes(searchResultsNode, "csw:BriefRecord")) {

                }
                return osDescriptionResponse;
            } else {
                throw new RequestException("Failed to query open search service description");
            }
        } catch (Exception e) {
            throw e instanceof RequestException ? e : new RequestException("Failed to query open search service description");
        }
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
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", payload.length() + "");
            // check whether we have some user password in the URL
            String userInfo = targetURL.getUserInfo();
            if(userInfo != null && userInfo.length() > 0) {
                String encoded = Base64.getEncoder().encodeToString(userInfo.getBytes(StandardCharsets.UTF_8));
                connection.setRequestProperty("Authorization", "Basic "+encoded);
            }
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
