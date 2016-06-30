package com.geocento.webapps.eobroker.admin.client.widgets;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.ScriptInjector;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Created by thomas on 19/04/2016.
 */
public class CodeEditor extends Composite {

    interface CodeEditorUiBinder extends UiBinder<HTMLPanel, CodeEditor> {
    }

    private static CodeEditorUiBinder ourUiBinder = GWT.create(CodeEditorUiBinder.class);

    @UiField
    HTMLPanel panel;

    private JavaScriptObject aceEditor;

    public CodeEditor() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void startEditing(final String code) {
        panel.add(new Label("Loading..."));
        ScriptInjector.fromUrl("./js/ace/ace.js").setWindow(ScriptInjector.TOP_WINDOW).setCallback(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Failed to load the ace library, reason is " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                aceEditor = startCodeEditing(panel.getElement(), "chrome");
                setCodeEditing(aceEditor, code);
            }
        }).inject();
    }

    public String getCode() {
        return getCodeEditing(aceEditor);
    }

    private native final JavaScriptObject startCodeEditing(Element element, String theme) /*-{
        var ace = $wnd['ace'];
        var editor = ace.edit(element);
        editor.setTheme("ace/theme/" + theme);
        editor.getSession().setMode("ace/mode/javascript");
        return editor;
    }-*/;

    private native final String getCodeEditing(JavaScriptObject aceEditor) /*-{
        return aceEditor.getValue();
    }-*/;

    private native final void setCodeEditing(JavaScriptObject aceEditor, String code) /*-{
        aceEditor.setValue(code);
    }-*/;

}