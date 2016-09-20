package com.geocento.webapps.eobroker.customer.shared.requests;

/**
 * Created by thomas on 11/07/2016.
 */
public class ImagerySupplierResponseDTO extends BaseResponseDTO {

    private Long id;

    public ImagerySupplierResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
