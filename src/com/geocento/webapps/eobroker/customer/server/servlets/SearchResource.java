package com.geocento.webapps.eobroker.customer.server.servlets;

import com.geocento.webapps.eobroker.common.shared.entities.ProductService;
import com.geocento.webapps.eobroker.common.shared.entities.utils.ProductHelper;
import com.geocento.webapps.eobroker.customer.client.services.SearchService;
import com.geocento.webapps.eobroker.common.server.EMF;
import com.geocento.webapps.eobroker.customer.server.imageapi.EIAPIUtil;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.SearchResult;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.common.shared.imageapi.ImageProductDTO;
import com.geocento.webapps.eobroker.common.shared.imageapi.SearchRequest;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.common.shared.utils.StringUtils;
import com.google.gwt.http.client.RequestException;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.ws.rs.Path;
import java.util.*;

@Path("/")
public class SearchResource implements SearchService {

    public SearchResource() {
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
                return new Double(o1.rank).compareTo(new Double(o2.rank));
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
                "          ORDER BY rank\n" +
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
                "          ORDER BY rank\n" +
                "          LIMIT 10;";
        EntityManager em = EMF.get().createEntityManager();
        Query q = em.createNativeQuery(sqlStatement);
        List<Object[]> results = q.getResultList();
        List<Long> productIds = new ArrayList<Long>();
        for(Object[] result : results) {
            productIds.add((Long) result[0]);
        }
        TypedQuery<Product> productQuery = em.createQuery("select p from Product p where p.id IN :productIds", Product.class);
        productQuery.setParameter("productIds", productIds);
        List<Product> products = productQuery.getResultList();
        List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
        List<ProductServiceDTO> productServices = new ArrayList<ProductServiceDTO>();
        for(Product product : products) {
            ProductDTO productDTO = ProductHelper.createProductDTO(product);
            productDTOs.add(productDTO);
            for(ProductService productService : product.getProductServices()) {
                ProductServiceDTO productServiceDTO = new ProductServiceDTO();
                productServiceDTO.setName(productService.getName());
                productServiceDTO.setDescription(productService.getDescription());
                productServiceDTO.setServiceImage(productService.getImageUrl());
                productServiceDTO.setCompanyLogo(productService.getCompany().getIconURL());
                productServiceDTO.setCompanyName(productService.getCompany().getName());
                productServiceDTO.setCompanyId(productService.getCompany().getId());
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
    public SearchResult listProducts(String textFilter, Integer start, Integer limit, Long aoiId) throws RequestException {
        SearchResult searchResult = new SearchResult();
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
                    "          ORDER BY rank;";
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
        List<ProductDTO> productDTOs = ListUtil.mutate(products, new ListUtil.Mutate<Product, ProductDTO>() {
            @Override
            public ProductDTO mutate(Product product) {
                return ProductHelper.createProductDTO(product);
            }
        });
        searchResult.setProducts(productDTOs);

        return searchResult;
    }

    @Override
    public List<ImageProductDTO> queryImages(SearchRequest searchRequest) throws RequestException {
        try {
            return EIAPIUtil.queryProducts(searchRequest);
        } catch (Exception e) {
            throw new RequestException(e.getMessage());
        }
    }

}
