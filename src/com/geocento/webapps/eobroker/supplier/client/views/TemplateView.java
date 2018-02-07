package com.geocento.webapps.eobroker.supplier.client.views;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.LoadingWidget;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialLabelIcon;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialSideNav;
import com.geocento.webapps.eobroker.common.client.widgets.UserWidget;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.supplier.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.LogOut;
import com.geocento.webapps.eobroker.supplier.client.places.*;
import com.geocento.webapps.eobroker.supplier.client.utils.NotificationHelper;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SupplierNotificationDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.ProgressType;
import gwt.material.design.client.ui.*;

import java.util.Iterator;
import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class TemplateView extends Composite implements HasWidgets, ResizeHandler {

    interface TemplateViewUiBinder extends UiBinder<Widget, TemplateView> {
    }

    private static TemplateViewUiBinder ourUiBinder = GWT.create(TemplateViewUiBinder.class);

    public static interface Style extends CssResource {

        String navOpened();

        String selected();
    }

    @UiField
    Style style;

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
    @UiField
    LoadingWidget loading;
    @UiField
    MaterialPanel mainPanel;
    @UiField
    MaterialSideNav sideNav;
    @UiField
    HTMLPanel panel;
    @UiField
    MaterialLink company;
    @UiField
    MaterialLink services;
    @UiField
    MaterialLink datasets;
    @UiField
    MaterialLink software;
    @UiField
    MaterialLink projects;
    @UiField
    HTMLPanel links;
    @UiField
    MaterialLabelIcon companyImage;
    @UiField
    MaterialLabel companyName;
    @UiField
    MaterialLink settings;
    @UiField
    MaterialLink successStories;
    @UiField
    MaterialLink testimonials;
    @UiField
    MaterialLink conversations;
    @UiField
    MaterialLink notificationsLink;
    @UiField
    MaterialLink statistics;

    private int maxNotifications = 5;

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

        company.setIconType(CategoryUtils.getIconType(Category.companies));
        services.setIconType(CategoryUtils.getIconType(Category.productservices));
        datasets.setIconType(CategoryUtils.getIconType(Category.productdatasets));
        software.setIconType(CategoryUtils.getIconType(Category.software));
        projects.setIconType(CategoryUtils.getIconType(Category.project));
        testimonials.setIconType(IconType.VERIFIED_USER);
        successStories.setIconType(IconType.SENTIMENT_VERY_SATISFIED);
        conversations.setIconType(IconType.CHAT_BUBBLE);
        notificationsLink.setIconType(IconType.NOTIFICATIONS);
        statistics.setIconType(IconType.PIE_CHART);
        settings.setIconType(IconType.SETTINGS);

        setLink(company, new CompanyPlace());
        setLink(services, new ProductServicesPlace());
        setLink(datasets, new ProductDatasetsPlace());
        setLink(software, new SoftwaresPlace());
        setLink(projects, new ProjectsPlace());
        setLink(testimonials, new TestimonialsPlace());
        setLink(successStories, new SuccessStoriesPlace());
        setLink(conversations, new ConversationsPlace());
        setLink(notificationsLink, new NotificationsPlace());
        setLink(statistics, new StatisticsPlace());
        setLink(settings, new SettingsPlace());

        onResize(null);
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

    public void setCompany(CompanyDTO companyDTO) {
        companyImage.setImageUrl(companyDTO.getIconURL());
        companyImage.setText(companyDTO.getName());
    }

    private void setLink(MaterialLink link, EOBrokerPlace place) {
        link.setHref("#" + PlaceHistoryHelper.convertPlace(place));
    }

    public void setPlace(String place) {
        for(Widget widget : links) {
            widget.removeStyleName(style.selected());
            if(widget instanceof MaterialLink) {
                ((MaterialLink) widget).setTextColor(null);
            }
        }
        if(place == null) {
            return;
        }
        switch (place) {
            case "companies":
                company.addStyleName(style.selected());
                break;
            case "productservices":
                services.addStyleName(style.selected());
                break;
            case "productdatasets":
                datasets.addStyleName(style.selected());
                break;
            case "software":
                software.addStyleName(style.selected());
                break;
            case "project":
                projects.addStyleName(style.selected());
                break;
            case "testimonials":
                testimonials.addStyleName(style.selected());
                break;
            case "stories":
                successStories.addStyleName(style.selected());
                break;
            case "statistics":
                statistics.addStyleName(style.selected());
                break;
            case "settings":
                settings.addStyleName(style.selected());
                break;
        }
    }

    @UiHandler("logOut")
    void logOut(ClickEvent clickEvent) {
        clientFactory.getEventBus().fireEvent(new LogOut());
    }

    public void setNotifications(List<SupplierNotificationDTO> notifications) {
        notificationsPanel.clear();
        boolean hasNotifications = notifications != null && notifications.size() > 0;
        notificationsBadge.setVisible(hasNotifications);
        if(hasNotifications) {
            boolean hasMore = notifications.size() > maxNotifications;
            if(hasMore) {
                notifications = notifications.subList(0, maxNotifications);
            }
            notificationsBadge.setText(notifications.size() + "");
            for (SupplierNotificationDTO supplierNotificationDTO : notifications) {
                addNotification(supplierNotificationDTO);
            }
            if(hasMore) {
                MaterialLink materialLink = new MaterialLink("See all notifications");
                materialLink.setTruncate(true);
                materialLink.setFontSize("0.8em");
                materialLink.setPadding(10);
                materialLink.getElement().getStyle().setTextAlign(com.google.gwt.dom.client.Style.TextAlign.CENTER);
                materialLink.getElement().getStyle().setTextDecoration(com.google.gwt.dom.client.Style.TextDecoration.UNDERLINE);
                materialLink.setHref("#" + PlaceHistoryHelper.convertPlace(new NotificationsPlace()));
                notificationsPanel.add(materialLink);
            }
        } else {
            notificationsBadge.setText("");
            notificationsPanel.add(new MaterialLabel("No new notification"));
        }
    }

    public void addNotification(SupplierNotificationDTO supplierNotificationDTO) {
        MaterialLink message = new MaterialLink(supplierNotificationDTO.getMessage());
        message.setTruncate(true);
        message.getElement().getStyle().setFontSize(0.8, com.google.gwt.dom.client.Style.Unit.EM);
        HTML timePanel = new HTML("<span style='text-align: right; font-size: 0.8em; color: black;'>" + DateUtils.getDuration(supplierNotificationDTO.getCreationDate()) + "</span>");
        timePanel.getElement().getStyle().setTextAlign(com.google.gwt.dom.client.Style.TextAlign.RIGHT);
        message.setHref("#" + PlaceHistoryHelper.convertPlace(NotificationHelper.convertToPlace(supplierNotificationDTO)));
        notificationsPanel.add(message);
    }

    public void scrollToTop() {
        Window.scrollTo(0, 0);
    }

    public void displayContent(boolean display) {
        mainPanel.setVisible(display);
        loading.setVisible(!display);
    }

    public void displayFullLoading(String message) {
        displayContent(false);
        loading.setText(message);
    }

    public void hideFullLoading() {
        displayContent(true);
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
        mainPanel.getElement().getStyle().setProperty("minHeight", (Window.getClientHeight() - 100) + "px");
    }

    public void displayWebsocketError(String message) {
        // TODO - change for something more specific
        displayError(message);
    }

}