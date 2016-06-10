package com.geocento.webapps.eobroker.server.servlets;

import com.geocento.webapps.eobroker.client.services.SearchService;
import com.geocento.webapps.eobroker.server.EMF;
import com.geocento.webapps.eobroker.shared.imageapi.SearchRequest;
import com.geocento.webapps.eobroker.shared.Suggestion;
import com.geocento.webapps.eobroker.shared.entities.Category;
import com.geocento.webapps.eobroker.shared.entities.Product;
import com.geocento.webapps.eobroker.shared.entities.SearchResult;
import com.geocento.webapps.eobroker.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.shared.entities.dtos.ProductServiceDTO;
import com.geocento.webapps.eobroker.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.shared.utils.StringUtils;
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
        List<ProductDTO> products = ListUtil.mutate(productQuery.getResultList(), new ListUtil.Mutate<Product, ProductDTO>() {
            @Override
            public ProductDTO mutate(Product product) {
                return createProductDTO(product);
            }
        });
        List<ProductServiceDTO> productServices = new ArrayList<ProductServiceDTO>();
        for(ProductDTO product : products) {
            ProductServiceDTO productServiceDTO = createProductServiceDTO();
            productServices.addAll(ListUtil.toList(new ProductServiceDTO[]{
                    productServiceDTO
            }));
        }
        searchResult.setProducts(products);
        searchResult.setProductServices(productServices);
        // now get the product suppliers for each one of them

        return searchResult;
    }

    @Override
    public SearchResult getMatchingServicesForProduct(Long productId, Long aoiId) throws RequestException {
        SearchResult searchResult = new SearchResult();
        EntityManager em = EMF.get().createEntityManager();
        Product product = em.find(Product.class, productId);
        ProductDTO productDTO = createProductDTO(product);
        // add product services
        List<ProductServiceDTO> productServices = new ArrayList<ProductServiceDTO>();
        ProductServiceDTO productServiceDTO = createProductServiceDTO();
        productServices.addAll(ListUtil.toList(new ProductServiceDTO[]{
                productServiceDTO,
                productServiceDTO,
                productServiceDTO
        }));
        searchResult.setProducts(ListUtil.toList(productDTO));
        searchResult.setProductServices(productServices);
        // now get the product suppliers for each one of them

        return searchResult;
    }

    private ProductServiceDTO createProductServiceDTO() {
        ProductServiceDTO productServiceDTO = new ProductServiceDTO();
        productServiceDTO.setName("Habitat assessment and monitoring");
        productServiceDTO.setDescription("Habitat assessment and monitoring is a requirement in many jurisdictions under legislation such as international (PS 6), national or state environmental assessment acts. It can cover diverse environments and habitats depending on a specific locations and applications, including: terrestrial, freshwater aquatic and coastal area. EO derived information support the mapping and monitoring of critical habitat and especially when in-situ data are being integrated.");
        productServiceDTO.setServiceImage("images/criticalhabitats.png");
        productServiceDTO.setCompanyLogo("images/eomapLogo.png");
        return productServiceDTO;
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
                return createProductDTO(product);
            }
        });
        searchResult.setProducts(productDTOs);

        return searchResult;
    }

    @Override
    public List<ProductDTO> query(SearchRequest searchRequest) throws RequestException {
        return null;
    }

    private ProductDTO createProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getDescription());
        productDTO.setImageUrl(getImage());
        return productDTO;
    }

    private String getImage() {
        String[] images = new String[]{
                "https://www3.epa.gov/climatechange/images/basics/factorysmoke.jpg",
                "http://www.nasa.gov/sites/default/files/styles/full_width/public/thumbnails/image/15-115.jpg?itok=-S4q6bvE",
                "https://www3.epa.gov/climatechange/kids/images/scientists-clues-print.jpg",
                "http://a.fastcompany.net/multisite_files/coexist/imagecache/1280/poster/2012/11/1680998-poster-1280-how-to-profit-from-climate-change.jpg",
                "http://www.unep.org/climatechange/Portals/5/images/CC-New/main_banner.jpg"
        };
        return images[((int) Math.floor(Math.random() * images.length))];
    }

}
