package com.geocento.webapps.eobroker.customer.shared.utils;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.geocento.webapps.eobroker.common.shared.entities.Product;
import com.geocento.webapps.eobroker.common.shared.entities.ProductService;
import com.geocento.webapps.eobroker.customer.shared.ProductDTO;

import java.util.List;

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

    public static boolean hasWMSSamples(List<DatasetAccess> samples) {
        if(samples != null && samples.size() > 0) {
            for(DatasetAccess datasetAccess : samples) {
                if(datasetAccess instanceof DatasetAccessOGC) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasSamples(List<DatasetAccess> samples) {
        return samples != null && samples.size() > 0;
    }
}
