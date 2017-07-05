package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageLoading;
import com.geocento.webapps.eobroker.common.client.widgets.forms.ElementEditor;
import com.geocento.webapps.eobroker.common.client.widgets.forms.FormHelper;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElement;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.FormElementValue;
import com.geocento.webapps.eobroker.common.shared.entities.formelements.TextFormElement;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.client.places.ProductFormPlace;
import com.geocento.webapps.eobroker.customer.client.widgets.maps.MapContainer;
import com.geocento.webapps.eobroker.customer.shared.ProductDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductFormDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceDTO;
import com.geocento.webapps.eobroker.customer.shared.ProductServiceFormDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProductFormViewImpl extends Composite implements ProductFormView {

    interface ProductFormUiBinder extends UiBinder<Widget, ProductFormViewImpl> {
    }

    private static ProductFormUiBinder ourUiBinder = GWT.create(ProductFormUiBinder.class);

    static public interface Style extends CssResource {

        String editor();
    }

    @UiField
    Style style;

    @UiField
    HTMLPanel formContainer;
    @UiField
    HTMLPanel productServices;
    @UiField
    MaterialImageLoading image;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialButton submit;
    @UiField
    HTMLPanel mapPanel;
    @UiField
    MapContainer mapContainer;
    @UiField
    MaterialLabel comment;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialNavBar navigation;
    @UiField
    MaterialPanel actions;
    @UiField
    HTMLPanel suppliers;
    @UiField
    MaterialPanel colorPanel;
    @UiField
    HTMLPanel genericComment;

    private Presenter presenter;

    private ClientFactoryImpl clientFactory;

    public ProductFormViewImpl(ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        mapContainer.setPresenter(new MapContainer.Presenter() {
            @Override
            public void aoiChanged(AoIDTO aoi) {
                displayAoI(aoi);
            }
        });
    }

    @Override
    public void displayAoI(AoIDTO aoi) {
        mapContainer.displayAoI(aoi);
        mapContainer.centerOnAoI();
    }

    @Override
    public void setMapLoadedHandler(Callback<Void, Exception> mapLoadedHandler) {
        mapContainer.setMapLoadedHandler(mapLoadedHandler);
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void clearForm() {
        formContainer.clear();
    }

    private void addFormElement(FormElement formElement) {
        ElementEditor editor = createEditor(formElement);
        editor.addStyleName(style.editor());
        formContainer.add(editor);
    }

    private ElementEditor createEditor(final FormElement formElement) {
        return FormHelper.createEditor(formElement);
    }

    private void clearSuppliers() {
        productServices.clear();
    }

    private void setComment(String comment) {
        this.comment.setText(comment);
    }

    @Override
    public void setProduct(ProductFormDTO productFormDTO) {
        navigation.setVisible(false);
        image.setImageUrl(productFormDTO.getImageUrl());
        title.setText(productFormDTO.getName());
        description.setText(productFormDTO.getDescription());
        setFormElements(productFormDTO.getFormFields());
        // add choice of suppliers
        suppliers.setVisible(true);
        clearSuppliers();
        for (ProductServiceDTO productServiceDTO : productFormDTO.getProductServices()) {
            MaterialCheckBox materialCheckBox =
                    new MaterialCheckBox("<span style='display: inline;'><b>" + productServiceDTO.getName() + "</b> " +
                            "by <img style='max-height: 24px; vertical-align: middle;' src='" + productServiceDTO.getCompanyLogo() + "'/> <b>" + productServiceDTO.getCompanyName() + "</b></span>", true);
            materialCheckBox.getElement().getStyle().setMarginBottom(10, com.google.gwt.dom.client.Style.Unit.PX);
            materialCheckBox.setObject(productServiceDTO);
            materialCheckBox.setValue(true);
            productServices.add(materialCheckBox);
        }
        setComment(productFormDTO.getProductServices().size() + " services available for this product category");
        // add generic comment
        genericComment.clear();
        genericComment.add(new MaterialLabel("Click on the submit button to send your request to all selected suppliers."));
        // add actions
        actions.clear();
        MaterialAnchorButton information = new MaterialAnchorButton("Information");
        actions.add(information);
        information.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Window.open("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.productid.toString() + "=" + productFormDTO.getId())), "_blank", null);
            }
        });
    }

    @Override
    public void setProductService(ProductServiceFormDTO productServiceFormDTO) {
        navigation.setVisible(true);
        navigation.clear();
        navigation.setBackgroundColor(CategoryUtils.getColor(Category.productservices));
        colorPanel.setBackgroundColor(CategoryUtils.getColor(Category.productservices));
        addBreadcrumb(productServiceFormDTO.getCompanyDTO());
        addBreadcrumb(productServiceFormDTO.getProduct());
        image.setImageUrl(productServiceFormDTO.getServiceImage());
        title.setText(productServiceFormDTO.getName());
        description.setText(productServiceFormDTO.getDescription());
        setFormElements(productServiceFormDTO.getFormFields());
        // add choice of suppliers
        suppliers.setVisible(false);
        clearSuppliers();
        // add generic comment
        genericComment.clear();
        genericComment.add(new HTML("Click on the submit button to send your request to the supplier. " +
                "You can also send your request to <a href='#" + PlaceHistoryHelper.convertPlace(
                new ProductFormPlace(Utils.generateTokens(ProductFormPlace.TOKENS.id.toString(), productServiceFormDTO.getProduct().getId() + "")))
                + "'>multiple suppliers</a> at the same time."
        ));
        // add actions
        actions.clear();
        MaterialAnchorButton information = new MaterialAnchorButton("Information");
        actions.add(information);
        information.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Window.open("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(FullViewPlace.TOKENS.productserviceid.toString() + "=" + productServiceFormDTO.getId())), "_blank", null);
            }
        });
    }

    private void setFormElements(List<FormElement> formFields) {
        clearForm();
        for (FormElement formElement : formFields) {
            addFormElement(formElement);
        }
        // add text for comment
        TextFormElement additionalInformation = new TextFormElement();
        additionalInformation.setName("Additional information");
        additionalInformation.setDescription("Please add any additional information you deem important");
        addFormElement(additionalInformation);
    }

    @Override
    public HasClickHandlers getSubmit() {
        return submit;
    }

    @Override
    public List<FormElementValue> getFormElementValues() throws Exception {
        List<FormElementValue> formElementValues = new ArrayList<FormElementValue>();
        for(int index = 0; index < formContainer.getWidgetCount(); index++) {
            Widget widget = formContainer.getWidget(index);
            if(widget instanceof ElementEditor) {
                formElementValues.add(((ElementEditor) widget).getFormElementValue());
            }
        }
        return formElementValues;
    }

    @Override
    public void displayFormValidationError(String message) {
        Window.alert("Problem with your form " + message);
    }

    @Override
    public List<ProductServiceDTO> getSelectedServices() {
        List<ProductServiceDTO> productServiceDTOs = new ArrayList<ProductServiceDTO>();
        for(int index = 0; index < productServices.getWidgetCount(); index++) {
            Widget widget = productServices.getWidget(index);
            if(widget instanceof MaterialCheckBox) {
                MaterialCheckBox materialCheckBox = (MaterialCheckBox) widget;
                if(((MaterialCheckBox) widget).getValue()) {
                    productServiceDTOs.add((ProductServiceDTO) materialCheckBox.getObject());
                }
            }
        }
        return productServiceDTOs;
    }

    @Override
    public void clearRequest() {
        // clear form elements
        for(int index = 0; index < formContainer.getWidgetCount(); index++) {
            Widget widget = formContainer.getWidget(index);
            if(widget instanceof ElementEditor) {
                ((ElementEditor) widget).resetValue();
            }
        }
        // clear services selection
        for(int index = 0; index < productServices.getWidgetCount(); index++) {
            Widget widget = productServices.getWidget(index);
            if(widget instanceof MaterialCheckBox) {
                ((MaterialCheckBox) widget).setValue(false);
            }
        }
    }

    private void addBreadcrumb(Object dto) {
        navigation.setVisible(true);
        MaterialBreadcrumb materialBreadcrumb = new MaterialBreadcrumb();
        Color color = Color.WHITE; //CategoryUtils.getColor(category);
        materialBreadcrumb.setTextColor(color);
        materialBreadcrumb.setIconColor(color);
        String token = "";
        IconType iconType = IconType.ERROR;
        String text = "Unknown";
        String id = null;
        if(dto instanceof CompanyDTO) {
            token = FullViewPlace.TOKENS.companyid.toString();
            iconType = CategoryUtils.getIconType(Category.companies);
            text = ((CompanyDTO) dto).getName();
            id = ((CompanyDTO) dto).getId() + "";
        } else if(dto instanceof ProductDTO) {
            token = FullViewPlace.TOKENS.productid.toString();
            iconType = CategoryUtils.getIconType(Category.products);
            text = ((ProductDTO) dto).getName();
            id = ((ProductDTO) dto).getId() + "";
        }
        materialBreadcrumb.setIconType(iconType);
        materialBreadcrumb.setText(text);
        materialBreadcrumb.setHref("#" + PlaceHistoryHelper.convertPlace(new FullViewPlace(token + "=" + id)));
        navigation.add(materialBreadcrumb);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}