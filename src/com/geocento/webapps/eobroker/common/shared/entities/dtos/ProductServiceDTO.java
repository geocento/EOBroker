package com.geocento.webapps.eobroker.common.shared.entities.dtos;

/**
 * Created by thomas on 06/06/2016.
 */
public class ProductServiceDTO {

    Long id;
    String name;
    String description;
    String serviceImage;
    ProductDTO product;
    Long companyId;
    String companyName;
    String companyLogo;
    boolean hasFeasibility;

    public ProductServiceDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyLogo() {
        return companyLogo;
    }

    public void setCompanyLogo(String companyLogo) {
        this.companyLogo = companyLogo;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public boolean isHasFeasibility() {
        return hasFeasibility;
    }

    public void setHasFeasibility(boolean hasFeasibility) {
        this.hasFeasibility = hasFeasibility;
    }
}
