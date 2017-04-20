package com.geocento.webapps.eobroker.customer.client.views;

import com.geocento.webapps.eobroker.common.client.widgets.material.MaterialRichEditor;
import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.customer.client.ClientFactoryImpl;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.client.widgets.MaterialSearch;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
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

    interface DummyUiBinder extends UiBinder<Widget, TestimonialViewImpl> {
    }

    private static DummyUiBinder ourUiBinder = GWT.create(DummyUiBinder.class);

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
            public void suggestionSelected(Suggestion suggestion) {
                presenter.selectSuggestion(suggestion);
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
            REST.withCallback(new MethodCallback<List<Suggestion>>() {

                @Override
                public void onFailure(Method method, Throwable exception) {

                }

                @Override
                public void onSuccess(Method method, List<Suggestion> response) {
                    // show only if last one to be called
                    if (lastCall == TestimonialViewImpl.this.lastCall) {
                        searchBox.setFocus(true);
                        searchBox.displayListSearches(response);
                    }
                }
            }).call(ServicesUtil.searchService).complete(text, category.getValue(), null);
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