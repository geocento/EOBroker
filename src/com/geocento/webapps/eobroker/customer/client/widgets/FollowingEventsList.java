package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingWidgetList;
import com.geocento.webapps.eobroker.customer.client.Customer;
import com.geocento.webapps.eobroker.customer.client.places.EOBrokerPlace;
import com.geocento.webapps.eobroker.customer.client.places.FullViewPlace;
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
        followingEventWidget.getAction().addClickHandler(event -> {
            EOBrokerPlace place = null;
            switch (followingEventDTO.getCategory()) {
                case companies:
                    switch (followingEventDTO.getType()) {
                        case TESTIMONIAL:
                            // TODO - replace by a full page for testimonies
                            place = new FullViewPlace(
                                    Utils.generateTokens(
                                            FullViewPlace.TOKENS.companyid.toString(), followingEventDTO.getCompanyDTO().getId() + "",
                                            FullViewPlace.TOKENS.tab.toString(), "credentials"));
                            break;
                        case OFFER:
                            place = new FullViewPlace(
                                    Utils.generateTokens(
                                            FullViewPlace.TOKENS.companyid.toString(), followingEventDTO.getLinkId(),
                                            FullViewPlace.TOKENS.tab.toString(), "offering"));
                            break;
                    }
                    break;
                case products:
                    switch (followingEventDTO.getType()) {
                        case TESTIMONIAL:
                            // TODO - replace by a full page for testimonies
                            break;
                        case OFFER:
                            place = new FullViewPlace(
                                    Utils.generateTokens(
                                            FullViewPlace.TOKENS.productid.toString(), followingEventDTO.getLinkId(),
                                            FullViewPlace.TOKENS.tab.toString(), "offering"));
                            break;
                    }
                    break;
                case productservices:
                    switch (followingEventDTO.getType()) {
                        case TESTIMONIAL:
                            // TODO - replace by a full page for testimonies
                            break;
                        case OFFER:
                            place = new FullViewPlace(
                                    Utils.generateTokens(
                                            FullViewPlace.TOKENS.productserviceid.toString(), followingEventDTO.getLinkId(),
                                            FullViewPlace.TOKENS.tab.toString(), "description"));
                            break;
                    }
                    break;
            }
            if (place != null) {
                Customer.clientFactory.getPlaceController().goTo(place);
            }
        });
        followingEventWidget.getElement().getStyle().setMarginBottom(10, Style.Unit.PX);
        return followingEventWidget;
    }

}
