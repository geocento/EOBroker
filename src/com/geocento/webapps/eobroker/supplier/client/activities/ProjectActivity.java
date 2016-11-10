package com.geocento.webapps.eobroker.supplier.client.activities;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.ClientFactory;
import com.geocento.webapps.eobroker.supplier.client.places.ProjectPlace;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.supplier.client.views.ProjectView;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ProjectDTO;
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
public class ProjectActivity extends TemplateActivity implements ProjectView.Presenter {

    private ProjectView projectView;

    private ProjectDTO projectDTO;

    public ProjectActivity(ProjectPlace place, ClientFactory clientFactory) {
        super(clientFactory);
        this.place = place;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        super.start(panel, eventBus);
        projectView = clientFactory.getProjectView();
        projectView.setPresenter(this);
        setTemplateView(projectView.getTemplateView());
        panel.setWidget(projectView.asWidget());
        Window.setTitle("Earth Observation Broker");
        bind();
        handleHistory();
    }

    private void handleHistory() {
        HashMap<String, String> tokens = Utils.extractTokens(place.getToken());

        Long projectId = null;
        if(tokens.containsKey(ProjectPlace.TOKENS.id.toString())) {
            projectId = Long.parseLong(tokens.get(ProjectPlace.TOKENS.id.toString()));
        }
        if(projectId != null) {
            try {
                REST.withCallback(new MethodCallback<ProjectDTO>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, ProjectDTO response) {
                        setProject(response);
                    }
                }).call(ServicesUtil.assetsService).getProject(projectId);
            } catch (RequestException e) {
            }
        } else {
            ProjectDTO projectDTO = new ProjectDTO();
            setProject(projectDTO);
        }
    }

    private void setProject(ProjectDTO projectDTO) {
        this.projectDTO = projectDTO;
        projectView.setTitleLine(projectDTO.getId() == null ? "Create project" : "Edit project");
        projectView.getName().setText(projectDTO.getName());
        projectView.setIconUrl(projectDTO.getImageUrl());
        projectView.getDescription().setText(projectDTO.getDescription());
        projectView.setFullDescription(projectDTO.getFullDescription());
        projectView.setSelectedProducts(projectDTO.getProducts());
    }

    @Override
    protected void bind() {
        super.bind();

        handlers.add(projectView.getSubmit().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                projectDTO.setName(projectView.getName().getText());
                projectDTO.setImageUrl(projectView.getImageUrl());
                projectDTO.setDescription(projectView.getDescription().getText());
                projectDTO.setFullDescription(projectView.getFullDescription());
                projectDTO.setProducts(projectView.getSelectedProducts());
                displayLoading("Saving project...");
                try {
                    REST.withCallback(new MethodCallback<Long>() {
                        @Override
                        public void onFailure(Method method, Throwable exception) {
                            hideLoading();
                            displayError("Error saving project");
                        }

                        @Override
                        public void onSuccess(Method method, Long companyId) {
                            hideLoading();
                            displaySuccess("Project saved");
                            projectDTO.setId(companyId);
                        }
                    }).call(ServicesUtil.assetsService).saveProject(projectDTO);
                } catch (RequestException e) {
                }
            }
        }));
    }

}
