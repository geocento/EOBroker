package com.geocento.webapps.eobroker.customer.shared.requests;

/**
 * Created by thomas on 16/09/2016.
 */
public class ProductServiceSupplierResponseDTO extends BaseResponseDTO {

    Long id;

    public ProductServiceSupplierResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
