package com.geocento.webapps.eobroker.shared.entities;

import com.geocento.webapps.eobroker.shared.entities.dtos.ProductDTO;
import com.geocento.webapps.eobroker.shared.entities.dtos.ProductServiceDTO;

import java.util.List;

/**
 * Created by thomas on 09/06/2016.
 */
public class SearchResult {

    List<ProductDTO> products;
    List<ProductServiceDTO> productServices;

    public SearchResult() {
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }

    public List<ProductServiceDTO> getProductServices() {
        return productServices;
    }

    public void setProductServices(List<ProductServiceDTO> productServices) {
        this.productServices = productServices;
    }
}
