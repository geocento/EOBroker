package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialSideNav;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.WMSLayerInfoJSNI;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by thomas on 09/05/2016.
 */
public class VisualisationViewImpl extends Composite implements VisualisationView, ResizeHandler {

    private Presenter presenter;

    interface VisualisationUiBinder extends UiBinder<Widget, VisualisationViewImpl> {
    }

    private static VisualisationUiBinder ourUiBinder = GWT.create(VisualisationUiBinder.class);

    static public interface Style extends CssResource {
        String navOpened();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MapContainer mapContainer;
    @UiField
    HTMLPanel displayPanel;
    @UiField
    MaterialSideNav resourcesBar;

    public VisualisationViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        Scheduler.get().scheduleDeferred(new Command() {
            @Override
            public void execute() {
                resourcesBar.show();
                onResize(null);
            }
        });
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        mapContainer.setMapLoadedHandler(mapLoadedHandler);
    }

    @Override
    public void addWMSLayer(String wmsUrl, String layerName) {
        //mapContainer.map.removeAllLayers();
        mapContainer.map.addWMSLayer(wmsUrl, WMSLayerInfoJSNI.createInfo(layerName, layerName), MapJSNI.createExtent(-180.0, -90.0, 180.0, 90.0));
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void onResize(ResizeEvent event) {
        displayPanel.setHeight((Window.getClientHeight() - 64) + "px");
        template.setPanelStyleName(style.navOpened(), resourcesBar.isOpen());
        if(mapContainer.map != null) {
            mapContainer.map.resize();
        }
    }

}