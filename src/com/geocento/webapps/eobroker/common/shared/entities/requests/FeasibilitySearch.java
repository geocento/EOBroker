package com.geocento.webapps.eobroker.common.shared.entities.requests;

import com.geocento.webapps.eobroker.common.server.Utils.GeometryConverter;
import com.geocento.webapps.eobroker.common.shared.entities.ProductService;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 14/11/2017.
 */
@Entity
public class FeasibilitySearch {

    @Id
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    ProductService productService;

    @Convert(converter = GeometryConverter.class)
    String selectionGeometry;

    @ElementCollection
    List<FormElementValue> formValues;

    public FeasibilitySearch() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProductService getProductService() {
        return productService;
    }

    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    public String getSelectionGeometry() {
        return selectionGeometry;
    }

    public void setSelectionGeometry(String geometry) {
        this.selectionGeometry = geometry;
    }

    public List<FormElementValue> getFormValues() {
        return formValues;
    }

    public void setFormValues(List<FormElementValue> formValues) {
        this.formValues = formValues;
    }
}
