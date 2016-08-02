package com.geocento.webapps.eobroker.common.shared.imageapi;

/**
 *
 * NOT AVAILABLE YET - specifies the archive scene selection when ordering
 */
public class ProductQuery {

    String productId;
    Policy.SELECTION selectionType;
    String selectionGeometry;

    /**
     *
     * @return the product identifier
     */
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public Policy.SELECTION getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(Policy.SELECTION selectionType) {
        this.selectionType = selectionType;
    }

    public String getSelectionGeometry() {
        return selectionGeometry;
    }

    public void setSelectionGeometry(String selectionGeometry) {
        this.selectionGeometry = selectionGeometry;
    }
}
