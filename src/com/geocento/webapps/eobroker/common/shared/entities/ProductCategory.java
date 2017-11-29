package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;
import java.util.List;

/**
 * Created by thomas on 29/11/2017.
 */
@Entity
public class ProductCategory {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 100)
    String name;

    @ElementCollection
    List<String> tags;

    public ProductCategory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
