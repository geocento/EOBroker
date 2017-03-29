package com.geocento.webapps.eobroker.common.client.widgets.material;

import com.geocento.webapps.eobroker.common.client.utils.StringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gwt.material.design.addins.client.richeditor.base.HasPasteHandlers;
import gwt.material.design.addins.client.richeditor.base.MaterialRichEditorBase;
import gwt.material.design.addins.client.richeditor.base.constants.RichEditorEvents;
import gwt.material.design.addins.client.richeditor.events.PasteEvent;
import gwt.material.design.addins.client.richeditor.js.JsRichEditor;

import static gwt.material.design.addins.client.richeditor.js.JsRichEditor.$;

/**
 * Created by thomas on 28/03/2017.
 */
public class MaterialRichEditor extends MaterialRichEditorBase implements HasValueChangeHandlers<String>, HasPasteHandlers {

    static {
        new gwt.material.design.addins.client.richeditor.MaterialRichEditor();
    }

    public MaterialRichEditor() {
        super();
    }

    public MaterialRichEditor(String placeholder) {
        this();
        setPlaceholder(placeholder);
    }

    public MaterialRichEditor(String placeholder, String value) {
        this(placeholder);
        setValue(value);
    }

    @Override
    protected void onLoad() {
        super.onLoad();

        initRichEditor();
    }

    /**
     * Intialize the rich editor with custom properties.
     */
    protected void initRichEditor() {
        JsRichEditor jsRichEditor = $(getElement());

        JsRichEditorOptions options = new JsRichEditorOptions();
        // Set up the toolbar items
        Object[][] toolbar = new Object[][]{};
        toolbar[0] = new Object[]{"style", extractOptions(getStyleOptions())};
        toolbar[1] = new Object[]{"para", extractOptions(getParaOptions())};
        toolbar[2] = new Object[]{"height", extractOptions(getHeightOptions())};
        toolbar[3] = new Object[]{"undo", extractOptions(getUndoOptions())};
        toolbar[4] = new Object[]{"fonts", extractOptions(getFontOptions())};
        toolbar[5] = new Object[]{"color", extractOptions(getColorOptions())};
        toolbar[6] = new Object[]{"ckMedia", extractOptions(getCkMediaOptions())};
        toolbar[7] = new Object[]{"misc", extractOptions(getMiscOptions())};

        // Other important options
        options.toolbar = toolbar;
        options.airMode = isAirMode();
        options.disableDragAndDrop = isDisableDragAndDrop();
        options.followingToolbar = false;
        options.placeholder = getPlaceholder();
        options.height = getHeight();
        options.minHeight = 200;
        options.defaultBackColor = "#777";
        options.defaultTextColor = "#fff";
        options.onImageUpload = (e) -> {
            uploadData(e, GWT.getModuleBaseURL().replace(GWT.getModuleName() + "/", "") + "upload/image/", new AsyncCallback<String>() {

                @Override
                public void onFailure(Throwable caught) {

                }

                @Override
                public void onSuccess(String response) {
                    String error = StringUtils.extract(response, "<error>", "</error>");
                    if(error.length() > 0) {
                        // TODO - send error event
                        Window.alert(error);
                        return;
                    }
                    String imageUrl = StringUtils.extract(response, "<value>", "</value>");
                    jsRichEditor.materialnote("insertImage", SafeHtmlUtils.fromString(imageUrl).asString());
                }
            });
            return null;
        };

        jsRichEditor.materialnote(options);

        // Events
        jsRichEditor.on(RichEditorEvents.MATERIALNOTE_BLUR, event -> {
            fireEvent(new BlurEvent() {
            });
            return true;
        });
        jsRichEditor.on(RichEditorEvents.MATERIALNOTE_FOCUS, event -> {
            fireEvent(new FocusEvent() {
            });
            return true;
        });
        jsRichEditor.on(RichEditorEvents.MATERIALNOTE_KEYUP, event -> {
            fireEvent(new KeyUpEvent() {
            });
            return true;
        });
        jsRichEditor.on(RichEditorEvents.MATERIALNOTE_KEYDOWN, event -> {
            fireEvent(new KeyDownEvent() {
            });
            return true;
        });
        jsRichEditor.on(RichEditorEvents.MATERIALNOTE_PASTE, event -> {
            fireEvent(new PasteEvent() {
            });
            return true;
        });
        jsRichEditor.on(RichEditorEvents.MATERIALNOTE_CHANGE, event -> {
            ValueChangeEvent.fire(MaterialRichEditor.this, getHTMLCode(getElement()));
            return true;
        });
    }

    private final native void uploadData(Object e, String url, AsyncCallback<String> callback) /*-{
        var data = new FormData();
        data.append("file", e[0]);
        console.log(data);
        $wnd["jQuery"].ajax({
            data: data,
            type: "POST",
            url: url,
            cache: false,
            contentType: false,
            processData: false,
            success: function(response) {
                callback.@com.google.gwt.user.client.rpc.AsyncCallback::onSuccess(Ljava/lang/Object;)(response);
            }
        });
    }-*/;

    @Override
    protected void onUnload() {
        super.onUnload();

        // Perform tear down on materialnote
        JsRichEditor jsRichEditor = $(getElement());
        jsRichEditor.off(RichEditorEvents.MATERIALNOTE_BLUR);
        jsRichEditor.off(RichEditorEvents.MATERIALNOTE_FOCUS);
        jsRichEditor.off(RichEditorEvents.MATERIALNOTE_KEYUP);
        jsRichEditor.off(RichEditorEvents.MATERIALNOTE_KEYDOWN);
        jsRichEditor.off(RichEditorEvents.MATERIALNOTE_PASTE);
        jsRichEditor.off(RichEditorEvents.MATERIALNOTE_CHANGE);
        jsRichEditor.destroy();
    }

    /**
     * Insert custom text inside the note zone.
     */
    public void insertText(String text) {
        insertText(getElement(), text);
    }

    /**
     * Insert custom text inside the note zone.
     */
    protected void insertText(Element e, String text) {
        $(e).materialnote("insertText", SafeHtmlUtils.fromString(text).asString());
    }

    /**
     * Insert custom HTML inside the note zone.
     */
    public void pasteHTML(String html) {
        pasteHTML(getElement(), html);
    }

    /**
     * Insert custom HTML inside the note zone with JSNI function.
     */
    protected void pasteHTML(Element e, String html) {
        $(e).materialnote("pasteHTML", html);
    }

    @Override
    public void clear() {
        $(getElement()).materialnote("reset");
    }

    @Override
    public HandlerRegistration addPasteHandler(final PasteEvent.PasteHandler handler) {
        return addHandler(handler, PasteEvent.TYPE);
    }

}
