package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.material.MaterialSearch;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.geocento.webapps.eobroker.supplier.client.services.ServicesUtil;
import com.google.gwt.http.client.RequestException;
import gwt.material.design.client.base.SearchObject;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 03/11/2016.
 */
public class CompanyTextBox extends MaterialSearch {

    public static interface Presenter {

        void selectCompany(CompanyDTO companyDTO);

    }

    private Presenter presenter;

    private long lastCall = 0;

    public CompanyTextBox() {

        setPlaceholder("Type in company name");

        setPresenter(new MaterialSearch.Presenter() {

            @Override
            public void textChanged(String text) {
                if (text == null || text.length() == 0) {
                    presenter.selectCompany(null);
                } else {
                    updateSuggestions();
                }
            }

            @Override
            public void suggestionSelected(SearchObject suggestion) {
                presenter.selectCompany((CompanyDTO) suggestion.getO());
            }

            @Override
            public void textSelected(String text) {

            }
        });

    }

    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    private void updateSuggestions() {
        this.lastCall++;
        final long lastCall = this.lastCall;

        String text = getText();
        if(text != null && text.length() > 0) {
            try {
                REST.withCallback(new MethodCallback<List<CompanyDTO>>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, List<CompanyDTO> response) {
                        // show only if last one to be called
                        if (lastCall == CompanyTextBox.this.lastCall) {
                            setFocus(true);
                            List<SearchObject> results = ListUtil.mutate(response, new ListUtil.Mutate<CompanyDTO, SearchObject>() {
                                @Override
                                public SearchObject mutate(CompanyDTO companyDTO) {
                                    return new SearchObject(companyDTO.getName(), "", companyDTO);
                                }
                            });
                            displayListSearches(results);
                        }
                    }
                }).call(ServicesUtil.assetsService).findCompanies(getText());
            } catch (RequestException e) {
            }
        }
    }

    public void setCompany(CompanyDTO companyDTO) {
        setSelectedObject(companyDTO != null ? new SearchObject(companyDTO.getName(), "", companyDTO) : null);
        setText(companyDTO != null && companyDTO.getName() != null ? companyDTO.getName() : "");
    }

    public CompanyDTO getCompany() {
        return getSelectedObject() != null ? (CompanyDTO) getSelectedObject().getO() : null;
    }

    public void clearCompany() {
        setCompany(null);
    }
}
