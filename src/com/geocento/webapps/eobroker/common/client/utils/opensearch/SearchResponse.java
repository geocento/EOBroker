package com.geocento.webapps.eobroker.common.client.utils.opensearch;

import java.util.List;

/**
 * Created by thomas on 24/05/2017.
 */
public class SearchResponse {

    int totalRecords;
    int start;
    int limit;
    List<Record> records;

    public SearchResponse() {
    }

    public int getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(int totalRecords) {
        this.totalRecords = totalRecords;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public List<Record> getRecords() {
        return records;
    }

    public void setRecords(List<Record> records) {
        this.records = records;
    }
}
