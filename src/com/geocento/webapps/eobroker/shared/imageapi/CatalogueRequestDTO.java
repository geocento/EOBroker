package com.geocento.webapps.eobroker.shared.imageapi;

/**
 * Created by thomas on 08/03/2016.
 */
public class CatalogueRequestDTO {

    private String productId;
    private SELECTION selectionType;
    private String selectionGeometry;

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public SELECTION getSelectionType() {
        return selectionType;
    }

    public void setSelectionType(SELECTION selectionType) {
        this.selectionType = selectionType;
    }

    public String getSelectionGeometry() {
        return selectionGeometry;
    }

    public void setSelectionGeometry(String selectionGeometry) {
        this.selectionGeometry = selectionGeometry;
    }
}
