package com.geocento.webapps.eobroker.server.servlets;

import com.geocento.webapps.eobroker.client.services.SearchService;
import com.geocento.webapps.eobroker.server.EMF;
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
            suggestions.add(new RankedSuggestion((String) result[0], Category.products, ((Long) result[2]) + "", (double) ((float) result[1])));
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
                ProductDTO productDTO = new ProductDTO();
                productDTO.setName(product.getName());
                productDTO.setDescription(product.getDescription());
                productDTO.setImageUrl("http://www.nasa.gov/sites/default/files/styles/full_width/public/thumbnails/image/15-115.jpg?itok=-S4q6bvE");
                return productDTO;
            }
        });
        List<ProductServiceDTO> productServices = new ArrayList<ProductServiceDTO>();
        for(ProductDTO product : products) {
            ProductServiceDTO productServiceDTO = new ProductServiceDTO();
            productServiceDTO.setName("Habitat assessment and monitoring");
            productServiceDTO.setDescription("Habitat assessment and monitoring is a requirement in many jurisdictions under legislation such as international (PS 6), national or state environmental assessment acts. It can cover diverse environments and habitats depending on a specific locations and applications, including: terrestrial, freshwater aquatic and coastal area. EO derived information support the mapping and monitoring of critical habitat and especially when in-situ data are being integrated.");
            productServiceDTO.setCompanyLogo("images/eomapLogo.png");
            productServices.addAll(ListUtil.toList(new ProductServiceDTO[]{

            }));
        }
        searchResult.setProducts(products);
        searchResult.setProductServices(productServices);
        // now get the product suppliers for each one of them

        return searchResult;
    }

}
