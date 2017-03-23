package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.shared.dtos.StyleDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.RootPanel;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialModal;
import gwt.material.design.client.ui.MaterialPanel;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

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
        materialModal.open();
        listStyles.clear();
        listStyles.add(new LoadingWidget("Loading styles..."));
        loadStyles();
    }

    private void loadStyles() {
        // load styles
        try {
            REST.withCallback(new MethodCallback<List<String>>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    listStyles.clear();
                    listStyles.add(new MaterialLabel("Error loading styles"));
                }

                @Override
                public void onSuccess(Method method, List<String> styles) {
                    listStyles.clear();
                    if (styles.size() == 0) {
                        listStyles.add(new MaterialLabel("No style defined..."));
                    } else {
                        for (final String styleName : styles) {
                            StyleWidget styleWidget = new StyleWidget(styleName);
                            styleWidget.getDelete().addClickHandler(new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent event) {
                                }
                            });
                            styleWidget.getSelect().addClickHandler(new ClickHandler() {
                                @Override
                                public void onClick(ClickEvent event) {
                                    if (presenter != null) {
                                        presenter.styleSelected(styleName);
                                    }
                                }
                            });
                            listStyles.add(styleWidget);
                        }
                    }
                }

            }).call(ServicesUtil.assetsService).getStyles();
        } catch (Exception e) {
        }
    }

    @UiHandler("submit")
    void createStyle(ClickEvent clickEvent) {
        StyleEditor.getInstance().display(new StyleEditor.Presenter() {
            @Override
            public void styleChanged(StyleDTO styleDTO) {
                loadStyles();
            }
        });
    }

    @UiHandler("cancel")
    void cancel(ClickEvent clickEvent) {
        hide();
    }

    public void hide() {
        materialModal.close();
    }

}