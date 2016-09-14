package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.ProgressButton;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.ArcGISMap;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.ArcgisMapJSNI;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.MapJSNI;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.addins.client.bubble.MaterialBubble;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.ui.*;

import java.util.Date;

/**
 * Created by thomas on 09/05/2016.
 */
public class OrderViewImpl extends Composite implements OrderView {

    private Presenter presenter;

    interface OrdersViewUiBinder extends UiBinder<Widget, OrderViewImpl> {
    }

    private static OrdersViewUiBinder ourUiBinder = GWT.create(OrdersViewUiBinder.class);

    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialTitle title;
    @UiField
    MaterialColumn requestDescription;
    @UiField
    HTMLPanel requestResponse;
    @UiField
    MaterialRow messages;
    @UiField
    ArcGISMap mapContainer;
    @UiField
    ProgressButton submit;

    private Callback<Void, Exception> mapLoadedHandler = null;

    public MapJSNI map;

    private boolean mapLoaded = false;

    public OrderViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        mapContainer.loadArcGISMap(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {

            }

            @Override
            public void onSuccess(Void result) {
                mapContainer.createMap("streets", new LatLng(40.0, -4.0), 3, new com.geocento.webapps.eobroker.common.client.widgets.maps.resources.Callback<MapJSNI>() {

                    @Override
                    public void callback(final MapJSNI mapJSNI) {
                        final ArcgisMapJSNI arcgisMap = mapContainer.arcgisMap;
                        OrderViewImpl.this.map = mapJSNI;
                        map.setZoom(3);
                        mapLoaded();
                    }
                });
            }
        });
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        this.mapLoadedHandler = mapLoadedHandler;
        if(mapLoaded) {
            mapLoadedHandler.onSuccess(null);
        }
    }

    private void mapLoaded() {
        mapLoaded = true;
        if(mapLoadedHandler != null) {
            mapLoadedHandler.onSuccess(null);
        }
    }

    @Override
    public void displayTitle(String title) {
        this.title.setTitle(title);
    }

    @Override
    public void displayUser(UserDTO customer) {
        this.title.setDescription("From user '" + customer.getName() + "'");
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void displayProductRequest(ProductServiceSupplierRequestDTO productServiceSupplierRequestDTO) {
        this.requestDescription.clear();
        for(FormElementValue formElementValue : productServiceSupplierRequestDTO.getFormValues()) {
            this.requestDescription.add(new HTMLPanel("<p><b>" + formElementValue.getName() + ":</b> " + formElementValue.getValue() + "</p>"));
        }
        displayAoI(AoIUtil.fromWKT(productServiceSupplierRequestDTO.getAoIWKT()));
        if(productServiceSupplierRequestDTO.getSupplierResponse() == null) {
/*
            MaterialRichEditor materialRichEditor = new MaterialRichEditor();
            materialRichEditor.setPlaceholder("Enter your response");
            requestResponse.add(materialRichEditor);
            ProgressButton progressButton = new ProgressButton();
            progressButton.setText("SUBMIT");
            requestResponse.add(progressButton);
*/
        } else {
            requestResponse.clear();
            requestResponse.add(new HTML(productServiceSupplierRequestDTO.getSupplierResponse()));
        }
        messages.clear();
        UserDTO customer = productServiceSupplierRequestDTO.getCustomer();
        for(MessageDTO messageDTO : productServiceSupplierRequestDTO.getMessages()) {
            boolean isCustomer = customer.getName().contentEquals(messageDTO.getFrom());
            addMessage(isCustomer ? customer.getCompanyDTO().getIconURL() : "https://mir-s3-cdn-cf.behance.net/project_modules/disp/70e0a922433737.5631e83fc9429.png",
                    isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
        }
    }

    private void addMessage(String imageUrl, boolean isCustomer, String message, Date date) {
        MaterialRow materialRow = new MaterialRow();
        materialRow.setMarginBottom(0);
        messages.add(materialRow);
        String colour = isCustomer ? "blue accent-1" : "green accent-1";
        MaterialImage materialImage = new MaterialImage(imageUrl);
        materialImage.setBackgroundColor(colour);
        materialImage.setMarginRight(12);
        materialImage.setMarginTop(8);
        materialImage.setFloat(isCustomer ? Style.Float.LEFT : Style.Float.RIGHT);
        materialImage.setWidth("40px");
        materialImage.setHeight("40px");
        materialImage.setShadow(1);
        materialImage.setCircle(true);
        materialRow.add(materialImage);
        MaterialBubble materialBubble = new MaterialBubble();
        materialBubble.setBackgroundColor(colour);
        materialBubble.setPosition(isCustomer ? Position.LEFT : Position.RIGHT);
        materialRow.add(materialBubble);
        materialBubble.add(new MaterialLabel(message));
        MaterialLabel materialLabel = new MaterialLabel();
        materialLabel.setText(DateUtils.dateTimeFormat.format(date));
        materialLabel.setFloat(Style.Float.RIGHT);
        materialLabel.setFontSize(0.6, Style.Unit.EM);
        materialBubble.add(materialLabel);
    }

    private void displayAoI(AoI aoi) {
        map.getGraphics().clear();
        if(aoi != null) {
            map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoi), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
        }
    }

    @Override
    public void displayImageryRequest(ImageryServiceRequestDTO response) {

    }

    @Override
    public void displayImagesRequest(ImagesRequestDTO response) {

    }

}