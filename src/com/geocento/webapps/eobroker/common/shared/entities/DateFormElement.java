package com.geocento.webapps.eobroker.common.shared.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by thomas on 23/06/2016.
 */
@Entity
@DiscriminatorValue("DATE")
public class DateFormElement extends FormElement {

    @Temporal(TemporalType.TIMESTAMP)
    Date minDate;

    @Temporal(TemporalType.TIMESTAMP)
    Date maxDate;
}
