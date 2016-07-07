package com.geocento.webapps.eobroker.common.shared.entities.formelements;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Created by thomas on 23/06/2016.
 */
@Embeddable
public class FormElementValue {

    @Column(length = 100)
    String formid;
    @Column(length = 100)
    String name;
    @Column(length = 10000)
    String value;

    public FormElementValue() {
    }

    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
