package com.geocento.webapps.eobroker.customer.shared;

/**
 * Created by thomas on 07/11/2016.
 */
public class ProductSoftwareDTO {

    Long id;
    String pitch;
    ProductDTO product;

    public ProductSoftwareDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }
}
