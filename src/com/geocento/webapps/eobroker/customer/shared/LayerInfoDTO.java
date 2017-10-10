package com.geocento.webapps.eobroker.customer.shared;

import com.geocento.webapps.eobroker.common.shared.entities.Extent;

import java.util.List;

/**
 * Created by thomas on 22/11/2016.
 */
public class LayerInfoDTO {

    String name;
    String title;
    String description;
    List<String> keywordList;
    List<String> crs;
    Extent extent;
    boolean queryable;
    private String serverUrl;
    private String layerName;
    private String styleName;
    private String version;
    boolean savedLayer;

    public LayerInfoDTO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getKeywordList() {
        return keywordList;
    }

    public void setKeywordList(List<String> keywordList) {
        this.keywordList = keywordList;
    }

    public List<String> getCrs() {
        return crs;
    }

    public void setCrs(List<String> crs) {
        this.crs = crs;
    }

    public Extent getExtent() {
        return extent;
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    public boolean isQueryable() {
        return queryable;
    }

    public void setQueryable(boolean queryable) {
        this.queryable = queryable;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }

    public String getLayerName() {
        return layerName;
    }

    public String getStyleName() {
        return styleName;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public boolean isSavedLayer() {
        return savedLayer;
    }

    public void setSavedLayer(boolean savedLayer) {
        this.savedLayer = savedLayer;
    }
}
