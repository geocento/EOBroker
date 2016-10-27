package com.geocento.webapps.eobroker.common.shared.entities.datasets;

import java.io.Serializable;
import java.util.List;

/**
 * Created by thomas on 07/10/2016.
 */
public class CSWGetRecordsResponse implements Serializable {

    String status;
    List<CSWBriefRecord> records;
    int nextRecord;
    int numberOfRecordsMatched;
    int numberOfRecordsReturned;

    public CSWGetRecordsResponse() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<CSWBriefRecord> getRecords() {
        return records;
    }

    public void setRecords(List<CSWBriefRecord> records) {
        this.records = records;
    }

    public int getNumberOfRecordsMatched() {
        return numberOfRecordsMatched;
    }

    public void setNumberOfRecordsMatched(int numberOfRecordsMatched) {
        this.numberOfRecordsMatched = numberOfRecordsMatched;
    }

    public int getNextRecord() {
        return nextRecord;
    }

    public void setNextRecord(int nextRecord) {
        this.nextRecord = nextRecord;
    }

    public int getNumberOfRecordsReturned() {
        return numberOfRecordsReturned;
    }

    public void setNumberOfRecordsReturned(int numberOfRecordsReturned) {
        this.numberOfRecordsReturned = numberOfRecordsReturned;
    }
}
