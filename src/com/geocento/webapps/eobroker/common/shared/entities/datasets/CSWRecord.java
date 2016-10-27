package com.geocento.webapps.eobroker.common.shared.entities.datasets;

import com.geocento.webapps.eobroker.common.shared.entities.Extent;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 07/10/2016.
 */
public class CSWRecord {

    String id;
    CSWRecordType type;
    String title;
    String description;
    List<String> keywords;
    List<AccessScheme> schemes;
    Date date;
    String language;
    String rights;
    Extent boundaries;

    public CSWRecord() {
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
}
