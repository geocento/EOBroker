package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.*;

/**
 * Created by thomas on 10/11/2016.
 */
@Entity
public class DatasetAccess {

    @Id
    @GeneratedValue
    Long id;

    @Enumerated(EnumType.STRING)
    AccessType accessType;

    @Column(length = 1000)
    String uri;

    @Column(length = 1000)
    String pitch;

    public DatasetAccess() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }
}
