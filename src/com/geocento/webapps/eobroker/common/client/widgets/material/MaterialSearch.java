/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 - 2016 GwtMaterialDesign
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.geocento.webapps.eobroker.common.client.widgets.material;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialLoadingLink;
import com.geocento.webapps.eobroker.common.client.widgets.MaterialMessage;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.TextBox;
import gwt.material.design.client.base.HasActive;
import gwt.material.design.client.base.SearchObject;
import gwt.material.design.client.constants.Color;
import gwt.material.design.client.constants.IconSize;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.constants.InputType;
import gwt.material.design.client.ui.MaterialIcon;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialSearchResult;
import gwt.material.design.client.ui.MaterialValueBox;
import gwt.material.design.client.ui.html.Label;

import java.util.List;

public class MaterialSearch extends MaterialValueBox<String> implements HasCloseHandlers<String>, HasActive {

    public interface Presenter {
        void textChanged(String text);

        void suggestionSelected(SearchObject suggestion);

        void textSelected(String text);
    }

    private Label label = new Label();
    private MaterialIcon iconSearch = new MaterialIcon(IconType.SEARCH);
    private MaterialIcon iconClose = new MaterialIcon(IconType.CLOSE);
    private MaterialLoadingLink iconLoading = new MaterialLoadingLink();

    /**
     * Panel to display the result items
     */
    private MaterialSearchResult searchResult;
    /**
     * Link selected to determine easily during the selection event (up / down key events)
     */
    private MaterialLink selectedLink;
    /**
     * Gets the selected object after Search Finish event
     */
    private SearchObject selectedObject;
    /**
     * -1 means that the selected index is not yet selected.
     * It will increment or decrement once trigger by key up / down events
     */
    private int curSel = -1;
    private boolean active;

    private Presenter presenter;

