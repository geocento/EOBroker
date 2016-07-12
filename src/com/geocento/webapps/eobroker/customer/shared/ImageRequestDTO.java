package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import java.util.List;

/**
 * Created by thomas on 07/07/2016.
 */
public class ImageRequestDTO {

    List<Long> supplierIds;
    List<FormElementValue> values;

    public ImageRequestDTO() {
    }

    public List<Long> getSupplierIds() {
        return supplierIds;
    }

    public void setSupplierIds(List<Long> supplierIds) {
        this.supplierIds = supplierIds;
    }

    public List<FormElementValue> getValues() {
        return values;
    }

    public void setValues(List<FormElementValue> values) {
        this.values = values;
    }
}
