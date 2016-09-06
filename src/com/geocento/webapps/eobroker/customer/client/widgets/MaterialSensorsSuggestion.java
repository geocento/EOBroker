package com.geocento.webapps.eobroker.customer.client.widgets;

/*
 * #%L
 * GwtMaterial
 * %%
 * Copyright (C) 2015 GwtMaterialDesign
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

import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.events.SuggestionSelected;
import com.geocento.webapps.eobroker.customer.client.styles.StyleResources;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.GwtEvent;
import gwt.material.design.client.base.MaterialWidget;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialTextBox;

import java.util.List;

//@formatter:off

/**
 * Material Search is a value box component that returs a result based on your search
 *
 * <p>
 * <h3>UiBinder Usage:</h3>
 * <pre>
 * {@code
 * <m:MaterialSearch placeholder="Sample"/>
 * }
 * </pre>
 *
 * <h3>Populating the search result objects</h3>
 * {@code
 *
 * List<SearchObject> objects = new ArrayList<>();
 *
 * private void onInitSearch(){
 *   objects.add(new SearchObject(IconType.POLYMER, "Pushpin", "#!pushpin"));
 *   objects.add(new SearchObject(IconType.POLYMER, "SideNavs", "#!sidenavs"));
 *   objects.add(new SearchObject(IconType.POLYMER, "Scrollspy", "#!scrollspy"));
 *   objects.add(new SearchObject(IconType.POLYMER, "Tabs", "#!tabs"));
 *   txtSearch.setListSearches(objects);
 * }
 *
 * }
 * </p>
 *
 * @author kevzlou7979
 * @author Ben Dol
 * @see <a href="http://gwt-material-demo.herokuapp.com/#navigations">Material Search</a>
 */
//@formatter:on
public class MaterialSensorsSuggestion extends MaterialTextBox {

    static public interface Presenter {
        void onTextChanged(String text);
    }

    /**
     * Panel to display the result items
     */
    class MaterialSearchResult extends MaterialWidget {

        public MaterialSearchResult() {
            super(Document.get().createDivElement(), StyleResources.INSTANCE.style().searchResult());
        }

    }
    private MaterialSearchResult searchResult;
    /**
     * Link selected to determine easily during the selection event (up / down key events)
     */
    private MaterialLink selectedLink;
    /**
     * Gets the selected object after Search Finish event
     */
    private Suggestion selectedObject;
    /**
     * -1 means that the selected index is not yet selected.
     * It will increment or decrement once triggere by key up / down events
     */
    private int curSel = -1;

    private Presenter presenter;

    public MaterialSensorsSuggestion() {
        // populate the lists of search result on search panel
        searchResult = new MaterialSearchResult();
        searchResult.setVisible(false);
        add(searchResult);
        addKeyUpHandler(new KeyUpHandler() {

            String currentText = getText();

            @Override
            public void onKeyUp(KeyUpEvent event) {

                // Apply selected search
                switch (event.getNativeEvent().getKeyCode()) {
                    case KeyCodes.KEY_ENTER: {
                        if (getCurSel() == -1) {
                            return;
                        }
                        MaterialLink selLink = getSelectedLink();
                        reset(selLink.getText());
                        selLink.fireEvent(new GwtEvent<ClickHandler>() {
                            @Override
                            public DomEvent.Type<ClickHandler> getAssociatedType() {
                                return ClickEvent.getType();
                            }

                            @Override
                            protected void dispatch(ClickHandler handler) {
                                handler.onClick(null);
                            }
                        });
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
                        if(presenter != null && !getText().contentEquals(currentText)) {
                            presenter.onTextChanged(getText());
                        }
                        break;
                }
                currentText = getText();
            }
        });
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void applyHighlightedItem(MaterialLink link){
        for(int index = 0; index < searchResult.getWidgetCount(); index++) {
            searchResult.getWidget(index).setStyleName(com.geocento.webapps.eobroker.customer.client.styles.StyleResources.INSTANCE.style().highlighted(), false);
        }
        link.addStyleName(com.geocento.webapps.eobroker.customer.client.styles.StyleResources.INSTANCE.style().highlighted());
        setSelectedLink(link);
    }

    public MaterialLink getSelectedLink() {
        return selectedLink;
    }

    public void setSelectedLink(MaterialLink selectedLink) {
        this.selectedLink = selectedLink;
    }

    public void displayListSearches(List<Suggestion> listSearches) {
        // Clear the panel and temp objects
        searchResult.clear();
        if(listSearches == null) {
            return;
        }
        // Populate the search result items
        for(final Suggestion suggestion : listSearches) {
            MaterialLink link = new MaterialLink();
            link.setTextColor("black");
            // Generate an image
            link.setText(suggestion.getCategory() == Category.imagery ? suggestion.getName() : "suitable for '" + suggestion.getName() + "'");
            link.getElement().getStyle().setProperty("lineHeight", "normal");
            link.getElement().getStyle().setProperty("height", "2em");
            link.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    searchResult.setVisible(false);
                    Customer.clientFactory.getEventBus().fireEvent(new SuggestionSelected(suggestion));
                }
            });
            searchResult.add(link);
        }
        searchResult.setVisible(true);
        setCurSel(-1);
    }

    // Resets the search result panel
    private void reset(String keyword){
        curSel = -1;
        setText(keyword);
        searchResult.clear();
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
    public HandlerRegistration addBlurHandler(final BlurHandler handler) {
        return super.addBlurHandler(new BlurHandler() {
            @Override
            public void onBlur(BlurEvent event) {
                EventTarget nextFocus = event.getNativeEvent().getRelatedEventTarget();
                if(!Element.is(nextFocus) || Element.as(nextFocus) != searchResult.getElement()) {
                    handler.onBlur(event);
                }
            }
        });
    }
*/
}


