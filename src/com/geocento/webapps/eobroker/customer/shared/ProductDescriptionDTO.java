package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.Sector;
import com.geocento.webapps.eobroker.common.shared.entities.Thematic;

import java.util.List;

/**
 * Created by thomas on 23/06/2016.
 */
public class ProductDescriptionDTO {

    Long id;
    String name;
    String imageUrl;
    String shortDescription;
    String description;
    Sector sector;
    Thematic thematic;
    boolean following;
    int followers;
    List<ProductServiceDTO> productServices;
    List<ProductDatasetDTO> productDatasets;
    boolean imageRule;
    List<SoftwareDTO> softwares;
    List<ProjectDTO> projects;
    List<ProductDTO> suggestedProducts;
    private List<SuccessStoryDTO> successStories;

    public ProductDescriptionDTO() {
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Sector getSector() {
        return sector;
    }

    public void setSector(Sector sector) {
        this.sector = sector;
    }

    public Thematic getThematic() {
        return thematic;
    }

    public void setThematic(Thematic thematic) {
        this.thematic = thematic;
    }

    public void setFollowing(boolean following) {
        this.following = following;
    }

    public boolean getFollowing() {
        return following;
    }

    public boolean isFollowing() {
        return following;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public void setProductServices(List<ProductServiceDTO> productServices) {
        this.productServices = productServices;
    }

    public List<ProductServiceDTO> getProductServices() {
        return productServices;
    }

    public boolean hasImageRule() {
        return imageRule;
    }

    public List<ProductDatasetDTO> getProductDatasets() {
        return productDatasets;
    }

    public void setProductDatasets(List<ProductDatasetDTO> productDatasets) {
        this.productDatasets = productDatasets;
    }

    public List<SoftwareDTO> getSoftwares() {
        return softwares;
    }

    public void setSoftwares(List<SoftwareDTO> softwares) {
        this.softwares = softwares;
    }

    public List<ProjectDTO> getProjects() {
        return projects;
    }

    public void setProjects(List<ProjectDTO> projects) {
        this.projects = projects;
    }

    public List<ProductDTO> getSuggestedProducts() {
        return suggestedProducts;
    }

    public void setSuggestedProducts(List<ProductDTO> suggestedProducts) {
        this.suggestedProducts = suggestedProducts;
    }

    public List<SuccessStoryDTO> getSuccessStories() {
        return successStories;
    }

    public void setSuccessStories(List<SuccessStoryDTO> successStories) {
        this.successStories = successStories;
    }
}
