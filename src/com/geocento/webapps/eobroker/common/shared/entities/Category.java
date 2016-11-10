package com.geocento.webapps.eobroker.common.shared.entities;

public enum Category {

    products("Products"),
    productservices("On-demand services"),
    productdatasets("Off-the-shelf data"),
    datasets("Datasets"),
    software("Software solutions"),
    project("Projects"),
    companies("Companies"),
    imagery("Imagery");

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
