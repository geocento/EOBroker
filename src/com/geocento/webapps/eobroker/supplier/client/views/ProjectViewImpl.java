package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.client.widgets.WidgetUtil;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.widgets.CompanyRoleWidget;
import com.geocento.webapps.eobroker.supplier.client.widgets.CompanyTextBox;
import com.geocento.webapps.eobroker.supplier.client.widgets.ProductProjectPitch;
import com.geocento.webapps.eobroker.supplier.client.widgets.ProductTextBox;
import com.geocento.webapps.eobroker.supplier.shared.dtos.CompanyRoleDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductProjectDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.richeditor.MaterialRichEditor;
import gwt.material.design.client.ui.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ProjectViewImpl extends Composite implements ProjectView {

    private Presenter presenter;

    interface ProjectViewUiBinder extends UiBinder<Widget, ProjectViewImpl> {
    }

    private static ProjectViewUiBinder ourUiBinder = GWT.create(ProjectViewUiBinder.class);
    @UiField
    MaterialTextBox name;
    @UiField
    MaterialButton submit;
    @UiField
    MaterialImageUploader imageUploader;
    @UiField
    MaterialTitle title;
    @UiField(provided = true)
    TemplateView template;
    @UiField
    MaterialTextArea description;
    @UiField
    MaterialRichEditor fullDescription;
    @UiField
    MaterialRow products;
    @UiField
    MaterialButton addProduct;
    @UiField
    MaterialTextBox companyRole;
    @UiField
    CompanyTextBox company;
    @UiField
    MaterialButton addCompany;
    @UiField
    MaterialRow consortium;
    @UiField
    ProductTextBox product;
    @UiField
    MaterialTextBox productPitch;
    @UiField
    MaterialDatePicker from;
    @UiField
    MaterialDatePicker until;

    public ProjectViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        template.setPlace("project");
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
    public void setTimeFrame(Date start, Date stop) {
        from.setDate(start);
        until.setDate(stop);
    }

    @Override
    public Date getTimeFrameFrom() {
        return from.getDate();
    }

    @Override
    public Date getTimeFrameUntil() {
        return until.getDate();
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
    public List<ProductProjectDTO> getSelectedProducts() {
        ArrayList<ProductProjectDTO> productProjectDTOs = new ArrayList<ProductProjectDTO>();
        for(int index = 0; index < products.getWidgetCount(); index++) {
            Widget widget = products.getWidget(index);
            if(widget instanceof ProductProjectPitch) {
                productProjectDTOs.add(((ProductProjectPitch) widget).getProductProjectDTO());
            }
        }
        return productProjectDTOs;
    }

    @Override
    public void setSelectedProducts(List<ProductProjectDTO> selectedProducts) {
        products.clear();
        if(selectedProducts == null || selectedProducts.size() == 0) {
            products.add(new MaterialLabel("No products added yet, use the add product button to add a product"));
        } else {
            for(ProductProjectDTO productProjectDTO : selectedProducts) {
                addProductProjectPitch(productProjectDTO);
            }
        }
    }

    private void addProductProjectPitch(ProductProjectDTO productProjectDTO) {
        // make sure we remove the text before
        if(getSelectedProducts().size() == 0) {
            products.clear();
        }
        final ProductProjectPitch productProjectPitch = new ProductProjectPitch();
        productProjectPitch.setProductProject(productProjectDTO);
        productProjectPitch.getRemove().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WidgetUtil.removeWidgets(products, new WidgetUtil.CheckValue() {
                    @Override
                    public boolean isValue(Widget widget) {
                        return widget instanceof ProductProjectPitch && ((ProductProjectPitch) widget) == productProjectPitch;
                    }
                });
            }
        });
        products.add(productProjectPitch);
    }

    @UiHandler("addProduct")
    void addProduct(ClickEvent clickEvent) {
        ProductDTO productDTO = product.getProduct();
        if(productDTO == null) {
            template.displayError("Please select a product");
            return;
        }
        String pitch = productPitch.getText();
        if(pitch.contentEquals("")) {
            template.displayError("Please provide a pitch for this product");
            return;
        }
        ProductProjectDTO productProjectDTO = new ProductProjectDTO();
        productProjectDTO.setProduct(productDTO);
        productProjectDTO.setPitch(pitch);
        addProductProjectPitch(productProjectDTO);
        // reset the values
        product.clearProduct();
        productPitch.setText("");
        productPitch.setFocus(false);
    }

    @Override
    public List<CompanyRoleDTO> getConsortium() {
        ArrayList<CompanyRoleDTO> companyRoles = new ArrayList<CompanyRoleDTO>();
        for(int index = 0; index < consortium.getWidgetCount(); index++) {
            Widget widget = consortium.getWidget(index);
            if(widget instanceof CompanyRoleWidget) {
                companyRoles.add(((CompanyRoleWidget) widget).getCompanyRole());
            }
        }
        return companyRoles;
    }

    @Override
    public void setConsortium(List<CompanyRoleDTO> companyRoleDTOs) {
        consortium.clear();
        if(companyRoleDTOs == null || companyRoleDTOs.size() == 0) {
            consortium.add(new MaterialLabel("No company added yet, use the add company button to add a company"));
        } else {
            for(CompanyRoleDTO companyRoleDTO : companyRoleDTOs) {
                addCompanyRole(companyRoleDTO);
            }
        }
    }

    private void addCompanyRole(CompanyRoleDTO companyRoleDTO) {
        // make sure we remove the text before
        if(getConsortium().size() == 0) {
            consortium.clear();
        }
        final CompanyRoleWidget companyRoleWidget = new CompanyRoleWidget(companyRoleDTO);
        companyRoleWidget.getRemove().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                WidgetUtil.removeWidgets(consortium, new WidgetUtil.CheckValue() {
                    @Override
                    public boolean isValue(Widget widget) {
                        return widget instanceof CompanyRoleWidget && ((CompanyRoleWidget) widget) == companyRoleWidget;
                    }
                });
            }
        });
        consortium.add(companyRoleWidget);
    }

    @UiHandler("addCompany")
    void addCompany(ClickEvent clickEvent) {
        CompanyDTO companyDTO = company.getCompany();
        if(companyDTO == null) {
            template.displayError("Please select a company");
            return;
        }
        String role = companyRole.getText();
        if(role.contentEquals("")) {
            template.displayError("Please provide a role for the company");
            return;
        }
        CompanyRoleDTO companyRoleDTO = new CompanyRoleDTO();
        companyRoleDTO.setCompanyDTO(companyDTO);
        companyRoleDTO.setRole(role);
        addCompanyRole(companyRoleDTO);
        // reset the values
        company.clearCompany();
        companyRole.setText("");
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

}