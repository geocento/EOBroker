package com.geocento.webapps.eobroker.customer.shared.utils;

import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.customer.shared.ProductDTO;

/**
 * Created by thomas on 08/06/2016.
 */
public class ProductHelper {

    public static ProductDTO createProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setShortDescription(product.getShortDescription());
        productDTO.setImageUrl(product.getImageUrl());
        productDTO.setSector(product.getSector());
        productDTO.setThematic(product.getThematic());
        productDTO.setFollowers(product.getFollowers() == null ? 0 : product.getFollowers().intValue());
        return productDTO;
    }

}
