package com.geocento.webapps.eobroker.shared;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.geocento.webapps.eobroker.shared.entities.Category;

public class Suggestion {

    private String name;
    private Category category;
    private String uri;

    @JsonCreator
    public Suggestion(@JsonProperty("name") String name, @JsonProperty("category") Category category, @JsonProperty("uri") String uri) {
        this.name = name;
        this.category = category;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
