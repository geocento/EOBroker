package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveProject;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveProjectHandler;
import com.geocento.webapps.eobroker.supplier.client.places.ProjectPlace;
import com.geocento.webapps.eobroker.supplier.client.places.ProjectsPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.DashboardView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.OfferDTO;
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
public class ProjectsActivity extends TemplateActivity implements DashboardView.Presenter {

    private DashboardView dashboardView;

    public CompanyDTO companyDTO;

    public ProjectsActivity(ProjectsPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        dashboardView = clientFactory.getDashboardView();
        dashboardView.setPresenter(this);
        setTemplateView(dashboardView.getTemplateView());
        panel.setWidget(dashboardView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        loadProjects();
    }

    private void loadProjects() {
        try {
            displayFullLoading("Loading projects...");
            REST.withCallback(new MethodCallback<OfferDTO>() {
                @Override
                public void onFailure(Method method, Throwable exception) {
                    hideFullLoading();
                    Window.alert("Error loading projects...");
                }

                @Override
                public void onSuccess(Method method, OfferDTO response) {
                    hideFullLoading();
                    dashboardView.displayProjects(response.getProjectDTOs());
                }

            }).call(ServicesUtil.assetsService).getOffer(Category.project);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void bind() {
        super.bind();

        activityEventBus.addHandler(RemoveProject.TYPE, new RemoveProjectHandler() {
            @Override
            public void onRemoveProject(RemoveProject event) {
                if (Window.confirm("Are you sure you want to remove this project?")) {
                    try {
                        REST.withCallback(new MethodCallback<Void>() {
                            @Override
                            public void onFailure(Method method, Throwable exception) {
                                displayError("Could not remove this project");
                            }

                            @Override
                            public void onSuccess(Method method, Void response) {
                                displaySuccess("Project has been removed");
                                // reload services
                                loadProjects();
                            }
                        }).call(ServicesUtil.assetsService).removeProject(event.getProjectId());
                    } catch (Exception e) {

                    }
                }
            }
        });

        handlers.add(dashboardView.getAddProject().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new ProjectPlace());
            }
        }));

    }

}
