package com.geocento.webapps.eobroker.common.shared.entities;

public enum Category {

    products("Product categories"),
    productservices("Bespoke services"),
    productdatasets("Off-the-shelf products"),
    datasets("Datasets"),
    software("Software solutions"),
    project("Projects"),
    companies("Companies"),
    imagery("Imagery"),
    newsItems;

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
