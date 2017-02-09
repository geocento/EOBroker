package com.geocento.webapps.eobroker.common.shared.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.*;

/**
 * Created by thomas on 10/11/2016.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonSubTypes({
        @JsonSubTypes.Type(value=DatasetAccessFile.class, name="FILE"),
        @JsonSubTypes.Type(value=DatasetAccessOGC.class, name="OGC"),
        @JsonSubTypes.Type(value=DatasetAccessAPP.class, name="APP"),
        @JsonSubTypes.Type(value=DatasetAccessAPI.class, name="API"),
})
@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY, property="@class")
public class DatasetAccess {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 100)
    String title;

    @Column(length = 1000)
    String pitch;

    @Column(length = 1000)
    String uri;

    boolean hostedData;

    public DatasetAccess() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public boolean isHostedData() {
        return hostedData;
    }

    public void setHostedData(boolean hostedData) {
        this.hostedData = hostedData;
    }
}
