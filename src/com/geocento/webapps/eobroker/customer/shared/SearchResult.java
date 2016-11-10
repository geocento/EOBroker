package com.geocento.webapps.eobroker.customer.shared;

import java.util.List;

/**
 * Created by thomas on 09/06/2016.
 */
public class SearchResult {

    List<ProductDTO> products;
    List<ProductServiceDTO> productServices;
    List<ProductDatasetDTO> productDatasets;
    List<SoftwareDTO> softwares;
    List<ProjectDTO> projects;
    List<DatasetProviderDTO> datasetsProviders;
    private boolean moreProducts;
    private boolean moreProductServices;
    private boolean moreProductDatasets;
    private boolean moreSoftware;
    private boolean moreProjects;

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

    public List<ProductDatasetDTO> getProductDatasets() {
        return productDatasets;
    }

    public void setProductDatasets(List<ProductDatasetDTO> productDatasets) {
        this.productDatasets = productDatasets;
    }

    public List<SoftwareDTO> getSoftwares() {
        return softwares;
    }

    public void setSoftwares(List<SoftwareDTO> softwares) {
        this.softwares = softwares;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
    }

    public List<DatasetProviderDTO> getDatasetsProviders() {
        return datasetsProviders;
    }

    public void setDatasetsProviders(List<DatasetProviderDTO> datasetsProviders) {
        this.datasetsProviders = datasetsProviders;
    }

    public boolean isMoreProducts() {
        return moreProducts;
    }

    public void setMoreProducts(boolean moreProducts) {
        this.moreProducts = moreProducts;
    }

    public boolean isMoreProductServices() {
        return moreProductServices;
    }

    public void setMoreProductServices(boolean moreProductServices) {
        this.moreProductServices = moreProductServices;
    }

    public boolean isMoreProductDatasets() {
        return moreProductDatasets;
    }

    public void setMoreProductDatasets(boolean moreProductDatasets) {
        this.moreProductDatasets = moreProductDatasets;
    }

    public boolean isMoreSoftware() {
        return moreSoftware;
    }

    public void setMoreSoftware(boolean moreSoftware) {
        this.moreSoftware = moreSoftware;
    }

    public boolean isMoreProjects() {
        return moreProjects;
    }

    public void setMoreProjects(boolean moreProjects) {
        this.moreProjects = moreProjects;
    }
}
