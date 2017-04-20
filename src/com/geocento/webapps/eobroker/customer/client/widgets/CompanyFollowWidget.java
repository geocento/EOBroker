package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.shared.entities.dtos.CompanyDTO;
import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.google.gwt.user.client.Window;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

/**
 * Created by thomas on 20/04/2017.
 */
public class CompanyFollowWidget extends FollowWidget {

    public CompanyFollowWidget(CompanyDTO companyDTO) {
        setName("company");
        setFollowing(companyDTO.getFollowing());
        setFollowers(companyDTO.getFollowers());
        follow.addClickHandler(event -> {
            try {
                follow.setLoading(true);
                boolean following = !companyDTO.getFollowing();
                REST.withCallback(new MethodCallback<Long>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        follow.setLoading(false);
                        Window.alert("Could not follow company please try again");
                    }

                    @Override
                    public void onSuccess(Method method, Long result) {
                        follow.setLoading(false);
                        companyDTO.setFollowing(following);
                        setFollowing(companyDTO.getFollowing());
                        companyDTO.setFollowers(result.intValue());
                        setFollowers(companyDTO.getFollowers());
                    }
                }).call(ServicesUtil.assetsService).followCompany(companyDTO.getId(), !companyDTO.getFollowing());
            } catch (Exception e) {
                follow.setLoading(false);
            }
        });
    }


}
