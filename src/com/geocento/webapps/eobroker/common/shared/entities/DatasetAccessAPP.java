package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Created by thomas on 10/11/2016.
 */
@Entity
@DiscriminatorValue("APP")
public class DatasetAccessAPP extends DatasetAccess {

    public DatasetAccessAPP() {
    }

}
