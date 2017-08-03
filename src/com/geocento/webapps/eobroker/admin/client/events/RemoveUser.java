package com.geocento.webapps.eobroker.admin.client.events;

import com.geocento.webapps.eobroker.admin.shared.dtos.UserDescriptionDTO;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 27/10/2016.
 */
public class RemoveUser extends GwtEvent<RemoveUserHandler> {

    public static Type<RemoveUserHandler> TYPE = new Type<RemoveUserHandler>();

    private UserDescriptionDTO userDescriptionDTO;

    public RemoveUser(UserDescriptionDTO userDescriptionDTO) {
        this.userDescriptionDTO = userDescriptionDTO;
    }

    public UserDescriptionDTO getUserDescriptionDTO() {
        return userDescriptionDTO;
    }

    public Type<RemoveUserHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveUserHandler handler) {
        handler.onRemoveUser(this);
    }
}
