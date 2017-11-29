package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.widgets.ChallengesList;
import com.geocento.webapps.eobroker.admin.client.widgets.SimpleFileUpload;
import com.geocento.webapps.eobroker.admin.shared.dtos.ChallengeDTO;
import com.geocento.webapps.eobroker.common.client.widgets.material.MaterialFileUploader;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialTextBox;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class ChallengesViewImpl extends Composite implements ChallengesView {

    interface ChallengesViewUiBinder extends UiBinder<Widget, ChallengesViewImpl> {
    }

    private static ChallengesViewUiBinder ourUiBinder = GWT.create(ChallengesViewUiBinder.class);

    public static interface Style extends CssResource {
    }

    @UiField
    Style style;

    @UiField(provided = true)
    TemplateView template;
    @UiField
    ChallengesList challenges;
    @UiField
    MaterialButton createNew;
    @UiField
    MaterialTextBox filter;
    @UiField
    FileUpload importCSVUpload;
    @UiField
    FormPanel fileForm;
    @UiField
    SimpleFileUpload importCSV;

    private Presenter presenter;

    public ChallengesViewImpl(final ClientFactoryImpl clientFactory) {

        template = new TemplateView(clientFactory);

        initWidget(ourUiBinder.createAndBindUi(this));

        challenges.setPresenter(() -> presenter.loadMore());

        importCSV.setUrl(GWT.getHostPageBaseURL() + "admin/api/upload/challenges/import");
        importCSV.addSuccessHandler(event -> presenter.reloadChallenges());

    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasClickHandlers getCreateNewButton() {
        return createNew;
    }

    @Override
    public void clearChallenges() {
        this.challenges.clearData();
    }

    @Override
    public void setChallengesLoading(boolean loading) {
        challenges.setLoading(loading);
    }

    @Override
    public void addChallenges(boolean hasMore, List<ChallengeDTO> challengeDTOs) {
        this.challenges.addData(challengeDTOs, hasMore);
    }

    @Override
    public TemplateView getTemplateView() {
        return template;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}