package com.geocento.webapps.eobroker.customer.shared;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by thomas on 28/10/2016.
 */
@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY, property="@class")
@JsonSubTypes({
    @JsonSubTypes.Type(value=ProductDTO.class, name="product"),
    @JsonSubTypes.Type(value=ProductServiceDTO.class, name="productservice"),
    @JsonSubTypes.Type(value=ProductDatasetDTO.class, name="productdataset"),
    @JsonSubTypes.Type(value=SoftwareDTO.class, name="software"),
    @JsonSubTypes.Type(value=ProjectDTO.class, name="project"),
})
public class Offer {
}
