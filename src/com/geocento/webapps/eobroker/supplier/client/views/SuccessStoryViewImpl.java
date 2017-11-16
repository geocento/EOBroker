package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialImageUploader;
import com.geocento.webapps.eobroker.common.client.widgets.WidgetUtil;
import com.geocento.webapps.eobroker.common.client.widgets.material.MaterialSearch;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.widgets.*;
import com.geocento.webapps.eobroker.supplier.shared.dtos.*;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.base.SearchObject;
import gwt.material.design.client.ui.*;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class SuccessStoryViewImpl extends Composite implements SuccessStoryView {

    private Presenter presenter;

    interface ProjectViewUiBinder extends UiBinder<Widget, SuccessStoryViewImpl> {
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
    com.geocento.webapps.eobroker.common.client.widgets.material.MaterialRichEditor fullDescription;
    @UiField
    MaterialRow consortium;
    @UiField
    MaterialDatePicker date;
    @UiField
    CompanyTextBox customer;
    @UiField
    MaterialLink viewClient;
    @UiField
    MaterialRow offeringsUsed;
    @UiField
    MaterialRow endorsements;
    @UiField
    ProductTextBox productCategory;
    @UiField
    MaterialSearch offeringsSearch;

    public SuccessStoryViewImpl(ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        offeringsSearch.setPresenter(new MaterialSearch.Presenter() {

            private long lastCall = 0;

            @Override
            public void textChanged(String text) {
                if(text == null || text.length() == 0) {
                    offeringsSearch.setValue(null);
                } else {
                    this.lastCall++;

                    final long currentCall = this.lastCall;

                    try {
                        REST.withCallback(new MethodCallback<List<Suggestion>>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                            }

                            @Override
                            public void onSuccess(Method method, List<Suggestion> response) {
                                // show only if last one to be called
                                if (currentCall == lastCall) {
                                    offeringsSearch.setFocus(true);
                                    List<SearchObject> results = ListUtil.mutate(response, new ListUtil.Mutate<Suggestion, SearchObject>() {
                                        @Override
                                        public SearchObject mutate(Suggestion suggestion) {
                                            SearchObject searchObject = new SearchObject(CategoryUtils.getIconType(suggestion.getCategory()), suggestion.getName(), suggestion.getUri());
                                            searchObject.setO(suggestion);
                                            return searchObject;
                                        }
                                    });
                                    offeringsSearch.displayListSearches(results);
                                }
                            }
                        }).call(ServicesUtil.assetsService).listOfferings(text);
                    } catch (RequestException e) {
                    }
                }
            }

            @Override
            public void suggestionSelected(SearchObject searchObject) {
                try {
                    // add offering to list of offerings
                    Suggestion suggestion = (Suggestion) searchObject.getO();
                    Long offeringId = Long.parseLong(suggestion.getUri());
                    switch (suggestion.getCategory()) {
                        case productdatasets: {
                            REST.withCallback(new MethodCallback<ProductDatasetDTO>() {
                                @Override
                                public void onFailure(Method method, Throwable exception) {
                                }

                                @Override
                                public void onSuccess(Method method, ProductDatasetDTO productDatasetDTO) {
                                    // check if not a duplicate
                                    ProductDatasetDTO dataset = ListUtil.findValue(getDatasets(), new ListUtil.CheckValue<ProductDatasetDTO>() {
                                        @Override
                                        public boolean isValue(ProductDatasetDTO value) {
                                            return value.getId().equals(offeringId);
                                        }
                                    });
                                    if(dataset == null) {
                                        addDatasetOffering(productDatasetDTO);
                                    }
                                }
                            }).call(ServicesUtil.assetsService).getProductDataset(offeringId);
                        } break;
                        case productservices: {
                            REST.withCallback(new MethodCallback<ProductServiceDTO>() {
                                @Override
                                public void onFailure(Method method, Throwable exception) {
                                }

                                @Override
                                public void onSuccess(Method method, ProductServiceDTO productServiceDTO) {
                                    // check if not a duplicate
                                    ProductServiceDTO serviceDTO = ListUtil.findValue(getServices(), new ListUtil.CheckValue<ProductServiceDTO>() {
                                        @Override
                                        public boolean isValue(ProductServiceDTO value) {
                                            return value.getId().equals(offeringId);
                                        }
                                    });
                                    if(serviceDTO == null) {
                                        addServiceOffering(serviceDTO);
                                    }
                                }
                            }).call(ServicesUtil.assetsService).getProductDataset(offeringId);
                        } break;
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void textSelected(String text) {

            }
        });

        template.setPlace("stories");
    }

    private void addOffering(Widget widget) {
        MaterialColumn materialColumn = new MaterialColumn(12, 4, 3);
        materialColumn.add(widget);
        offeringsUsed.add(materialColumn);
    }

    private void addServiceOffering(ProductServiceDTO serviceDTO) {
        addOffering(new ProductServiceWidget(serviceDTO));
    }

    private void addDatasetOffering(ProductDatasetDTO productDatasetDTO) {
        addOffering(new ProductDatasetWidget(productDatasetDTO));
    }

    private void addSoftwareOffering(SoftwareDTO softwareDTO) {
        addOffering(new SoftwareWidget(softwareDTO));
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
    public void setDate(Date date) {
        this.date.setDate(date);
    }

    @Override
    public Date getDate() {
        return date.getDate();
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

    @Override
    public HasClickHandlers getViewClient() {
        return viewClient;
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public void setCustomer(CompanyDTO customer) {
        this.customer.setCompany(customer);
    }

    @Override
    public CompanyDTO getCustomer() {
        return this.customer.getCompany();
    }

    @Override
    public void setEndorsements(List<EndorsementDTO> endorsements) {
        this.endorsements.clear();
        if(endorsements == null || endorsements.size() == 0) {
            this.endorsements.add(new MaterialLabel("No endorsement yet..."));
        } else {
            for(EndorsementDTO endorsementDTO : endorsements) {
                MaterialColumn materialColumn = new MaterialColumn(12, 12, 6);
                materialColumn.add(new EndorsementWidget(endorsementDTO));
                this.endorsements.add(materialColumn);
            }
        }
    }

    @Override
    public void setProductCategory(ProductDTO productDTO) {
        this.productCategory.setProduct(productDTO);
    }

    @Override
    public ProductDTO getProductCategory() {
        return this.productCategory.getProduct();
    }

    @Override
    public void setOfferings(List<ProductDatasetDTO> datasetDTOs, List<ProductServiceDTO> serviceDTOs, List<SoftwareDTO> softwareDTOs) {
        offeringsUsed.clear();
        for(ProductDatasetDTO productDatasetDTO : datasetDTOs) {
            addDatasetOffering(productDatasetDTO);
        }
        for(ProductServiceDTO productServiceDTO : serviceDTOs) {
            addServiceOffering(productServiceDTO);
        }
        for(SoftwareDTO softwareDTO : softwareDTOs) {
            addSoftwareOffering(softwareDTO);
        }
    }

    @Override
    public List<ProductServiceDTO> getServices() {
        List<ProductServiceDTO> services = new ArrayList<ProductServiceDTO>();
        for(Widget widget : offeringsUsed.getChildrenList()) {
            if(widget instanceof MaterialColumn) {
                ProductServiceWidget productServiceWidget = (ProductServiceWidget) WidgetUtil.findChild((MaterialColumn) widget, new WidgetUtil.CheckValue() {
                    @Override
                    public boolean isValue(Widget widget) {
                        return widget instanceof ProductServiceWidget;
                    }
                });
                if(productServiceWidget != null) {
                    services.add(productServiceWidget.getProductService());
                }
            }
        }
        return services;
    }

    @Override
    public List<ProductDatasetDTO> getDatasets() {
        List<ProductDatasetDTO> datasets = new ArrayList<ProductDatasetDTO>();
        for(Widget widget : offeringsUsed.getChildrenList()) {
            if(widget instanceof MaterialColumn) {
                ProductDatasetWidget productDatasetWidget = (ProductDatasetWidget) WidgetUtil.findChild((MaterialColumn) widget, new WidgetUtil.CheckValue() {
                    @Override
                    public boolean isValue(Widget widget) {
                        return widget instanceof ProductDatasetWidget;
                    }
                });
                if(productDatasetWidget != null) {
                    datasets.add(productDatasetWidget.getProductDataset());
                }
            }
        }
        return datasets;
    }

    @Override
    public List<SoftwareDTO> getSoftware() {
        return null;
    }

}