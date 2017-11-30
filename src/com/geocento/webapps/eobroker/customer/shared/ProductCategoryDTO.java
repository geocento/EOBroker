package com.geocento.webapps.eobroker.customer.shared;

import java.util.List;

/**
 * Created by thomas on 30/11/2017.
 */
public class ProductCategoryDTO {

    String name;
    List<String> tags;

    public ProductCategoryDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
