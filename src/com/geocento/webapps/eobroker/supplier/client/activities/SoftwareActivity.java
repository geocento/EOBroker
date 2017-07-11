package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.SoftwarePlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.SoftwareView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProductSoftwareDTO;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SoftwareDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class SoftwareActivity extends TemplateActivity implements SoftwareView.Presenter {

    private SoftwareView softwareView;

    private SoftwareDTO softwareDTO;

    public SoftwareActivity(SoftwarePlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        softwareView = clientFactory.getSoftwareView();
        softwareView.setPresenter(this);
        setTemplateView(softwareView.getTemplateView());
        panel.setWidget(softwareView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        Long softwareId = null;
        if(tokens.containsKey(SoftwarePlace.TOKENS.id.toString())) {
            softwareId = Long.parseLong(tokens.get(SoftwarePlace.TOKENS.id.toString()));
        }
        if(softwareId != null) {
            try {
                displayFullLoading("loading software information...");
                REST.withCallback(new MethodCallback<SoftwareDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideFullLoading();
                        Window.alert("Problem loading software");
                    }

                    @Override
                    public void onSuccess(Method method, SoftwareDTO response) {
                        hideFullLoading();
                        setSoftware(response);
                    }
                }).call(ServicesUtil.assetsService).getSoftware(softwareId);
            } catch (RequestException e) {
            }
        } else {
            SoftwareDTO softwareDTO = new SoftwareDTO();
            setSoftware(softwareDTO);
        }
    }

    private void setSoftware(SoftwareDTO softwareDTO) {
        this.softwareDTO = softwareDTO;
        softwareView.setTitleLine(softwareDTO.getId() == null ? "Create software" : "Edit software");
        softwareView.getName().setText(softwareDTO.getName());
        softwareView.setIconUrl(softwareDTO.getImageUrl());
        softwareView.getDescription().setText(softwareDTO.getDescription());
        softwareView.setSoftwareType(softwareDTO.getSoftwareType());
        softwareView.setFullDescription(softwareDTO.getFullDescription());
        softwareView.setSelectedProducts(softwareDTO.getProducts());
        softwareView.setTermsAndConditions(softwareDTO.getTermsAndConditions());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(softwareView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                softwareDTO.setName(softwareView.getName().getText());
                softwareDTO.setImageUrl(softwareView.getImageUrl());
                softwareDTO.setDescription(softwareView.getDescription().getText());
                softwareDTO.setSoftwareType(softwareView.getSoftwareType());
                softwareDTO.setFullDescription(softwareView.getFullDescription());
                softwareDTO.setProducts(softwareView.getSelectedProducts());
                softwareDTO.setTermsAndConditions(softwareView.getTermsAndConditions());
                // do some checks
                try {
                    if (softwareDTO.getName() == null || softwareDTO.getName().length() < 3) {
                        throw new Exception("Please provide a valid name");
                    }
                    if (softwareDTO.getImageUrl() == null) {
                        throw new Exception("Please provide a picture");
                    }
                    if (softwareDTO.getDescription() == null || softwareDTO.getDescription().length() < 3) {
                        throw new Exception("Please provide a valid description");
                    }
                    if(softwareDTO.getSoftwareType() == null) {
                        throw new Exception("Please provide a software type");
                    }
                    if (softwareDTO.getFullDescription() == null || softwareDTO.getFullDescription().length() < 3) {
                        throw new Exception("Please provide a valid full description");
                    }
                    if (softwareDTO.getProducts() == null) {
                        throw new Exception("Please select at least one product");
                    } else {
                        softwareDTO.setProducts(ListUtil.filterValues(softwareDTO.getProducts(), new ListUtil.CheckValue<ProductSoftwareDTO>() {
                            @Override
                            public boolean isValue(ProductSoftwareDTO value) {
                                return value.getPitch().length() > 0 && value.getProduct() != null;
                            }
                        }));
                        if (softwareDTO.getProducts().size() == 0) {
                            throw new Exception("Please select at least one product or fill in the missing fields");
                        }
                    }
                } catch (Exception e) {
                    // TODO - use the view instead
                    Window.alert(e.getMessage());
                    return;
                }
                displayLoading("Saving software...");
                try {
                    REST.withCallback(new MethodCallback<Long>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Error saving software");
                        }

                        @Override
                        public void onSuccess(Method method, Long companyId) {
                            hideLoading();
                            displaySuccess("Software saved");
                            softwareDTO.setId(companyId);
                        }
                    }).call(ServicesUtil.assetsService).updateSoftware(softwareDTO);
                } catch (RequestException e) {
                }
            }
        }));

        handlers.add(softwareView.getViewClient().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                // TODO - how do we get to use the place instead?
                Window.open(GWT.getHostPageBaseURL() + "#fullview:softwareid=" + softwareDTO.getId(), "_fullview;", null);
            }
        }));
    }

}
