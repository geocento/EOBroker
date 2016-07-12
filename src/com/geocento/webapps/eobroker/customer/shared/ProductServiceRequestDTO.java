package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import java.util.List;

/**
 * Created by thomas on 07/07/2016.
 */
public class ProductServiceRequestDTO {

    Long productId;
    List<Long> productServiceIds;
    List<FormElementValue> values;

    public ProductServiceRequestDTO() {
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public List<Long> getProductServiceIds() {
        return productServiceIds;
    }

    public void setProductServiceIds(List<Long> productServiceIds) {
        this.productServiceIds = productServiceIds;
    }

    public List<FormElementValue> getValues() {
        return values;
    }

    public void setValues(List<FormElementValue> values) {
        this.values = values;
    }
}
