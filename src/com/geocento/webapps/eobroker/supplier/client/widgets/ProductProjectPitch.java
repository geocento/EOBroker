package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductProjectDTO;

/**
 * Created by thomas on 08/11/2016.
 */
public class ProductProjectPitch extends ProductPitchWidget {

    private ProductProjectDTO productProjectDTO;

    public ProductProjectPitch() {
    }

    public void setProductProject(ProductProjectDTO productProjectDTO) {
        this.productProjectDTO = productProjectDTO;
        setProduct(productProjectDTO.getProduct());
        setPitch(productProjectDTO.getPitch());
    }

    public ProductProjectDTO getProductProjectDTO() {
        // update values
        productProjectDTO.setProduct(getProduct());
        productProjectDTO.setPitch(getPitch());
        return productProjectDTO;
    }

}
