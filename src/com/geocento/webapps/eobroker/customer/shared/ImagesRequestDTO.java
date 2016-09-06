package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.imageapi.Product;

import java.util.List;

/**
 * Created by thomas on 29/08/2016.
 */
public class ImagesRequestDTO {

    Long imageServiceId;
    String aoiWKT;
    List<Product> products;

    public ImagesRequestDTO() {
    }

    public Long getImageServiceId() {
        return imageServiceId;
    }

    public void setImageServiceId(Long imageServiceId) {
        this.imageServiceId = imageServiceId;
    }

    public String getAoiWKT() {
        return aoiWKT;
    }

    public void setAoiWKT(String aoiWKT) {
        this.aoiWKT = aoiWKT;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
