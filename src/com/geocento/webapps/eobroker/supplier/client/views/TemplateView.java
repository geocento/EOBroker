package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.UserWidget;
import com.geocento.webapps.eobroker.common.shared.entities.notifications.SupplierNotification;
import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.LogOut;
import com.geocento.webapps.eobroker.supplier.client.places.*;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierNotificationDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
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
    @UiField
    MaterialLink orders;
    @UiField
    MaterialBadge ordersBadge;
    @UiField
    UserWidget userIcon;

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

        orders.setHref("#" + PlaceHistoryHelper.convertPlace(new OrdersPlace()));

        ordersBadge.setVisible(false);

        userIcon.setUser(Supplier.getLoginInfo().getUserName());
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
        MaterialToast.fireToast(message, "red darken-1");
    }

    public void displaySuccess(String message) {
        MaterialToast.fireToast(message, "green darken-1");
    }

    @UiHandler("logOut")
    void logOut(ClickEvent clickEvent) {
        clientFactory.getEventBus().fireEvent(new LogOut());
    }

    public void setNotifications(List<SupplierNotificationDTO> notifications) {
        notificationsBadge.setText(notifications.size() + "");
        notificationsPanel.clear();
        boolean hasNotifications = notifications != null && notifications.size() > 0;
        notificationsBadge.setVisible(hasNotifications);
        if(hasNotifications) {
            for (SupplierNotificationDTO supplierNotificationDTO : notifications) {
                MaterialLink message = new MaterialLink(supplierNotificationDTO.getMessage());
                message.setTruncate(true);
                message.getElement().getStyle().setFontSize(0.8, Style.Unit.EM);
                message.add(new HTML("<span style='text-align: right; font-size: 0.8em; color: black;'>" + DateUtils.getDuration(supplierNotificationDTO.getCreationDate()) + "</span>"));
                EOBrokerPlace place = null;
                switch(supplierNotificationDTO.getType()) {
                    case MESSAGE:
                        place = new ConversationPlace(ConversationPlace.TOKENS.id.toString() + "=" + supplierNotificationDTO.getLinkId());
                        break;
                    case PRODUCTREQUEST:
                    case IMAGESERVICEREQUEST:
                    case IMAGEREQUEST:
                        place = new OrderPlace(
                                supplierNotificationDTO.getLinkId(),
                                supplierNotificationDTO.getType() == SupplierNotification.TYPE.IMAGEREQUEST ? RequestDTO.TYPE.image :
                                        supplierNotificationDTO.getType() == SupplierNotification.TYPE.IMAGESERVICEREQUEST ? RequestDTO.TYPE.imageservice :
                                                supplierNotificationDTO.getType() == SupplierNotification.TYPE.PRODUCTREQUEST ? RequestDTO.TYPE.product : null
                        );
                        break;
                }
                message.setHref("#" + PlaceHistoryHelper.convertPlace(place));
                notificationsPanel.add(message);
            }
        } else {
            notificationsPanel.add(new MaterialLabel("No new notification"));
        }
    }

    public void scrollToTop() {
        Window.scrollTo(0, 0);
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