package com.geocento.webapps.eobroker.common.shared.entities.utils;

import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.ProductDTO;

/**
 * Created by thomas on 08/06/2016.
 */
public class ProductHelper {

    public static ProductDTO createProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setDescription(product.getShortDescription());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setSector(product.getSector());
        productDTO.setThematic(product.getThematic());
        return productDTO;
    }

}
