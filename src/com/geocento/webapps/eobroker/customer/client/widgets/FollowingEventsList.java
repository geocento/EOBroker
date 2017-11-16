package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingWidgetList;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.EOBrokerPlace;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
import com.geocento.webapps.eobroker.customer.client.places.TestimonialPlace;
import com.geocento.webapps.eobroker.customer.shared.FollowingEventDTO;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by thomas on 04/07/2017.
 */
public class FollowingEventsList extends AsyncPagingWidgetList<FollowingEventDTO> {

    public FollowingEventsList() {
        this(10, 12, 6, 6);
    }

    public FollowingEventsList(int pageSize, int small, int medium, int large) {
        super(pageSize, small, medium, large);
    }

    @Override
    protected Widget getItemWidget(FollowingEventDTO followingEventDTO) {
        FollowingEventWidget followingEventWidget = new FollowingEventWidget(followingEventDTO);
        EOBrokerPlace place = null;
        String linkId = followingEventDTO.getLinkId();
        if(linkId != null) {
            switch (followingEventDTO.getType()) {
                case TESTIMONIAL:
                    place = new TestimonialPlace(
                            Utils.generateTokens(
                                    TestimonialPlace.TOKENS.id.toString(), linkId));
                    break;
                case OFFER: {
                    FullViewPlace.TOKENS firstToken = null;
                    switch (followingEventDTO.getCategory()) {
                        case companies:
                            firstToken = FullViewPlace.TOKENS.companyid;
                            break;
                        case products:
                            firstToken = FullViewPlace.TOKENS.productid;
                            place = new FullViewPlace(
                                    Utils.generateTokens(
                                            FullViewPlace.TOKENS.productid.toString(), linkId,
                                            FullViewPlace.TOKENS.tab.toString(), "offerings"));
                            break;
                        case productservices:
                            firstToken = FullViewPlace.TOKENS.productserviceid;
                            break;
                        case productdatasets:
                            firstToken = FullViewPlace.TOKENS.productdatasetid;
                            break;
                    }
                    if (firstToken != null) {
                        place = new FullViewPlace(
                                Utils.generateTokens(firstToken.toString(), linkId,
                                        FullViewPlace.TOKENS.tab.toString(), "offerings"));
                    }
                }
                break;
            }
        }
        boolean hasAction = place != null;
        followingEventWidget.displayAction(hasAction);
        if (hasAction) {
            final EOBrokerPlace finalPlace = place;
            followingEventWidget.getAction().addClickHandler(event -> {
                Customer.clientFactory.getPlaceController().goTo(finalPlace);
            });
        }
        followingEventWidget.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        return followingEventWidget;
    }

}
