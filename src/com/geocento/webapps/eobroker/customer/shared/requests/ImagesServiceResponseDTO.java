package com.geocento.webapps.eobroker.customer.shared.requests;

import com.geocento.webapps.eobroker.common.shared.imageapi.Product;

import java.util.List;

/**
 * Created by thomas on 07/07/2016.
 */
public class ImagesServiceResponseDTO extends BaseResponseDTO {

    String id;
    String aoiWKT;
    List<Product> products;

    public ImagesServiceResponseDTO() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
