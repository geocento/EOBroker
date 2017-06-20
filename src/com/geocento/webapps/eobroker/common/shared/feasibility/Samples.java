package com.geocento.webapps.eobroker.common.shared.feasibility;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
public class Samples {

    String pdfReport;
    String wmsLayer;

    public Samples() {
    }

    public Samples(String pdfReport, String wmsLayer) {
        this.pdfReport = pdfReport;
        this.wmsLayer = wmsLayer;
    }

    public String getPdfReport() {
        return pdfReport;
    }

    public void setPdfReport(String pdfReport) {
        this.pdfReport = pdfReport;
    }

    public String getWmsLayer() {
        return wmsLayer;
    }

    public void setWmsLayer(String wmsLayer) {
        this.wmsLayer = wmsLayer;
    }
}

