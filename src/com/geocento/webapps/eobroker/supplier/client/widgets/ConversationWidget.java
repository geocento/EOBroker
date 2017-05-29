package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.common.client.utils.DateUtils;
import com.geocento.webapps.eobroker.common.client.utils.Utils;
import com.geocento.webapps.eobroker.supplier.client.places.ConversationPlace;
import com.geocento.webapps.eobroker.supplier.client.places.PlaceHistoryHelper;
import com.geocento.webapps.eobroker.supplier.shared.dtos.ConversationDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import gwt.material.design.addins.client.bubble.MaterialBubble;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialPanel;

/**
 * Created by thomas on 29/05/2017.
 */
public class ConversationWidget extends MaterialPanel {

    interface ConversationWidgetUiBinder extends UiBinder<MaterialBubble, ConversationWidget> {
    }

    private static ConversationWidgetUiBinder ourUiBinder = GWT.create(ConversationWidgetUiBinder.class);

    @UiField
    MaterialLabel comment;
    @UiField
    MaterialLink title;

    public ConversationWidget(ConversationDTO conversationDTO) {

        add(ourUiBinder.createAndBindUi(this));

        title.setText("Conversation '" + conversationDTO.getTopic() + "'");
        comment.setText("with user " + conversationDTO.getUser().getName() + " started on " + DateUtils.formatDateOnly(conversationDTO.getCreationDate()));

        title.setHref("#" + PlaceHistoryHelper.convertPlace(
                new ConversationPlace(Utils.generateTokens(ConversationPlace.TOKENS.id.toString(), conversationDTO.getId()))));
    }

}