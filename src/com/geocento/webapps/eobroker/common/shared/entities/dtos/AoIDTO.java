package com.geocento.webapps.eobroker.common.shared.entities.dtos;

/**
 * Created by thomas on 03/06/2016.
 */
public class AoIDTO {

    Long id;
    String name;
    String wktGeometry;

    public AoIDTO() {
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

    public String getWktGeometry() {
        return wktGeometry;
    }

    public void setWktGeometry(String wktGeometry) {
        this.wktGeometry = wktGeometry;
    }
}
