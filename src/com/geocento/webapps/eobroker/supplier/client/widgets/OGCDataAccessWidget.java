package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccess;
import com.geocento.webapps.eobroker.common.shared.entities.DatasetAccessOGC;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gwt.material.design.client.constants.Display;
import gwt.material.design.client.constants.IconPosition;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 08/11/2016.
 */
public class OGCDataAccessWidget extends DataAccessWidget {

    private MaterialLink materialLink;

    public OGCDataAccessWidget(DatasetAccessOGC datasetAccess, boolean editableUri) {
        super(datasetAccess, editableUri);
        // if the uri is not editable it is a sample from the local geoserver
        if(!editableUri) {
            MaterialPanel panel = new MaterialPanel();
            panel.setMarginTop(20);
            MaterialLabel materialLabel = new MaterialLabel("Style: ");
            materialLabel.setDisplay(Display.INLINE_BLOCK);
            materialLabel.setMarginRight(10);
            panel.add(materialLabel);
            materialLink = new MaterialLink(datasetAccess.getStyleName());
            String styleName = ((DatasetAccessOGC) datasetAccess).getStyleName();
            setStyle(styleName);
            materialLink.setDisplay(Display.INLINE_BLOCK);
            materialLink.setIconType(IconType.EDIT);
            materialLink.setIconPosition(IconPosition.RIGHT);
            materialLink.addClickHandler(new ClickHandler() {
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
            panel.add(materialLink);
            addField(panel);
        }
    }

    private void setStyle(String styleName) {
        materialLink.setText(styleName == null ? "default" : styleName);
    }

    public DatasetAccess getDatasetAccess() {
        super.getDatasetAccess();
        if(materialLink != null) {
            String styleName = materialLink.getText();
            if(styleName.contentEquals("default")) {
                styleName = null;
            }
            ((DatasetAccessOGC) datasetAccess).setStyleName(styleName);
        }
        return datasetAccess;
    }

}