package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.material.MaterialSearch;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.google.gwt.http.client.RequestException;
import gwt.material.design.client.base.SearchObject;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 21/04/2017.
 */
public class CategorySearchBox extends MaterialSearch {

    public static interface Presenter {

        void selectSuggestion(Suggestion suggestion);

    }

    private Presenter presenter;

    private Category category;

    private long lastCall = 0;

    public CategorySearchBox() {

        setPresenter(new MaterialSearch.Presenter() {
            @Override
            public void textChanged(String text) {
                if(text == null || text.length() == 0) {
                    presenter.selectSuggestion(null);
                } else {
                    updateSuggestions();
                }
            }

            @Override
            public void suggestionSelected(SearchObject searchObject) {
                presenter.selectSuggestion(searchObject == null ? null : (Suggestion) searchObject.getO());
            }

            @Override
            public void textSelected(String text) {
                presenter.selectSuggestion(null);
            }
        });
    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    private void updateSuggestions() {
        this.lastCall++;
        final long lastCall = this.lastCall;

        String text = getText();
        if(text != null && text.length() > 0) {
            fetchSuggestions(text, new MethodCallback<List<Suggestion>>() {

                @Override
                public void onFailure(Method method, Throwable exception) {
                    displaySearchError(method.getResponse().getText());
                }

                @Override
                public void onSuccess(Method method, List<Suggestion> suggestions) {
                    // show only if last one to be called
                    if (lastCall == CategorySearchBox.this.lastCall) {
                        setFocus(true);
                        displayListSearches(ListUtil.mutate(suggestions, new ListUtil.Mutate<Suggestion, SearchObject>() {
                            @Override
                            public SearchObject mutate(Suggestion suggestion) {
                                SearchObject searchObject = new SearchObject();
                                searchObject.setIcon(CategoryUtils.getIconType(suggestion.getCategory()));
                                searchObject.setKeyword(suggestion.getName());
                                searchObject.setO(suggestion);
                                return searchObject;
                            }
                        }));
                    }
                }
            });
        }
    }

    // default version
    protected void fetchSuggestions(String text, MethodCallback<List<Suggestion>> methodCallback) {
        try {
            REST.withCallback(new MethodCallback<List<Suggestion>>() {

                @Override
                public void onFailure(Method method, Throwable exception) {
                     displaySearchError(method.getResponse().getText());
                }

                @Override
                public void onSuccess(Method method, List<Suggestion> suggestions) {
                    // show only if last one to be called
                    if (lastCall == CategorySearchBox.this.lastCall) {
                        setFocus(true);
                        displayListSearches(ListUtil.mutate(suggestions, new ListUtil.Mutate<Suggestion, SearchObject>() {
                            @Override
                            public SearchObject mutate(Suggestion suggestion) {
                                SearchObject searchObject = new SearchObject();
                                searchObject.setIcon(CategoryUtils.getIconType(suggestion.getCategory()));
                                searchObject.setKeyword(suggestion.getName());
                                searchObject.setO(suggestion);
                                return searchObject;
                            }
                        }));
                    }
                }
            }).call(ServicesUtil.searchService).complete(text, category);
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

}
