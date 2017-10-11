package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.admin.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.common.client.widgets.material.MaterialSearch;
import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.common.shared.utils.ListUtil;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import gwt.material.design.client.base.SearchObject;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

import java.util.List;

/**
 * Created by thomas on 03/11/2016.
 */
public class CompanyTextBox extends MaterialSearch {

    public CompanyTextBox() {

        setPlaceholder("Type in company name");

        addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                REST.withCallback(new MethodCallback<List<CompanyDTO>>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {

                    }

                    @Override
                    public void onSuccess(Method method, List<CompanyDTO> response) {
                        displayListSearches(ListUtil.mutate(response, new ListUtil.Mutate<CompanyDTO, SearchObject>() {
                            @Override
                            public SearchObject mutate(CompanyDTO companyDTO) {
                                return new SearchObject(companyDTO.getName(), "", companyDTO);
                            }
                        }));
                    }
                }).call(ServicesUtil.assetsService).findCompanies(getValue());
            }
        });
    }

    public void setCompany(CompanyDTO companyDTO) {
        setSelectedObject(companyDTO != null ? new SearchObject(companyDTO.getName(), "", companyDTO) : null);
        setText(companyDTO != null && companyDTO.getName() != null ? companyDTO.getName() : "");
    }

    public CompanyDTO getCompany() {
        return getSelectedObject() != null ? (CompanyDTO) getSelectedObject().getO() : null;
    }

    public void clearCompany() {
        setText("");
    }
}
