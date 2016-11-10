package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;

/**
 * Created by thomas on 07/11/2016.
 */
public class ProductProjectDTO {

    Long id;
    String pitch;
    com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO product;

    public ProductProjectDTO() {
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

    public com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }
}
