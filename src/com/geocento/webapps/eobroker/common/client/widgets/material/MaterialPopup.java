package com.geocento.webapps.eobroker.common.client.widgets.material;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.ButtonType;
import gwt.material.design.client.constants.ModalType;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialModalContent;
import gwt.material.design.client.ui.MaterialModalFooter;

/**
 * Created by thomas on 13/11/2017.
 */
public class MaterialPopup {

    private static MaterialPopup instance;

    private final MaterialModal materialModal;

    private final MaterialModalContent materialModalContent;

    public MaterialPopup() {
        materialModal = new MaterialModal();
        materialModal.setType(ModalType.DEFAULT);
        materialModal.setDismissible(false);
        materialModalContent = new MaterialModalContent();
        materialModal.add(materialModalContent);
        MaterialModalFooter materialModalFooter = new MaterialModalFooter();
        MaterialButton materialButton = new MaterialButton("CLOSE");
        materialButton.setType(ButtonType.FLAT);
        materialModalFooter.add(materialButton);
        materialModal.add(materialModalFooter);
        materialButton.addClickHandler(event -> materialModal.close());
        // add to document
        RootPanel.get().add(materialModal);
    }

    public static MaterialPopup getInstance() {
        if(instance == null) {
            instance = new MaterialPopup();
        }
        return instance;
    }

    public void display(Widget widget) {
        materialModalContent.clear();
        materialModalContent.add(widget);
        materialModal.open();
    }
}
