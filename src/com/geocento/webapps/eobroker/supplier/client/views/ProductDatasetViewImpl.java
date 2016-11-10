package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.richeditor.MaterialRichEditor;
import gwt.material.design.client.base.SearchObject;
import gwt.material.design.client.ui.*;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductDatasetViewImpl extends Composite implements ProductDatasetView {

    private Presenter presenter;

    interface ProductDatasetViewUiBinder extends UiBinder<Widget, ProductDatasetViewImpl> {
    }

    private static ProductDatasetViewUiBinder ourUiBinder = GWT.create(ProductDatasetViewUiBinder.class);
    @UiField
    MaterialTextBox name;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialTextBox uri;
    @UiField
    MaterialTitle title;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialSearch product;
    @UiField
    MaterialTextArea description;
    @UiField
    MaterialRichEditor fullDescription;
    @UiField
    MapContainer mapContainer;

    public ProductDatasetViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

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
    public void setTitleLine(String title) {
        this.title.setTitle(title);
    }

    @Override
    public HasText getName() {
        return name;
    }

    @Override
    public HasText getUri() {
        return uri;
    }

    @Override
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public String getImageUrl() {
        return imageUploader.getImageUrl();
    }

    @Override
    public void setIconUrl(String iconURL) {
        imageUploader.setImageUrl(iconURL);
    }

    @Override
    public HasText getDescription() {
        return description;
    }

    @Override
    public String getFullDescription() {
        return fullDescription.getHTML();
    }

    @Override
    public void setFullDescription(String fullDescription) {
        this.fullDescription.setHTML(fullDescription);
    }

    @Override
    public ProductDTO getSelectProduct() {
        return (ProductDTO) product.getSelectedObject().getO();
    }

    @Override
    public void setSelectedProduct(ProductDTO productDTO) {
        product.setSelectedObject(productDTO == null ? null : new SearchObject(productDTO.getName(), "", productDTO));
        product.setText(productDTO.getName());
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void setExtent(AoI extent) {
/*
        mapContainer.displayAoI(new AoIRectangle(extent));
*/
/*
        mapContainer.displayAoI(AoIUtil.fromWKT("POLYGON((" +
                extent.getEast() + " " + extent.getNorth() + "," +
                extent.getWest() + " " + extent.getNorth() + "," +
                extent.getWest() + " " + extent.getSouth() + "," +
                extent.getEast() + " " + extent.getSouth() + "," +
                extent.getEast() + " " + extent.getNorth() +
                "))"));
*/
        mapContainer.displayAoI(extent);
    }

    @Override
    public AoI getExtent() {
        return mapContainer.getAoi();
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        mapContainer.setMapLoadedHandler(mapLoadedHandler);
    }

}