    public MaterialSearch() {

        super(new TextBox());

        setType(InputType.SEARCH);

        iconSearch.setLayoutPosition(Style.Position.ABSOLUTE);
        iconSearch.setLeft(10);
        insert(iconSearch, 0);

        iconLoading.setLayoutPosition(Style.Position.ABSOLUTE);
        iconLoading.setLeft(10);
        insert(iconLoading, 0);

        label.getElement().setAttribute("for", "search");
        add(label);

        add(iconClose);
        iconClose.addMouseDownHandler(mouseDownEvent -> CloseEvent.fire(MaterialSearch.this, getText()));

        // populate the lists of search result on search panel
        searchResult = new MaterialSearchResult();
        add(searchResult);
        addKeyUpHandler(new KeyUpHandler() {

            private Timer fetchTimer;
            private String currentText = "";

            @Override
            public void onKeyUp(KeyUpEvent event) {
                // Apply selected search
                switch (event.getNativeEvent().getKeyCode()) {
                    case KeyCodes.KEY_ENTER: {
                        if (getCurSel() == -1) {
                            hideListSearches();
                            presenter.textSelected(getText());
                        } else {
                            MaterialLink selLink = getSelectedLink();
                            reset(selLink.getText());
                            selLink.fireEvent(new GwtEvent<ClickHandler>() {
                                @Override
                                public com.google.gwt.event.shared.GwtEvent.Type<ClickHandler> getAssociatedType() {
                                    return ClickEvent.getType();
                                }

                                @Override
                                protected void dispatch(ClickHandler handler) {
                                    handler.onClick(null);
                                }
                            });
                        }
                        event.stopPropagation();
                    }
                    break;
                    case KeyCodes.KEY_DOWN: {
                        int totalItems = searchResult.getWidgetCount();
                        if (curSel >= totalItems) {
                            setCurSel(getCurSel());
                            applyHighlightedItem((MaterialLink) searchResult.getWidget(curSel - 1));
                        } else {
                            setCurSel(getCurSel() + 1);
                            applyHighlightedItem((MaterialLink) searchResult.getWidget(curSel));
                        }
                        event.stopPropagation();
                    }
                    break;
                    case KeyCodes.KEY_UP: {
                        if (curSel <= -1) {
                            setCurSel(-1);
                            applyHighlightedItem((MaterialLink) searchResult.getWidget(curSel));
                        } else {
                            setCurSel(getCurSel() - 1);
                            applyHighlightedItem((MaterialLink) searchResult.getWidget(curSel));
                        }
                        event.stopPropagation();
                    }
                    break;
                    default:
                        if (fetchTimer != null) {
                            fetchTimer.cancel();
                            fetchTimer = null;
                        }
                        // create a timer to make sure we don't query too soon
                        fetchTimer = new Timer() {

                            @Override
                            public void run() {
                                // make sure we don't refresh options if the text hasn't changed
                                String text = getText();
                                if (text.contentEquals(currentText)) {
                                    return;
                                }
                                currentText = text;
                                presenter.textChanged(currentText);
                                fetchTimer = null;
                            }
                        };
                        // start the timer to make sure we waited long enough
                        fetchTimer.schedule(300);
                        break;
                }
            }
        });
        addCloseHandler(new CloseHandler<String>() {
            @Override
            public void onClose(CloseEvent<String> event) {
                reset("");
                presenter.textSelected("");
                setFocus(true);
            }
        });
        addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                new Timer() {

                    @Override
                    public void run() {
                        hideListSearches();
                    }
                }.schedule(300);
            }
        });
        hideListSearches();
        setLoading(false);
    }

    public MaterialSearch(String placeholder) {
        this();
        setPlaceholder(placeholder);
    }

    public MaterialSearch(String placeholder, Color backgroundColor, Color iconColor, boolean active, int shadow) {
        this(placeholder);
        setBackgroundColor(backgroundColor);
        setIconColor(iconColor);
        setActive(active);
        setShadow(shadow);
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void applyHighlightedItem(MaterialLink link) {
        for(int index = 0; index < searchResult.getWidgetCount(); index++) {
            searchResult.getWidget(index).setStyleName("higlighted", false);
        }
        link.addStyleName("higlighted");
        setSelectedLink(link);
    }

    public void displayListSearches(List<SearchObject> listSearches) {
        // Clear the panel and temp objects
        searchResult.clear();
        if(listSearches == null || listSearches.size() == 0) {
            MaterialLink link = new MaterialLink();
            link.setIconColor(Color.GREY);
            link.setTextColor(Color.BLACK);
            link.setText("No Result...");
            link.setTruncate(true);
            searchResult.add(link);
        } else {
            // Populate the search result items
            for (final SearchObject suggestion : listSearches) {
                MaterialLink link = new MaterialLink();
                link.setIconColor(Color.GREY);
                link.setTextColor(Color.BLACK);
                link.setTruncate(true);
                if (suggestion.getIcon() != null) {
                    link.setIconType(suggestion.getIcon());
                }
                link.setText(suggestion.getKeyword());
                link.addClickHandler(new ClickHandler() {
                    @Override
                    public void onClick(ClickEvent event) {
                        selectedObject = suggestion;
                        if (presenter != null) {
                            presenter.suggestionSelected(suggestion);
                        }
                    }
                });
                searchResult.add(link);
            }
        }
        searchResult.setVisible(true);
    }

    // Resets the search result panel
    private void reset(String keyword) {
        curSel = -1;
        setText(keyword);
        searchResult.clear();
        searchResult.setVisible(false);
    }

    public int getCurSel() {
        return curSel;
    }

    public void setCurSel(int curSel) {
        this.curSel = curSel;
    }

    public void hideListSearches() {
        searchResult.setVisible(false);
    }

/*
    @Override
    protected void onUnload() {
        super.onUnload();

        clear();
        setCurSel(-1);
    }

*/
    @Override
    public HandlerRegistration addCloseHandler(final CloseHandler<String> handler) {
        return addHandler((CloseHandler<String>) handler::onClose, CloseEvent.getType());
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
        if (active) {
            setTextColor(Color.BLACK);
            iconClose.setIconColor(Color.BLACK);
            iconSearch.setIconColor(Color.BLACK);
        } else {
            iconClose.setIconColor(Color.WHITE);
            iconSearch.setIconColor(Color.WHITE);
        }
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public MaterialLink getSelectedLink() {
        return selectedLink;
    }

    public void setSelectedLink(MaterialLink selectedLink) {
        this.selectedLink = selectedLink;
    }

    public SearchObject getSelectedObject() {
        return selectedObject;
    }

    public void setSelectedObject(SearchObject selectedObject) {
        this.selectedObject = selectedObject;
    }

    public void displaySearchLoading(String message) {
        searchResult.setVisible(true);
        searchResult.clear();
        MaterialLoadingLink materialLoadingLink = new MaterialLoadingLink();
        materialLoadingLink.setText(message);
        materialLoadingLink.setTextColor(Color.BLACK);
        materialLoadingLink.setIconSize(IconSize.SMALL);
        // TODO - solve problem with display of icon size
        //materialLoadingLink.setLoading(true);
        searchResult.add(materialLoadingLink);
    }

    public void hideListSuggestionsLoading() {
        searchResult.clear();
        searchResult.setVisible(false);
    }

    public void displaySearchError(String message) {
        MaterialMessage materialMessage = new MaterialMessage();
        materialMessage.displayErrorMessage(message);
        searchResult.setVisible(true);
        searchResult.clear();
        searchResult.add(materialMessage);
    }

    public void setLoading(boolean loading) {
        searchResult.setVisible(false);
        label.setEnabled(!loading);
        iconSearch.setVisible(!loading);
        iconLoading.setVisible(loading);
        iconLoading.setLoading(loading);
    }

}