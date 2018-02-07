package com.geocento.webapps.eobroker.admin.shared.dtos;

import java.util.HashMap;

/**
 * Created by thomas on 14/06/2017.
 */
public class AdminStatisticsDTO {

    HashMap<String, String> usersStatistics;
    HashMap<String, Double> usersPerCountry;

    HashMap<String, String> companyStatistics;
    HashMap<String, Double> companyFollowers;

    HashMap<String, String> supplierStatistics;
    HashMap<String, Double> suppliersPerCountry;

    HashMap<String, String> offeringStatistics;

    HashMap<String, String> productsStatistics;
    HashMap<String, Double> productFollowers;

    public AdminStatisticsDTO() {
    }

    public HashMap<String, String> getUsersStatistics() {
        return usersStatistics;
    }

    public void setUsersStatistics(HashMap<String, String> usersStatistics) {
        this.usersStatistics = usersStatistics;
    }

    public HashMap<String, Double> getUsersPerCountry() {
        return usersPerCountry;
    }

    public void setUsersPerCountry(HashMap<String, Double> usersPerCountry) {
        this.usersPerCountry = usersPerCountry;
    }

    public HashMap<String, String> getCompanyStatistics() {
        return companyStatistics;
    }

    public void setCompanyStatistics(HashMap<String, String> companyStatistics) {
        this.companyStatistics = companyStatistics;
    }

    public HashMap<String, Double> getCompanyFollowers() {
        return companyFollowers;
    }

    public void setCompanyFollowers(HashMap<String, Double> companyFollowers) {
        this.companyFollowers = companyFollowers;
    }

    public HashMap<String, String> getSupplierStatistics() {
        return supplierStatistics;
    }

    public void setSupplierStatistics(HashMap<String, String> supplierStatistics) {
        this.supplierStatistics = supplierStatistics;
    }

    public HashMap<String, Double> getSuppliersPerCountry() {
        return suppliersPerCountry;
    }

    public void setSuppliersPerCountry(HashMap<String, Double> suppliersPerCountry) {
        this.suppliersPerCountry = suppliersPerCountry;
    }

    public HashMap<String, String> getOfferingStatistics() {
        return offeringStatistics;
    }

    public void setOfferingStatistics(HashMap<String, String> offeringStatistics) {
        this.offeringStatistics = offeringStatistics;
    }

    public HashMap<String, String> getProductsStatistics() {
        return productsStatistics;
    }

    public void setProductsStatistics(HashMap<String, String> productsStatistics) {
        this.productsStatistics = productsStatistics;
    }

    public HashMap<String, Double> getProductFollowers() {
        return productFollowers;
    }

    public void setProductFollowers(HashMap<String, Double> productFollowers) {
        this.productFollowers = productFollowers;
    }
}
