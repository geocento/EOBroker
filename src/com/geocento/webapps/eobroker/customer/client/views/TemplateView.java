package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.AoI;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.LoginInfo;
import com.geocento.webapps.eobroker.common.shared.entities.orders.RequestDTO;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.events.LogOut;
import com.geocento.webapps.eobroker.customer.client.places.*;
import com.geocento.webapps.eobroker.customer.client.widgets.MaterialSuggestion;
import com.geocento.webapps.eobroker.customer.shared.NotificationDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiChild;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
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

    public static interface Presenter {

        void categoryChanged(Category category);

        void aoiChanged(AoI aoi);

        void textChanged(String text);

        void suggestionSelected(Suggestion suggestion);

        void textSelected(String text);
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
    @UiField
    MaterialSuggestion textSearch;
    @UiField
    MaterialLink allCategories;
    @UiField
    MaterialDropDown categoriesDropdown;
    @UiField
    MaterialLink categories;
    @UiField
    MaterialBadge notificationsBadge;
    @UiField
    MaterialDropDown notificationsPanel;

    private final ClientFactoryImpl clientFactory;

    private LoginInfo loginInfo;

    private Presenter presenter;

    public TemplateView(final ClientFactoryImpl clientFactory) {
        this.clientFactory = clientFactory;

        initWidget(ourUiBinder.createAndBindUi(this));

        logo.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                clientFactory.getPlaceController().goTo(clientFactory.getDefaultPlace());
            }
        });

        orders.setHref("#" + PlaceHistoryHelper.convertPlace(new RequestsPlace()));

        textSearch.setPresenter(new MaterialSuggestion.Presenter() {
            @Override
            public void textChanged(String text) {
                presenter.textChanged(text);
            }

            @Override
            public void suggestionSelected(Suggestion suggestion) {
                presenter.suggestionSelected(suggestion);
            }

            @Override
            public void textSelected(String text) {
                presenter.textSelected(text);
            }
        });
        // add categories
        for(final Category category : Category.values()) {
            MaterialLink categoryLink = new MaterialLink(category.getName());
            categoryLink.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    displayCategory(category);
                    presenter.categoryChanged(category);
                }
            });
            categoriesDropdown.add(categoryLink);
        }
        allCategories.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                displayCategory(null);
                presenter.categoryChanged(null);
            }
        });
    }

    public void displayCategory(Category category) {
        if(category == null) {
            categories.setText("All categories");
        } else {
            categories.setText(category.getName());
        }
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
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

    public ClientFactoryImpl getClientFactory() {
        return clientFactory;
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
                        place = new ConversationPlace(ConversationPlace.TOKENS.conversationid.toString() + "=" + notificationDTO.getLinkId());
                        break;
                    case PRODUCTREQUEST:
                        place = new ProductResponsePlace(ProductResponsePlace.TOKENS.id.toString() + "=" + notificationDTO.getLinkId());
                        break;
                    case IMAGESERVICEREQUEST:
                        place = new ImageryResponsePlace(ImageryResponsePlace.TOKENS.id.toString() + "=" + notificationDTO.getLinkId());
                        break;
                    case IMAGEREQUEST:
                        place = new ImagesResponsePlace(ImageryResponsePlace.TOKENS.id.toString() + "=" + notificationDTO.getLinkId());
                        break;
                }
                message.setHref("#" + PlaceHistoryHelper.convertPlace(place));
                notificationsPanel.add(message);
            }
        } else {
            notificationsPanel.add(new MaterialLabel("No new notification"));
        }
    }

    public void displaySignedIn(boolean signedIn) {
        orders.setVisible(signedIn);
        notifications.setVisible(signedIn);
        signIn.setVisible(!signedIn);
        signOut.setVisible(signedIn);
    }

    public void displayListSuggestions(List<Suggestion> searchObjects) {
        textSearch.setFocus(true);
        textSearch.displayListSearches(searchObjects);
    }

    public void setSearchText(String text) {
        textSearch.setText(text);
    }

    public void displaySearchError(String message) {
        MaterialToast.fireToast(message);
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