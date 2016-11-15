package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.events.LogOut;
import com.geocento.webapps.eobroker.admin.client.places.*;
import com.geocento.webapps.eobroker.common.client.widgets.UserWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.ProgressType;
import gwt.material.design.client.ui.*;

import java.util.Iterator;

/**
 * Created by thomas on 09/05/2016.
 */
public class TemplateView extends Composite implements HasWidgets, ResizeHandler {

    interface TemplateViewUiBinder extends UiBinder<Widget, TemplateView> {
    }

    private static TemplateViewUiBinder ourUiBinder = GWT.create(TemplateViewUiBinder.class);

    public static interface Style extends CssResource {

        String navOpened();
    }

    @UiField
    Style style;

    @UiField
    MaterialLink companies;
    @UiField
    MaterialSideNav sideNav;
    @UiField
    MaterialPanel mainPanel;
    @UiField
    HTMLPanel panel;
    @UiField
    MaterialLink products;
    @UiField
    MaterialNavBar navBar;
    @UiField
    MaterialLink newsItems;
    @UiField
    MaterialLabel title;
    @UiField
    UserWidget userIcon;

    private final ClientFactoryImpl clientFactory;

    public TemplateView(final ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        setLink(companies, new CompaniesPlace());
        setLink(products, new ProductsPlace());
        setLink(newsItems, new NewsItemsPlace());

        userIcon.setUser(Admin.getLoginInfo().getUserName());

        onResize(null);
    }

    private void setLink(MaterialLink link, EOBrokerPlace place) {
        link.setHref("#" + PlaceHistoryHelper.convertPlace(place));
    }

    public void setLoading(String message) {
        navBar.showProgress(ProgressType.INDETERMINATE);
        MaterialToast.fireToast(message);
    }

    public void setLoadingError(String message) {
        navBar.hideProgress();
        MaterialToast.fireToast(message, "deep-orange lighten-1");
    }

    public void hideLoading(String message) {
        navBar.hideProgress();
        MaterialToast.fireToast(message, "green darken-1");
    }

    @UiHandler("userIcon")
    void logOut(ClickEvent clickEvent) {
        clientFactory.getEventBus().fireEvent(new LogOut());
    }

    public void setTitleText(String titleText) {
        title.setText(titleText);
    }

    @Override
    public Widget asWidget() {
        return this;
    }

    @Override
    public void add(Widget w) {
        mainPanel.add(w);
    }

    @Override
    public void clear() {
        mainPanel.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return mainPanel.iterator();
    }

    @Override
    public boolean remove(Widget w) {
        return mainPanel.remove(w);
    }

    @Override
    public void onResize(ResizeEvent event) {
        if(sideNav.isVisible()) {
            panel.addStyleName(style.navOpened());
        } else {
            panel.setStyleName(style.navOpened(), false);
        }
    }

}