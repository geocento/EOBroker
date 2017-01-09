package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.RootPanel;
import gwt.material.design.client.ui.MaterialListBox;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 15/11/2016.
 */
public class StyleNameEditor {

    interface StyleNameEditorUiBinder extends UiBinder<MaterialModal, StyleNameEditor> {
    }

    private static StyleNameEditorUiBinder ourUiBinder = GWT.create(StyleNameEditorUiBinder.class);

    public static interface Presenter {
        void styleSelected(String styleName);
    }

    @UiField
    MaterialPanel listStyles;

    private final MaterialModal materialModal;

    private static StyleNameEditor instance = null;

    private Presenter presenter;

    public StyleNameEditor() {

        materialModal = ourUiBinder.createAndBindUi(this);

        // add to document
        RootPanel.get().add(materialModal);

    }

    public static StyleNameEditor getInstance() {
        if(instance == null) {
            instance = new StyleNameEditor();
        }
        return instance;
    }

    // TODO - clean the dropzone?
    public void display(final Presenter presenter) {
        this.presenter = presenter;
        materialModal.openModal();
    }

    private void hide() {
        materialModal.closeModal();
    }

}