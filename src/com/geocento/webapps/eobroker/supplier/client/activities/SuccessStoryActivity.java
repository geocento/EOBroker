package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.supplier.client.places.SuccessStoryPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.SuccessStoryView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SuccessStoryEditDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.HashMap;

/**
 * Created by thomas on 09/05/2016.
 */
public class SuccessStoryActivity extends TemplateActivity implements SuccessStoryView.Presenter {

    private SuccessStoryView successStoryView;

    private SuccessStoryEditDTO successStoryEditDTO;

    public SuccessStoryActivity(SuccessStoryPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        successStoryView = clientFactory.getSuccessStoryView();
        successStoryView.setPresenter(this);
        setTemplateView(successStoryView.getTemplateView());
        panel.setWidget(successStoryView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        successStoryView.setTitleLine("Edit your success story information");
        Long successStoryId = null;
        if(tokens.containsKey(SuccessStoryPlace.TOKENS.id.toString())) {
            try {
                successStoryId = Long.parseLong(tokens.get(SuccessStoryPlace.TOKENS.id.toString()));
            } catch (Exception e) {
            }
        }
        if(successStoryId != null) {
            try {
                displayFullLoading("Loading success story...");
                REST.withCallback(new MethodCallback<SuccessStoryEditDTO>() {

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        hideFullLoading();
                        Window.alert("Problem loading success story");
                    }

                    @Override
                    public void onSuccess(Method method, SuccessStoryEditDTO successStoryEditDTO) {
                        hideFullLoading();
                        setSuccessStory(successStoryEditDTO);
                    }

                }).call(ServicesUtil.assetsService).getSuccessStory(successStoryId);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        } else {
            setSuccessStory(new SuccessStoryEditDTO());
        }
    }

    private void setSuccessStory(SuccessStoryEditDTO successStoryEditDTO) {
        this.successStoryEditDTO = successStoryEditDTO;
        successStoryView.getName().setText(successStoryEditDTO.getName());
        successStoryView.setIconUrl(successStoryEditDTO.getImageUrl());
        successStoryView.setCustomer(successStoryEditDTO.getCustomer());
        successStoryView.setEndorsements(successStoryEditDTO.getEndorsements());
        successStoryView.setProductCategory(successStoryEditDTO.getProductDTO());
        successStoryView.setOfferings(successStoryEditDTO.getDatasetDTOs(), successStoryEditDTO.getServiceDTOs(), successStoryEditDTO.getSoftwareDTOs());
        successStoryView.getDescription().setText(successStoryEditDTO.getDescription());
        successStoryView.setFullDescription(successStoryEditDTO.getFullDescription());
        successStoryView.getPeriod().setText(successStoryEditDTO.getPeriod());
        successStoryView.setConsortium(successStoryEditDTO.getConsortium());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(successStoryView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                successStoryEditDTO.setImageUrl(successStoryView.getImageUrl());
                successStoryEditDTO.setName(successStoryView.getName().getText());
                successStoryEditDTO.setDescription(successStoryView.getDescription().getText());
                successStoryEditDTO.setFullDescription(successStoryView.getFullDescription());
                successStoryEditDTO.setPeriod(successStoryView.getPeriod().getText());
                successStoryEditDTO.setCustomer(successStoryView.getCustomer());
                successStoryEditDTO.setProductDTO(successStoryView.getProductCategory());
                successStoryEditDTO.setServiceDTOs(successStoryView.getServices());
                successStoryEditDTO.setDatasetDTOs(successStoryView.getDatasets());
                successStoryEditDTO.setSoftwareDTOs(successStoryView.getSoftware());
                // validate inputs
                try {
                    if(successStoryEditDTO.getName() == null || successStoryEditDTO.getName().length() == 0) {
                        throw new Exception("Please specify a name");
                    }
                    if (successStoryEditDTO.getCustomer() == null) {
                        throw new Exception("Please specify a customer");
                    }
                    if(successStoryEditDTO.getDescription() == null || successStoryEditDTO.getDescription().length() == 0) {
                        throw new Exception("Please specify a description");
                    }
                    if(successStoryEditDTO.getFullDescription() == null || successStoryEditDTO.getFullDescription().length() == 0) {
                        throw new Exception("Please specify a full description");
                    }
                    if(successStoryEditDTO.getProductDTO() == null) {
                        throw new Exception("Please specify a product category");
                    }
                    if(successStoryEditDTO.getPeriod() == null) {
                        throw new Exception("Please specify a period");
                    }
                } catch (Exception e) {
                    displayError(e.getMessage());
                    return;
                }
                try {
                    displayLoading("Saving success story...");
                    REST.withCallback(new MethodCallback<Long>() {

                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError(method.getResponse().getText());
                        }

                        @Override
                        public void onSuccess(Method method, Long result) {
                            hideLoading();
                            displaySuccess("Success story saved");
                            History.newItem(PlaceHistoryHelper.convertPlace(new SuccessStoryPlace(
                                    Utils.generateTokens(SuccessStoryPlace.TOKENS.id.toString(), result.toString()))));
                        }

                    }).call(ServicesUtil.assetsService).updateSuccessStory(successStoryEditDTO);
                } catch (RequestException e) {
                    e.printStackTrace();
                }
            }
        }));

        handlers.add(successStoryView.getViewClient().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Window.open(GWT.getHostPageBaseURL() + "#story:id=" + successStoryEditDTO.getId(), "_story;", null);
            }
        }));
    }

}
