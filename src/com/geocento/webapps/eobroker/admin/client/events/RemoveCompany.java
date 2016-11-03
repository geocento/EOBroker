package com.geocento.webapps.eobroker.admin.client.events;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 27/10/2016.
 */
public class RemoveCompany extends GwtEvent<RemoveCompanyHandler> {

    public static Type<RemoveCompanyHandler> TYPE = new Type<RemoveCompanyHandler>();

    private CompanyDTO companyDTO;

    public RemoveCompany(CompanyDTO companyDTO) {
        this.companyDTO = companyDTO;
    }

    public CompanyDTO getCompanyDTO() {
        return companyDTO;
    }

    public Type<RemoveCompanyHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveCompanyHandler handler) {
        handler.onRemoveCompany(this);
    }
}
