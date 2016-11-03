package com.geocento.webapps.eobroker.customer.shared;

import java.util.List;

/**
 * Created by thomas on 09/06/2016.
 */
public class SearchResult {

    List<ProductDTO> products;
    List<ProductServiceDTO> productServices;
    private List<DatasetProviderDTO> datasetsProviders;

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

    public List<DatasetProviderDTO> getDatasetsProviders() {
        return datasetsProviders;
    }

    public void setDatasetsProviders(List<DatasetProviderDTO> datasetsProviders) {
        this.datasetsProviders = datasetsProviders;
    }
}
