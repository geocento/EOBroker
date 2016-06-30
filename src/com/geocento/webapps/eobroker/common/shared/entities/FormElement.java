package com.geocento.webapps.eobroker.common.shared.entities;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import javax.persistence.*;

/**
 * Created by thomas on 23/06/2016.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@JsonSubTypes({@JsonSubTypes.Type(value=AoIFormElement.class, name="AoI"), @JsonSubTypes.Type(value = TextFormElement.class, name="TEXT")})
@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class FormElement {

    @Id
    @GeneratedValue
    Long id;

    @Column(length = 100)
    String name;
    @Column(length = 100)
    String formid;
    @Column(length = 1000)
    String description;

    public FormElement() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
