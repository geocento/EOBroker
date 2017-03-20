package com.geocento.webapps.eobroker.supplier.client.widgets;

import com.geocento.webapps.eobroker.supplier.client.Supplier;
import com.geocento.webapps.eobroker.supplier.client.events.RemoveSuccessStory;
import com.geocento.webapps.eobroker.supplier.client.places.SuccessStoryPlace;
import com.geocento.webapps.eobroker.supplier.shared.dtos.SuccessStoryDTO;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.addins.client.bubble.MaterialBubble;
import gwt.material.design.client.ui.MaterialImage;
import gwt.material.design.client.ui.MaterialLabel;
import gwt.material.design.client.ui.MaterialLink;

/**
 * Created by thomas on 20/03/2017.
 */
public class SuccessStoryWidget extends Composite {

    interface SuccessStoryWidgetUiBinder extends UiBinder<Widget, SuccessStoryWidget> {
    }

    private static SuccessStoryWidgetUiBinder ourUiBinder = GWT.create(SuccessStoryWidgetUiBinder.class);

    @UiField
    MaterialBubble panel;
    @UiField
    MaterialImage image;
    @UiField
    MaterialLabel title;
    @UiField
    MaterialLabel description;
    @UiField
    MaterialLabel date;
    @UiField
    MaterialLink edit;
    @UiField
    MaterialLink remove;

    static DateTimeFormat fmt = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.YEAR_MONTH);

    public SuccessStoryWidget(final SuccessStoryDTO successStoryDTO) {

        initWidget(ourUiBinder.createAndBindUi(this));

        image.setUrl(successStoryDTO.getImageUrl());
        title.setText(successStoryDTO.getName());
        description.setText(successStoryDTO.getDescription());
        date.setText(fmt.format(successStoryDTO.getDate()));

        edit.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Supplier.clientFactory.getPlaceController().goTo(new SuccessStoryPlace(SuccessStoryPlace.TOKENS.id.toString() + "=" +
                        successStoryDTO.getId()));
            }
        });
        remove.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Supplier.clientFactory.getEventBus().fireEvent(new RemoveSuccessStory(successStoryDTO.getId()));
            }
        });
    }

}