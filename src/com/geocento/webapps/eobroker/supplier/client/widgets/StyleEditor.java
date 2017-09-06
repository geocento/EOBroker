package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.supplier.client.resources.Resources;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.shared.dtos.StyleDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialPanel;
import gwt.material.design.client.ui.MaterialTextBox;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

/**
 * Created by thomas on 15/11/2016.
 */
public class StyleEditor {

    interface StyleEditorUiBinder extends UiBinder<MaterialModal, StyleEditor> {
    }

    private static StyleEditorUiBinder ourUiBinder = GWT.create(StyleEditorUiBinder.class);

    public static interface Presenter {
        void styleChanged(StyleDTO styleDTO);
    }

    @UiField
    MaterialPanel styleEditor;
    @UiField
    MaterialTextBox styleName;

    private static StyleEditor instance = null;

    private final MaterialModal materialModal;

    private JavaScriptObject aceEditor;

    private Presenter presenter;

    public StyleEditor() {

        materialModal = ourUiBinder.createAndBindUi(this);

        // add to document
        RootPanel.get().add(materialModal);

    }

    public static StyleEditor getInstance() {
        if(instance == null) {
            instance = new StyleEditor();
        }
        return instance;
    }

    // TODO - clean the dropzone?
    public void display(final Presenter presenter) {
        this.presenter = presenter;
        materialModal.open();
        styleEditor.add(new Label("Loading..."));
        ScriptInjector.fromUrl("./js/ace/ace.js").setWindow(ScriptInjector.TOP_WINDOW).setCallback(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Failed to load the ace library, reason is " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                aceEditor = startCodeEditing(styleEditor.getElement(), "chrome");
                setCodeEditing(aceEditor, Resources.INSTANCE.defaultStyle().getText());
            }
        }).inject();
    }

    @UiHandler("create")
    void createStyle(ClickEvent clickEvent) {
        if(presenter != null) {
            final StyleDTO styleDTO = new StyleDTO();
            styleDTO.setStyleName(styleName.getText());
            styleDTO.setSldBody(getCode());
            try {
                REST.withCallback(new MethodCallback<String>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        Window.alert("Could not save style, reason is " + method.getResponse().getText());
                    }

                    @Override
                    public void onSuccess(Method method, String response) {
                        hide();
                        presenter.styleChanged(styleDTO);
                    }
                }).call(ServicesUtil.assetsService).saveStyle(styleDTO);
            } catch (Exception e) {
            }
        }
    }

    @UiHandler("cancel")
    void cancel(ClickEvent clickEvent) {
        hide();
    }

    public void hide() {
        materialModal.close();
    }

    public String getCode() {
        return getCodeEditing(aceEditor);
    }

    private native final JavaScriptObject startCodeEditing(Element element, String theme) /*-{
        var ace = $wnd['ace'];
        var editor = ace.edit(element);
        editor.setTheme("ace/theme/" + theme);
        editor.getSession().setMode("ace/mode/xml");
        return editor;
    }-*/;

    private native final String getCodeEditing(JavaScriptObject aceEditor) /*-{
        return aceEditor.getValue();
    }-*/;

    private native final void setCodeEditing(JavaScriptObject aceEditor, String code) /*-{
        aceEditor.setValue(code);
    }-*/;

}