package com.geocento.webapps.eobroker.customer.client.places;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 31/03/2017.
 */
public class FollowCompany extends GwtEvent<FollowCompanyHandler> {

    public static Type<FollowCompanyHandler> TYPE = new Type<FollowCompanyHandler>();

    boolean follow;
    CompanyDTO companyDTO;

    public FollowCompany(boolean follow, CompanyDTO companyDTO) {
        this.follow = follow;
        this.companyDTO = companyDTO;
    }

    public boolean isFollow() {
        return follow;
    }

    public CompanyDTO getCompanyDTO() {
        return companyDTO;
    }

    public Type<FollowCompanyHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(FollowCompanyHandler handler) {
        handler.onFollowCompany(this);
    }
}
