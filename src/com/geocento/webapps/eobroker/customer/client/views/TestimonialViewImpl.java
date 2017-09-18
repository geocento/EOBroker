package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.utils.CategoryUtils;
import com.geocento.webapps.eobroker.common.client.widgets.material.MaterialRichEditor;
import com.geocento.webapps.eobroker.common.client.widgets.material.MaterialSearch;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import gwt.material.design.client.base.SearchObject;
import gwt.material.design.client.ui.MaterialButton;
import gwt.material.design.client.ui.MaterialListValueBox;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 09/05/2016.
 */
public class TestimonialViewImpl extends Composite implements TestimonialView {

    private Presenter presenter;

    interface TestimonialViewUiBinder extends UiBinder<Widget, TestimonialViewImpl> {
    }

    private static TestimonialViewUiBinder ourUiBinder = GWT.create(TestimonialViewUiBinder.class);

    @UiField
    MaterialListValueBox<Category> category;
    @UiField
    MaterialSearch searchBox;
    @UiField
    MaterialRichEditor content;
    @UiField
    MaterialButton create;

    private int lastCall = 0;

    public TestimonialViewImpl(ClientFactoryImpl clientFactory) {

        initWidget(ourUiBinder.createAndBindUi(this));

        for(Category category : new Category[] {Category.companies, Category.products, Category.productdatasets}) {
            this.category.addItem(category, category.getName());
        }

        this.category.addValueChangeHandler(event -> categoryChanged());

        searchBox.setPresenter(new MaterialSearch.Presenter() {
            @Override
            public void textChanged(String text) {
                updateSuggestions();
            }

            @Override
            public void suggestionSelected(SearchObject searchObject) {
                presenter.selectSuggestion(searchObject == null ? null : (Suggestion) searchObject.getO());
            }

            @Override
            public void textSelected(String text) {

            }
        });
    }

    private void categoryChanged() {
        searchBox.clear();
    }

    private void updateSuggestions() {
        this.lastCall++;
        final long lastCall = this.lastCall;

        String text = searchBox.getText();
        if(text != null && text.length() > 0) {
            try {
                REST.withCallback(new MethodCallback<List<Suggestion>>() {

                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        searchBox.displaySearchError(method.getResponse().getText());
                    }

                    @Override
                    public void onSuccess(Method method, List<Suggestion> suggestions) {
                        // show only if last one to be called
                        if (lastCall == TestimonialViewImpl.this.lastCall) {
                            searchBox.setFocus(true);
                            searchBox.displayListSearches(ListUtil.mutate(suggestions, new ListUtil.Mutate<Suggestion, SearchObject>() {
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
                }).call(ServicesUtil.searchService).complete(text, category.getValue(), null);
            } catch (RequestException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public HasClickHandlers getCreateButton() {
        return create;
    }

    @Override
    public HasHTML getTestimonial() {
        return content;
    }

    @Override
    public HasValue<Category> getCategory() {
        return category;
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public HasText getSearchText() {
        return searchBox;
    }

    @Override
    public Widget asWidget() {
        return this;
    }

}