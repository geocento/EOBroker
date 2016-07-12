package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.events.LogOut;
import com.geocento.webapps.eobroker.supplier.client.places.DashboardPlace;
import com.geocento.webapps.eobroker.supplier.client.places.OrderPlace;
import com.geocento.webapps.eobroker.supplier.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierNotificationDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.IconType;
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
    @UiField
    MaterialContainer mainPanel;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialImage logo;
    @UiField
    MaterialNavBar navBar;
    @UiField
    MaterialLink logOut;
    @UiField
    MaterialLink notifications;
    @UiField
    MaterialBadge notificationsBadge;
    @UiField
    MaterialDropDown notificationsPanel;

    private final ClientFactoryImpl clientFactory;

    public TemplateView(final ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        logo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(new DashboardPlace());
            }
        });

/*
        final MaterialBubble bubble = new MaterialBubble();
        bubble.add(new MaterialLabel("Test"));
        bubble.setPosition(Position.BOTTOM);
        bubble.setLayoutPosition(Style.Position.FIXED);
        RootPanel.get().add(bubble);
        bubble.setVisible(false);

        notifications.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                bubble.setVisible(true);
                bubble.setTop(notifications.getAbsoluteTop() + notifications.getOffsetHeight());
                bubble.setLeft(notifications.getAbsoluteLeft() + bubble.getOffsetWidth() / 2 - notifications.getOffsetWidth() / 2);
            }
        });
*/
    }

    public void setTitleText(String title) {
        this.title.setText(title);
    }

    public void setLoading(String message) {
        navBar.showProgress(ProgressType.INDETERMINATE);
        MaterialToast.fireToast(message);
    }

    public void hideLoading() {
        navBar.hideProgress();
    }

    public void displayError(String message) {
        MaterialToast.fireToast(message, "green darken-1");
    }

    public void displaySuccess(String message) {
        MaterialToast.fireToast(message, "green darken-1");
    }

    @UiHandler("logOut")
    void logOut(ClickEvent clickEvent) {
        clientFactory.getEventBus().fireEvent(new LogOut());
    }

    public void displayNotifications(List<SupplierNotificationDTO> notifications) {
        notificationsBadge.setText(notifications.size() + "");
        notificationsPanel.clear();
        boolean hasNotifications = notifications != null && notifications.size() > 0;
        notificationsBadge.setVisible(hasNotifications);
        if(hasNotifications) {
            for (SupplierNotificationDTO supplierNotificationDTO : notifications) {
                MaterialLink message = new MaterialLink(supplierNotificationDTO.getMessage(), new MaterialIcon(IconType.SHOP));
                message.getElement().getStyle().setFontSize(0.8, Style.Unit.EM);
                message.setHref("#" + PlaceHistoryHelper.convertPlace(new OrderPlace(OrderPlace.TOKENS.id.toString() + "=" + supplierNotificationDTO.getLinkId())));
                notificationsPanel.add(message);
            }
        } else {
            notificationsPanel.add(new MaterialLabel("No new notification"));
        }
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