package com.geocento.webapps.eobroker.supplier.shared.dtos;

/**
 * Created by thomas on 21/11/2016.
 */
public class SampleUploadDTO {

    String fileUri;
    String layerName;

    public SampleUploadDTO() {
    }

    public String getFileUri() {
        return fileUri;
    }

    public void setFileUri(String fileUri) {
        this.fileUri = fileUri;
    }

    public String getLayerName() {
        return layerName;
    }

    public void setLayerName(String layerName) {
        this.layerName = layerName;
    }
}
