package com.geocento.webapps.eobroker.customer.shared;

/**
 * Created by thomas on 23/11/2017.
 */
public class ChallengeDTO {

    Long id;
    String name;
    String shortDescription;
    private String imageUrl;
    private int numberProducts;

    public ChallengeDTO() {
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setNumberProducts(int numberProducts) {
        this.numberProducts = numberProducts;
    }

    public int getNumberProducts() {
        return numberProducts;
    }
}
