package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductSoftwareDTO;

/**
 * Created by thomas on 08/11/2016.
 */
public class ProductSoftwarePitch extends ProductPitchWidget {

    private ProductSoftwareDTO productSoftwareDTO;

    public ProductSoftwarePitch() {
    }

    public void setProductSoftware(ProductSoftwareDTO productSoftwareDTO) {
        this.productSoftwareDTO = productSoftwareDTO;
        setProduct(productSoftwareDTO.getProduct());
        setPitch(productSoftwareDTO.getPitch());
    }

    public ProductSoftwareDTO getProductSoftwareDTO() {
        // update values
        productSoftwareDTO.setProduct(getProduct());
        productSoftwareDTO.setPitch(getPitch());
        return productSoftwareDTO;
    }

}
