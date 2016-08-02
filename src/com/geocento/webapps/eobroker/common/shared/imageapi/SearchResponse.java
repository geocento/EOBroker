package com.geocento.webapps.eobroker.common.shared.imageapi;

import java.util.List;

/**
 *
 * the search response returned when submitting a search request
 */
public class SearchResponse extends StatusResponse {

    String id;
    List<Product> products;

    public SearchResponse() {
    }

    /**
     *
     * the id of the saved search request
     *
     * @return
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * the list of products found
     *
     * @return
     */
    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

}
