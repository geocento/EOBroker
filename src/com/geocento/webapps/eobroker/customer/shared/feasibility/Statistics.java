package com.geocento.webapps.eobroker.customer.shared.feasibility;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Created by thomas on 02/06/2017.
 */
@JsonSubTypes({
        @JsonSubTypes.Type(value=TimeStatistics.class, name="timeChart"),
        @JsonSubTypes.Type(value=TimeStatistics.class, name="heatmapChart"),
        @JsonSubTypes.Type(value=WMSStatistics.class, name="wmsChart"),
        @JsonSubTypes.Type(value=BarChartStatistics.class, name="barChart"),
        @JsonSubTypes.Type(value=PieChartStatistics.class, name="pieChart")
})
@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY, property="@class")
public class Statistics {

    String name;
    String description;

    public Statistics() {
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
