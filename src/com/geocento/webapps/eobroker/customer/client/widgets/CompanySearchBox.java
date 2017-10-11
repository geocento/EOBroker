package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.Suggestion;
import com.geocento.webapps.eobroker.common.shared.entities.Category;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import gwt.material.design.client.base.SearchObject;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 21/04/2017.
 */
public class CompanySearchBox extends CategorySearchBox {

    public CompanySearchBox() {
        setCategory(Category.companies);
    }

    @Override
    protected void fetchSuggestions(String text, MethodCallback<List<Suggestion>> methodCallback) {
        REST.withCallback(methodCallback).call(ServicesUtil.searchService).completeAllCompanies(text, null);
    }

    public void setCompany(CompanyDTO companyDTO) {
        setSelectedObject(companyDTO != null ? new SearchObject(companyDTO.getName(), "", companyDTO) : null);
        setText(companyDTO != null && companyDTO.getName() != null ? companyDTO.getName() : "");
    }
}
