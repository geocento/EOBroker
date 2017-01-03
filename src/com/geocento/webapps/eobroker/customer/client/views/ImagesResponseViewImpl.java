package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.client.widgets.maps.resources.*;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.imageapi.Product;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.shared.requests.ImagesServiceResponseDTO;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialCheckBox;
import gwt.material.design.client.ui.MaterialLabel;

import java.util.HashMap;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ImagesResponseViewImpl extends RequestViewImpl implements ImagesResponseView {

    private ImagesResponseView.Presenter presenter;

    private class ProductRendering {
        GraphicJSNI footprint;
        WMSLayerJSNI overlay;
    }

    private HashMap<Product, ProductRendering> renderedProducts = new HashMap<Product, ProductRendering>();

    private Product outlinedProduct;

    private GraphicJSNI outlinedProductGraphicJSNI;

    private static NumberFormat format = NumberFormat.getFormat("#.##");

    public ImagesResponseViewImpl(ClientFactoryImpl clientFactory) {
        super(clientFactory);

        setCategory(Category.imagery);
    }

    @Override
    public void setPresenter(ImagesResponseView.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void displayImagesRequest(ImagesServiceResponseDTO imagesServiceResponseDTO) {
        resetTabs();
        setStatus(imagesServiceResponseDTO.getStatus());
        this.requestDescription.clear();
        displayAoI(AoIUtil.fromWKT(imagesServiceResponseDTO.getAoiWKT()));
        final List<Product> products = imagesServiceResponseDTO.getProducts();
        if(products == null || products.size() == 0) {
            requestDescription.add(new MaterialLabel("No products!"));
        } else {
            MaterialLabel materialLabel = new MaterialLabel("Products selected");
            materialLabel.setFontSize(1.2, Style.Unit.EM);
            materialLabel.setPadding(10);
            requestDescription.add(materialLabel);
            for (Product product : imagesServiceResponseDTO.getProducts()) {
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
            refreshMap(imagesServiceResponseDTO.getProducts());
        }
        addResponseTab("response", "Offer", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                selectTab("response");
            }
        });
        displayResponseSupplier(imagesServiceResponseDTO.getCompany().getIconURL(), imagesServiceResponseDTO.getCompany().getName());
        displayResponse(imagesServiceResponseDTO.getResponse());
        displayMessages(imagesServiceResponseDTO.getMessages());
        selectTab("response");
    }

    private void refreshMap(List<Product> products) {
        ArcgisMapJSNI arcgisMap = mapContainer.getArcgisMap();
        MapJSNI map = mapContainer.map;
        // refresh products display on map
        for(Product product : products) {
            boolean toRender = isProductSelected(product);
            boolean rendered = renderedProducts.containsKey(product);
            if(toRender && !rendered) {
                ProductRendering productRendering = new ProductRendering();
                PolygonJSNI polygon = arcgisMap.createPolygon(product.getCoordinatesWKT().replace("POLYGON((", "").replace("))", ""));
                productRendering.footprint = map.getGraphics().addGraphic(polygon,
                        arcgisMap.createFillSymbol(product.getType() == Product.TYPE.ARCHIVE ? "#ff0000" : "#00ffff", 2, "rgba(0,0,0,0.0)"));
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
            outlinedProductGraphicJSNI = map.getGraphics().addGraphic(arcgisMap.createPolygon(outlinedProduct.getCoordinatesWKT().replace("POLYGON((", "").replace("))", "")),
                    arcgisMap.createFillSymbol("#0000ff", 2, "rgba(0,0,0,0.2)"));
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

}