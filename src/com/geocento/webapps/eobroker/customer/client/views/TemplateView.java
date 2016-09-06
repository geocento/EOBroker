package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.events.LogOut;
import com.geocento.webapps.eobroker.customer.client.places.LoginPagePlace;
import com.geocento.webapps.eobroker.customer.client.places.OrdersPlace;
import com.geocento.webapps.eobroker.customer.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.customer.shared.RequestDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.ProgressType;
import gwt.material.design.client.ui.*;

import java.util.Iterator;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class TemplateView extends Composite implements HasWidgets {

    interface TemplateViewUiBinder extends UiBinder<Widget, TemplateView> {
    }

    private static TemplateViewUiBinder ourUiBinder = GWT.create(TemplateViewUiBinder.class);

    public static interface Style extends CssResource {

        String navOpened();
    }

    @UiField
    Style style;

    @UiField
    MaterialContainer mainPanel;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialNavBar navBar;
    @UiField
    HTMLPanel header;
    @UiField
    MaterialLink signOut;
    @UiField
    MaterialLink signIn;
    @UiField
    MaterialImage logo;
    @UiField
    MaterialLink orders;
    @UiField
    MaterialBadge ordersBadge;
    @UiField
    MaterialLink notifications;
    @UiField
    HTMLPanel panel;
    @UiField
    HTMLPanel navbarElements;

    private final ClientFactoryImpl clientFactory;

    private LoginInfo loginInfo;

    public TemplateView(final ClientFactoryImpl clientFactory) {
        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        logo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(clientFactory.getDefaultPlace());
            }
        });

        orders.setHref("#" + PlaceHistoryHelper.convertPlace(new OrdersPlace()));
    }

    public void setTitleText(String title) {
        this.title.setText(title);
    }

    public void setUser(LoginInfo loginInfo) {
        boolean signedIn = loginInfo != null;
        signIn.setVisible(!signedIn);
        signOut.setVisible(signedIn);
        this.loginInfo = loginInfo;
    }

    @UiHandler("signIn")
    void signIn(ClickEvent clickEvent) {
        clientFactory.getEventBus().fireEvent(new PlaceChangeEvent(new LoginPagePlace(clientFactory.getPlaceController().getWhere())));
    }

    @UiHandler("signOut")
    void signOut(ClickEvent clickEvent) {
        clientFactory.getEventBus().fireEvent(new LogOut());
    }

    public void setLoading(String message) {
        displayLoading();
        MaterialToast.fireToast(message);
    }

    @UiChild(tagname = "header")
    public void addHeader(Widget widget) {
        header.add(widget);
    }

    @UiChild(tagname = "navBar")
    public void addNavBarElement(Widget widget) {
        navbarElements.add(widget);
    }

    public void displayLoading() {
        navBar.showProgress(ProgressType.INDETERMINATE);
    }

    public void hideLoading() {
        navBar.hideProgress();
    }

    public void displayError(String message) {
        MaterialToast.fireToast(message, "red darken-1");
    }

    public void displaySuccess(String message) {
        MaterialToast.fireToast(message, "green darken-1");
    }

    public void setRequests(List<RequestDTO> orders) {
        int count = orders == null ? 0 : orders.size();
        ordersBadge.setText(count + "");
    }

    public void displaySignedIn(boolean signedIn) {
        orders.setVisible(signedIn);
        notifications.setVisible(signedIn);
        signIn.setVisible(!signedIn);
        signOut.setVisible(signedIn);
    }

    public void setPanelStyleName(String styleName, boolean added) {
        panel.setStyleName(styleName, added);
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

}