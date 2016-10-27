package com.geocento.webapps.eobroker.common.shared.entities.datasets;

import com.geocento.webapps.eobroker.common.shared.entities.Extent;

import java.io.Serializable;

/**
 * Created by thomas on 07/10/2016.
 */
public class CSWBriefRecord implements Serializable {

    String id;
    CSWRecordType type;
    String title;
    String description;
    Extent extent;

    public CSWBriefRecord() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CSWRecordType getType() {
        return type;
    }

    public void setType(CSWRecordType type) {
        this.type = type;
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

    public Extent getExtent() {
        return extent;
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
    }
}
