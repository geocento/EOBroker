package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.*;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.ArcGISMap;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.shared.LatLng;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.addins.client.bubble.MaterialBubble;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.Position;
import gwt.material.design.client.ui.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

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
    MaterialLabel title;
    @UiField
    MaterialColumn requestDescription;
    @UiField
    HTMLPanel requestResponse;
    @UiField
    MaterialRow messages;
    @UiField
    ArcGISMap mapContainer;
    @UiField
    ProgressButton submitMessage;
    @UiField
    MaterialTextArea message;
    @UiField
    UserWidget userImage;
    @UiField
    ProgressButton submitResponse;
    @UiField
    com.geocento.webapps.eobroker.common.client.widgets.material.MaterialRichEditor responseEditor;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLabelIcon userName;
    @UiField
    ExpandPanel descriptionPanel;
    @UiField
    MaterialImageLoading image;
    @UiField
    MaterialButton status;
    @UiField
    MaterialLabel messagesComment;
    @UiField
    MaterialTooltip userTooltip;

    private Callback<Void, Exception> mapLoadedHandler = null;

    public MapJSNI map;

    private boolean mapLoaded = false;

    private class ProductRendering {
        GraphicJSNI footprint;
        WMSLayerJSNI overlay;
    }

    private HashMap<Product, ProductRendering> renderedProducts = new HashMap<Product, ProductRendering>();

    private Product outlinedProduct;

    private GraphicJSNI outlinedProductGraphicJSNI;

    private static NumberFormat format = NumberFormat.getFormat("#.##");

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

        userImage.setUser(Supplier.getLoginInfo().getUserName());

        template.setPlace(null);
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
        this.title.setText(title);
    }

    @Override
    public void displayDescription(String description) {
        this.description.setText(description);
    }

    @Override
    public void displayUser(UserDTO customer) {
        userName.setText(customer.getFullName() == null ? customer.getName() : customer.getFullName());
        userName.setImageUrl(customer.getIconUrl());
        userTooltip.setText("from company " + customer.getCompanyDTO().getName());
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
        displayTitle("Request for bespoke service '" + productServiceSupplierRequestDTO.getServiceName() + "'");
        image.setImageUrl(productServiceSupplierRequestDTO.getServiceImage());
        displayDescription("Request ID '" + productServiceSupplierRequestDTO.getId() +
                "' requested on " + DateUtils.formatDateTime(productServiceSupplierRequestDTO.getCreationTime()));
        displayUser(productServiceSupplierRequestDTO.getCustomer());
        // TODO - change to take into account request status
        status.setText("Submitted");
        this.requestDescription.clear();
        addRequestValue("Product requested", productServiceSupplierRequestDTO.getProduct().getName());
        if(productServiceSupplierRequestDTO.getFormValues().size() == 0) {
            this.requestDescription.add(new HTMLPanel("<p>No data provided</p>"));
        } else {
            for (FormElementValue formElementValue : productServiceSupplierRequestDTO.getFormValues()) {
                addRequestValue(formElementValue.getName(), formElementValue.getValue());
            }
        }
        if(productServiceSupplierRequestDTO.getSearchId() != null) {
            this.requestDescription.add(new HTMLPanel("<p style='padding: 0.5rem;'>This request originated from the following " +
                    "<a href='" + GWT.getHostPageBaseURL() + "#productfeasibility:feasibilityId=" + productServiceSupplierRequestDTO.getSearchId() + "'>feasibility search</a></p>"));
        }
        displayAoI(AoIUtil.fromWKT(productServiceSupplierRequestDTO.getAoIWKT()));
        displayResponse(productServiceSupplierRequestDTO.getSupplierResponse());
        displayMessages(productServiceSupplierRequestDTO.getMessages());
    }

    @Override
    public void displayOTSProductRequest(OTSProductRequestDTO otsProductRequestDTO) {
        ProductDatasetDTO productDatasetDTO = otsProductRequestDTO.getProductDataset();
        displayTitle("Request for off the shelf products '" + productDatasetDTO.getName() + "'");
        image.setImageUrl(productDatasetDTO.getImageUrl());
        displayDescription("Request ID '" + otsProductRequestDTO.getId() +
                "' requested on " + DateUtils.formatDateTime(otsProductRequestDTO.getCreationTime()));
        displayUser(otsProductRequestDTO.getCustomer());
        // TODO - change to take into account request status
        status.setText("Submitted");
        this.requestDescription.clear();
        addRequestValue("Customer comment", otsProductRequestDTO.getComments());
        addRequestValue("Products ID selection", otsProductRequestDTO.getSelection());
        if(otsProductRequestDTO.getFormValues().size() == 0) {
            this.requestDescription.add(new HTMLPanel("<p>No search parameters provided</p>"));
        } else {
            this.requestDescription.add(new HTMLPanel("<p>Initial search parameters</p>"));
            for (FormElementValue formElementValue : otsProductRequestDTO.getFormValues()) {
                addRequestValue(formElementValue.getName(), formElementValue.getValue());
            }
        }
        displayAoI(AoIUtil.fromWKT(otsProductRequestDTO.getAoIWKT()));
        displayResponse(otsProductRequestDTO.getSupplierResponse());
        displayMessages(otsProductRequestDTO.getMessages());
    }

    @Override
    public void displayResponse(String supplierResponse) {
        if(supplierResponse == null) {
            responseEditor.setHTML("");
        } else {
            responseEditor.setVisible(false);
            requestResponse.clear();
            requestResponse.add(new HTML(supplierResponse));
        }
    }

    private void displayMessages(List<MessageDTO> messages) {
        this.messages.clear();
        String userName = Supplier.getLoginInfo().getUserName();
        if(messages != null && messages.size() > 0) {
            for (MessageDTO messageDTO : messages) {
                boolean isCustomer = !userName.contentEquals(messageDTO.getFrom());
                addMessage(messageDTO.getFrom(),
                        isCustomer, messageDTO.getMessage(), messageDTO.getCreationDate());
            }
            message.setPlaceholder("Reply...");
        }
        updateMessagesComment();
    }

    private void updateMessagesComment() {
        boolean hasMessages = messages.getWidgetCount() > 0;
        messagesComment.setVisible(!hasMessages);
        if(!hasMessages) {
            messagesComment.setText("No messages yet...");
            message.setPlaceholder("Start a conversation...");
        } else {
            message.setPlaceholder("Reply...");
        }
    }

    @Override
    public void addMessage(String userName, boolean isCustomer, String message, Date date) {
        MaterialRow materialRow = new MaterialRow();
        materialRow.setMarginBottom(0);
        messages.add(materialRow);
        Color colour = Color.WHITE;
        UserWidget userWidget = new UserWidget(userName);
        userWidget.setMarginTop(8);
        userWidget.setFloat(isCustomer ? Style.Float.LEFT : Style.Float.RIGHT);
        userWidget.setSize(40);
        materialRow.add(userWidget);
        MaterialBubble materialBubble = new MaterialBubble();
        materialBubble.setBackgroundColor(colour);
        materialBubble.setFloat(isCustomer ? Style.Float.LEFT : Style.Float.RIGHT);
        materialBubble.setPosition(isCustomer ? Position.LEFT : Position.RIGHT);
        if(isCustomer) {
            materialBubble.setMarginLeft(12);
        } else {
            materialBubble.setMarginRight(12);
        }
        materialBubble.setWidth("50%");
        materialRow.add(materialBubble);
        materialBubble.add(new MaterialLabel(message));
        MaterialLabel materialLabel = new MaterialLabel();
        materialLabel.setText(DateUtils.dateFormat.format(date));
        materialLabel.setFloat(Style.Float.RIGHT);
        materialLabel.setFontSize(0.6, Style.Unit.EM);
        materialBubble.add(materialLabel);
        updateMessagesComment();
    }

    private void displayAoI(AoIDTO aoi) {
        map.getGraphics().clear();
        if(aoi != null) {
            map.getGraphics().addGraphic(mapContainer.arcgisMap.createGeometryFromAoI(aoi), mapContainer.arcgisMap.createFillSymbol("#ff00ff", 2, "rgba(0,0,0,0.2)"));
            map.setExtent(mapContainer.arcgisMap.createGeometryFromAoI(aoi).getExtent());
        }
    }

    @Override
    public void displayImageryRequest(ImageryServiceRequestDTO imageryServiceRequestDTO) {
        this.requestDescription.clear();
        addRequestValue("Image type", imageryServiceRequestDTO.getImageType());
        addRequestValue("Period of interest",
                DateUtils.formatTimePeriod(imageryServiceRequestDTO.getStart(), imageryServiceRequestDTO.getStop()));
        addRequestValue("Application", imageryServiceRequestDTO.getApplication());
        addRequestValue("Additional information", imageryServiceRequestDTO.getAdditionalInformation());
        displayAoI(AoIUtil.fromWKT(imageryServiceRequestDTO.getAoiWKT()));
        if(imageryServiceRequestDTO.getSupplierResponse() == null) {
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
            requestResponse.add(new HTML(imageryServiceRequestDTO.getSupplierResponse()));
        }
        displayMessages(imageryServiceRequestDTO.getMessages());
    }

    private void addRequestValue(String name, String value) {
        this.requestDescription.add(new HTMLPanel("<p style='padding: 0.5rem;'><b>" + name + ":</b> " +
                (value == null ? "not provided" : value) + "</p>"));
    }

    @Override
    public void displayImagesRequest(ImagesRequestDTO imagesRequestDTO) {
        this.requestDescription.clear();
        displayAoI(AoIUtil.fromWKT(imagesRequestDTO.getAoiWKT()));
        final List<Product> products = imagesRequestDTO.getProducts();
        if(products == null || products.size() == 0) {
            requestDescription.add(new MaterialLabel("No products!"));
        } else {
            MaterialLabel materialLabel = new MaterialLabel("Products selected");
            materialLabel.setFontSize(1.2, Style.Unit.EM);
            materialLabel.setPadding(10);
            requestDescription.add(materialLabel);
            for (Product product : imagesRequestDTO.getProducts()) {
                MaterialCheckBox materialCheckBox = new MaterialCheckBox(product.getSatelliteName() +
                        " acquired on " + DateUtils.formatDateOnly(product.getStart()));
                materialCheckBox.setObject(product);
                materialCheckBox.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        refreshMap(products);
                    }
                });
                materialCheckBox.getElement().getStyle().setPaddingLeft(30, Style.Unit.PX);
                requestDescription.add(materialCheckBox);
            }
            refreshMap(imagesRequestDTO.getProducts());
        }
        if(imagesRequestDTO.getResponse() == null) {
        } else {
            requestResponse.clear();
            requestResponse.add(new HTML(imagesRequestDTO.getResponse()));
        }
        displayMessages(imagesRequestDTO.getMessages());
    }

    private void refreshMap(List<Product> products) {
        // refresh products display on map
        for(Product product : products) {
            boolean toRender = isProductSelected(product);
            boolean rendered = renderedProducts.containsKey(product);
            if(toRender && !rendered) {
                ProductRendering productRendering = new ProductRendering();
                PolygonJSNI polygon = mapContainer.arcgisMap.createPolygon(product.getCoordinatesWKT().replace("POLYGON((", "").replace("))", ""));
                productRendering.footprint = map.getGraphics().addGraphic(polygon,
                        mapContainer.arcgisMap.createFillSymbol(product.getType() == Product.TYPE.ARCHIVE ? "#ff0000" : "#00ffff", 2, "rgba(0,0,0,0.0)"));
                // add wms layer
                if (product.getType() == Product.TYPE.ARCHIVE && product.getQl() != null) {
                    productRendering.overlay = map.addWMSLayer(product.getQl(), WMSLayerInfoJSNI.createInfo(product.getSatelliteName(), product.getSatelliteName()), polygon.getExtent());
                }
                renderedProducts.put(product, productRendering);
            } else if(!toRender && rendered) {
                ProductRendering productRendering = renderedProducts.get(product);
                if(productRendering.footprint != null) {
                    map.getGraphics().remove(productRendering.footprint);
                }
                if(productRendering.overlay != null) {
                    map.removeWMSLayer(productRendering.overlay);
                }
                renderedProducts.remove(product);
            }
        }
        // remove previous outline product
        if(outlinedProductGraphicJSNI != null) {
            map.getGraphics().remove(outlinedProductGraphicJSNI);
        }
        // add outlined product on top
        if(outlinedProduct != null) {
            outlinedProductGraphicJSNI = map.getGraphics().addGraphic(mapContainer.arcgisMap.createPolygon(outlinedProduct.getCoordinatesWKT().replace("POLYGON((", "").replace("))", "")),
                    mapContainer.arcgisMap.createFillSymbol("#0000ff", 2, "rgba(0,0,0,0.2)"));
        }
    }

    private boolean isProductSelected(Product product) {
        for(Widget widget : requestDescription) {
            if(widget instanceof MaterialCheckBox && ((MaterialCheckBox) widget).getObject() == product) {
                return ((MaterialCheckBox) widget).getValue();
            }
        }
        return false;
    }

    @Override
    public HasClickHandlers getSubmitMessage() {
        return submitMessage;
    }

    @Override
    public HasClickHandlers getSubmitResponse() {
        return submitResponse;
    }

    @Override
    public HasText getMessageText() {
        return message;
    }

    @Override
    public String getResponse() {
        return responseEditor.getHTML();
    }

}