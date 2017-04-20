package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.customer.client.services.ServicesUtil;
import com.geocento.webapps.eobroker.customer.shared.ProductDTO;
import com.google.gwt.user.client.Window;
import org.fusesource.restygwt.client.Method;
import org.fusesource.restygwt.client.MethodCallback;
import org.fusesource.restygwt.client.REST;

/**
 * Created by thomas on 20/04/2017.
 */
public class ProductFollowWidget extends FollowWidget {

    public ProductFollowWidget(ProductDTO productDTO) {
        setName("product");
        setFollowers(productDTO.getFollowers());
        setFollowing(productDTO.isFollowing());
        follow.addClickHandler(event -> {
            try {
                follow.setLoading(true);
                boolean following = !productDTO.getFollowing();
                REST.withCallback(new MethodCallback<Long>() {
                    @Override
                    public void onFailure(Method method, Throwable exception) {
                        follow.setLoading(false);
                        Window.alert("Could not follow product please try again");
                    }

                    @Override
                    public void onSuccess(Method method, Long followers) {
                        follow.setLoading(false);
                        productDTO.setFollowing(following);
                        setFollowing(productDTO.getFollowing());
                        productDTO.setFollowers(followers.intValue());
                        setFollowers(productDTO.getFollowers());
                    }
                }).call(ServicesUtil.assetsService).followProduct(productDTO.getId(), following);
            } catch (Exception e) {
                follow.setLoading(false);
            }
        });
    }


}
