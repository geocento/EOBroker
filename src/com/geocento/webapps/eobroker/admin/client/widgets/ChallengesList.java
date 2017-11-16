package com.geocento.webapps.eobroker.admin.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.AsyncPagingWidgetList;
import com.geocento.webapps.eobroker.admin.shared.dtos.ChallengeDTO;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by thomas on 21/06/2016.
 */
public class ChallengesList extends AsyncPagingWidgetList<ChallengeDTO> {

    public ChallengesList() {
        super(24, 12, 4, 3);
    }

    @Override
    protected Widget getItemWidget(ChallengeDTO value) {
        return new ChallengeWidget(value);
    }
}
