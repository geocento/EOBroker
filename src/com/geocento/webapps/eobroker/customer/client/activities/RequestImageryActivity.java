package com.geocento.webapps.eobroker.customer.client.activities;

import com.geocento.webapps.eobroker.common.client.widgets.maps.AoIUtil;
import com.geocento.webapps.eobroker.common.shared.entities.ImageService;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.AoIDTO;
import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactory;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.RequestCreated;
import com.geocento.webapps.eobroker.customer.client.places.RequestImageryPlace;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.views.RequestImageryView;
import com.geocento.webapps.eobroker.customer.shared.ImageRequestDTO;
import com.google.gwt.core.client.Callback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.Date;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class RequestImageryActivity extends TemplateActivity implements RequestImageryView.Presenter {

    private RequestImageryView requestImageryView;

    public RequestImageryActivity(RequestImageryPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        requestImageryView = clientFactory.getRequestImageryView();
        requestImageryView.setPresenter(this);
        panel.setWidget(requestImageryView.asWidget());
        setTemplateView(requestImageryView.getTemplateView());
        Window.setTitle("Earth Observation Broker");
        bind();
        requestImageryView.setMapLoadedHandler(new Callback<Void, Exception>() {
            @Override
            public void onFailure(Exception reason) {
                Window.alert("Error " + reason.getMessage());
            }

            @Override
            public void onSuccess(Void result) {
                try {
                    REST.withCallback(new MethodCallback<List<ImageService>>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {

                        }

                        @Override
                        public void onSuccess(Method method, List<ImageService> imageServices) {
                            requestImageryView.setSuppliers(imageServices);
                            handleHistory();
                        }
                    }).call(ServicesUtil.assetsService).getImageServices();
                } catch (RequestException e) {
                }
            }
        });
    }

    private void handleHistory() {
        setAoI(Customer.currentAoI);
    }

    public void setAoI(AoIDTO aoi) {
        super.setAoI(aoi);
        requestImageryView.displayAoI(aoi);
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(requestImageryView.getSubmitButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                submitRequest();
            }
        }));
   }

    private void submitRequest() {
        String imageType = requestImageryView.getImageType();
        if(imageType.length() == 0) {
            requestImageryView.displayFormError("Please provide a description of the imagery type");
            return;
        }
        Date start = requestImageryView.getStartDate();
        Date stop = requestImageryView.getStopDate();
        if(start == null || stop == null) {
            requestImageryView.displayFormError("Please provide a start and stop time");
            return;
        }
        String application = requestImageryView.getApplication();
        String additionalInformation = requestImageryView.getAdditionalInformation();
        List<ImageService> imageServices = requestImageryView.getSelectedServices();
        if(imageServices.size() == 0) {
            requestImageryView.displayFormError("Please select at least one supplier service");
            return;
        }
        ImageRequestDTO imageRequestDTO = new ImageRequestDTO();
        imageRequestDTO.setAoiWKT(AoIUtil.toWKT(currentAoI));
        imageRequestDTO.setImageType(imageType);
        imageRequestDTO.setStart(start);
        imageRequestDTO.setStop(stop);
        imageRequestDTO.setApplication(application);
        imageRequestDTO.setAdditionalInformation(additionalInformation);
        imageRequestDTO.setImageServiceIds(ListUtil.mutate(imageServices, new ListUtil.Mutate<ImageService, Long>() {
            @Override
            public Long mutate(ImageService imageService) {
                return imageService.getId();
            }
        }));
        try {
            requestImageryView.displaySubmitLoading(true);
            REST.withCallback(new MethodCallback<RequestDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    requestImageryView.displaySubmitLoading(false);
                    Window.alert("Could not submit image request");
                }

                @Override
                public void onSuccess(Method method, RequestDTO requestDTO) {
                    requestImageryView.displaySubmitLoading(false);
                    requestImageryView.displaySucces("Request submitted");
                    clientFactory.getEventBus().fireEvent(new RequestCreated(requestDTO));
                }
            }).call(ServicesUtil.ordersService).submitImageRequest(imageRequestDTO);
        } catch (RequestException e) {
        }
    }

    @Override
    public void aoiChanged(AoIDTO aoi) {
        setAoI(aoi);
    }
}
