package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.constants.IconPosition;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 08/11/2016.
 */
public class OGCDataAccessWidget extends DataAccessWidget {

    private MaterialTextBox serverUrl;

    private MaterialTextBox layerName;

    private MaterialLink styleNameEditor;

    private MaterialTextBox wcsServer;

    private MaterialTextBox wcsResourceName;

    private MaterialCheckBox corsEnabled;

    public OGCDataAccessWidget(DatasetAccessOGC datasetAccess) {
        this(datasetAccess, datasetAccess.isHostedData(), true);
    }

    public OGCDataAccessWidget(DatasetAccessOGC datasetAccess, boolean editableUri) {
        this(datasetAccess, editableUri, true);
    }

    public OGCDataAccessWidget(DatasetAccessOGC datasetAccess, boolean editableUri, boolean withWCS) {
        super(datasetAccess, editableUri);
        //  rename uri to layer name
        uri.setPlaceholder("Provide the link to the original file for this data");

        // add server url
        serverUrl = new MaterialTextBox();
        serverUrl.setPlaceholder("The OWS server base WMS URL");
        serverUrl.setMarginTop(20);
        addField(serverUrl);
        setServerUrl(datasetAccess.getServerUrl());
        serverUrl.setReadOnly(!editableUri);
        serverUrl.setEnabled(editableUri);

        // add server url
        layerName = new MaterialTextBox();
        layerName.setPlaceholder("The layer name");
        layerName.setMarginTop(20);
        addField(layerName);
        setLayerName(datasetAccess.getLayerName());
        layerName.setReadOnly(!editableUri);
        layerName.setEnabled(editableUri);

        // if the uri is not editable it is a sample from the local geoserver
        if(!editableUri) {
            MaterialPanel panel = new MaterialPanel();
            panel.setMarginTop(20);
            MaterialLabel materialLabel = new MaterialLabel("Style: ");
            materialLabel.setDisplay(Display.INLINE_BLOCK);
            materialLabel.setMarginRight(10);
            panel.add(materialLabel);
            styleNameEditor = new MaterialLink(datasetAccess.getStyleName());
            String styleName = ((DatasetAccessOGC) datasetAccess).getStyleName();
            setStyle(styleName);
            styleNameEditor.setDisplay(Display.INLINE_BLOCK);
            styleNameEditor.setIconType(IconType.EDIT);
            styleNameEditor.setIconPosition(IconPosition.RIGHT);
            styleNameEditor.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    // TODO - display style name window
                    StyleNameEditor.getInstance().display(new StyleNameEditor.Presenter() {
                        @Override
                        public void styleSelected(String styleName) {
                            setStyle(styleName);
                            StyleNameEditor.getInstance().hide();
                        }
                    });
                }
            });
            panel.add(styleNameEditor);
            addField(panel);
        }
        if(withWCS) {
            wcsServer = new MaterialTextBox();
            wcsServer.setPlaceholder("The WCS service server URL, if available");
            wcsServer.setMarginTop(20);
            //wcsServer.setReadOnly(!editableUri);
            wcsServer.setText(datasetAccess.getWcsServerUrl());
            addField(wcsServer);
            wcsResourceName = new MaterialTextBox();
            wcsResourceName.setPlaceholder("The WCS resource name, if available");
            wcsResourceName.setMarginTop(20);
            //wcsResourceName.setReadOnly(!editableUri);
            wcsResourceName.setText(datasetAccess.getWcsResourceName());
            addField(wcsResourceName);
        }
        corsEnabled = new MaterialCheckBox();
        corsEnabled.setText("CORS enabled");
        corsEnabled.setEnabled(editableUri);
        corsEnabled.setValue(datasetAccess.isCorsEnabled());
        MaterialPanel materialPanel = new MaterialPanel();
        materialPanel.add(corsEnabled);
        materialPanel.setMarginTop(20);
        materialPanel.setMarginBottom(20);
        addField(materialPanel);
    }

    private void setServerUrl(String serverUrl) {
        this.serverUrl.setText(serverUrl);
    }

    private void setLayerName(String layerName) {
        this.layerName.setText(layerName);
    }

    private void setStyle(String styleName) {
        styleNameEditor.setText(styleName == null ? "default" : styleName);
    }

    public DatasetAccess getDatasetAccess() {
        super.getDatasetAccess();
        // update fields if editable
        if(editableUri) {
            ((DatasetAccessOGC) datasetAccess).setServerUrl(serverUrl.getText());
            ((DatasetAccessOGC) datasetAccess).setLayerName(layerName.getText());
            ((DatasetAccessOGC) datasetAccess).setWcsServerUrl(wcsServer == null ? null : wcsServer.getText());
            ((DatasetAccessOGC) datasetAccess).setWcsResourceName(wcsResourceName == null ? null : wcsResourceName.getText());
            ((DatasetAccessOGC) datasetAccess).setCorsEnabled(corsEnabled.getValue());
        } else {
            String styleName = styleNameEditor.getText();
            if(styleName.contentEquals("default")) {
                styleName = null;
            }
            ((DatasetAccessOGC) datasetAccess).setStyleName(styleName);
        }
        return datasetAccess;
    }

}