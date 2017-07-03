package com.geocento.webapps.eobroker.customer.shared;

/**
 * Created by thomas on 03/07/2017.
 */
public class CreateCompanyDTO {

    String name;
    String description;
    String fullDescription;
    String address;
    String countryCode;
    boolean isSupplier;

    public CreateCompanyDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isSupplier() {
        return isSupplier;
    }

    public void setSupplier(boolean isSupplier) {
        this.isSupplier = isSupplier;
    }
}
