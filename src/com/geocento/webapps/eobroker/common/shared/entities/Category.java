package com.geocento.webapps.eobroker.common.shared.entities;

public enum Category {

    imagery("Imagery"),
    products("Analytics"),
    companies("Companies"),
    datasets("Datasets"),
    software("Software");

    private String name;

    Category() {
    }

    private Category(String s) {
            name = s;
        }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
