package com.geocento.webapps.eobroker.admin.client.views;

import com.geocento.webapps.eobroker.admin.client.Admin;
import com.geocento.webapps.eobroker.admin.client.ClientFactory;
import com.geocento.webapps.eobroker.admin.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.admin.client.activities.TemplateActivity;
import com.geocento.webapps.eobroker.admin.client.events.LogOut;
import com.geocento.webapps.eobroker.admin.client.places.*;
import com.geocento.webapps.eobroker.admin.shared.dtos.NotificationDTO;
import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.widgets.UserWidget;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
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

    public static interface Presenter {

    }

    @UiField
    Style style;

    @UiField
    MaterialSideNav sideNav;
    @UiField
    MaterialPanel mainPanel;
    @UiField
    HTMLPanel panel;
    @UiField
    MaterialNavBar navBar;
    @UiField
    MaterialLink companies;
    @UiField
    MaterialLink products;
    @UiField
    MaterialLink newsItems;
    @UiField
    MaterialLink feedback;
    @UiField
    MaterialLabel title;
    @UiField
    UserWidget userIcon;
    @UiField
    MaterialBadge notificationsBadge;
    @UiField
    MaterialDropDown notificationsPanel;
    @UiField
    HTMLPanel links;
    @UiField
    MaterialLink signOut;
    @UiField
    MaterialLink users;
    @UiField
    MaterialLink logs;
    @UiField
    MaterialLink settings;

    private final ClientFactoryImpl clientFactory;

    private Presenter presenter;

    public TemplateView(final ClientFactoryImpl clientFactory) {

        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        // update icons
        companies.setIconType(CategoryUtils.getIconType(Category.companies));
        products.setIconType(CategoryUtils.getIconType(Category.products));
        newsItems.setIconType(CategoryUtils.getIconType(Category.newsItems));

        setLink(users, new UsersPlace());
        setLink(companies, new CompaniesPlace());
        setLink(products, new ProductsPlace());
        setLink(newsItems, new NewsItemsPlace());
        setLink(feedback, new FeedbackPlace());
        setLink(settings, new SettingsPlace());
        setLink(logs, new LogsPlace());

        userIcon.setUser(Admin.getLoginInfo().getUserName());

        onResize(null);
    }

    private void setLink(MaterialLink link, EOBrokerPlace place) {
        link.setHref("#" + PlaceHistoryHelper.convertPlace(place));
    }

    public void setLink(String place) {
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
            case "users":
                users.addStyleName(style.selected());
                break;
            case "companies":
                companies.addStyleName(style.selected());
                break;
            case "products":
                products.addStyleName(style.selected());
                break;
            case "newsItems":
                newsItems.addStyleName(style.selected());
                break;
            case "feedback":
                feedback.addStyleName(style.selected());
                break;
            case "settings":
                settings.addStyleName(style.selected());
                break;
            case "logs":
                logs.addStyleName(style.selected());
                break;
        }
    }

    public void setLoading(String message) {
        navBar.showProgress(ProgressType.INDETERMINATE);
        if(message != null) {
            MaterialToast.fireToast(message);
        }
    }

    public void setLoadingError(String message) {
        navBar.hideProgress();
        if(message != null) {
            displayError(message);
        }
    }

    public void displayError(String message) {
        MaterialToast.fireToast(message, "deep-orange lighten-1");
    }

    public void hideLoading(String message) {
        navBar.hideProgress();
        if(message != null) {
            MaterialToast.fireToast(message, "green darken-1");
        }
    }

    public void displaySuccess(String message) {
        MaterialToast.fireToast(message, "green darken-1");
    }

    public void displayLoading() {
        setLoading(null);
    }

    public void hideLoading() {
        hideLoading(null);
    }

    public void setNotifications(List<NotificationDTO> notifications) {
        notificationsBadge.setText(notifications.size() + "");
        notificationsPanel.clear();
        boolean hasNotifications = notifications != null && notifications.size() > 0;
        notificationsBadge.setVisible(hasNotifications);
        if(hasNotifications) {
            for(NotificationDTO notificationDTO : notifications) {
                MaterialLink message = new MaterialLink(notificationDTO.getMessage());
                message.getElement().getStyle().setFontSize(0.8, com.google.gwt.dom.client.Style.Unit.EM);
                message.add(new HTML("<span style='text-align: right; font-size: 0.8em; color: black;'>" + DateUtils.getDuration(notificationDTO.getCreationDate()) + "</span>"));
                EOBrokerPlace place = null;
                switch(notificationDTO.getType()) {
                    case MESSAGE:
                        place = new FeedbackPlace(FeedbackPlace.TOKENS.feedbackid.toString() + "=" + notificationDTO.getLinkId());
                        break;
                    case USER:
                        place = new UsersPlace(UsersPlace.TOKENS.name.toString() + "=" + notificationDTO.getLinkId());
                        break;
                    case COMPANY:
                        place = new CompanyPlace(CompanyPlace.TOKENS.id.toString() + "=" + notificationDTO.getLinkId());
                        break;
                }
                message.setHref("#" + PlaceHistoryHelper.convertPlace(place));
                notificationsPanel.add(message);
            }
        } else {
            notificationsPanel.add(new MaterialLabel("No new notification"));
        }
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public Presenter getPresenter() {
        return presenter;
    }

    public ClientFactory getClientFactory() {
        return clientFactory;
    }

    @UiHandler("signOut")
    void logOut(ClickEvent clickEvent) {
        clientFactory.getEventBus().fireEvent(new LogOut());
    }

    public void setTitleText(String titleText) {
        title.setText(titleText);
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

    @Override
    public void onResize(ResizeEvent event) {
        if(sideNav.isVisible()) {
            panel.addStyleName(style.navOpened());
        } else {
            panel.setStyleName(style.navOpened(), false);
        }
        mainPanel.getElement().getStyle().setProperty("minHeight", (Window.getClientHeight() - 100) + "px");
    }

}