package com.geocento.webapps.eobroker.customer.client.widgets;

import com.geocento.webapps.eobroker.common.client.widgets.MaterialLoadingLink;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import gwt.material.design.client.constants.IconType;
import gwt.material.design.client.ui.MaterialLink;
import gwt.material.design.client.ui.MaterialTooltip;

/**
 * Created by thomas on 09/06/2016.
 */
public class FollowWidget extends Composite {

    interface FollowWidgetUiBinder extends UiBinder<Widget, FollowWidget> {
    }

    private static FollowWidgetUiBinder ourUiBinder = GWT.create(FollowWidgetUiBinder.class);

    @UiField
    MaterialTooltip followTooltip;
    @UiField
    MaterialLoadingLink follow;
    @UiField
    MaterialLink followers;
    @UiField
    MaterialTooltip followersTooltip;

    private String name = "";

    public FollowWidget() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFollowers(int followers) {
        this.followers.setText(followers + "");
        followersTooltip.setText(followers + " people are following this " + name);
    }

    public void setFollowing(boolean following) {
        follow.setText("");
        followTooltip.setText(following ? "Your are following this " + name + ", click to unfollow" : "Click to follow this " + name + " and be notified of changes");
        follow.setIconType(following ? IconType.STAR: IconType.STAR_BORDER);
    }

}