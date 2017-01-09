package com.geocento.webapps.eobroker.admin.client.events;

import com.geocento.webapps.eobroker.admin.shared.dtos.UserDescriptionDTO;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 09/01/2017.
 */
public class EditUser extends GwtEvent<EditUserHandler> {

    public static Type<EditUserHandler> TYPE = new Type<EditUserHandler>();

    private final UserDescriptionDTO userDescriptionDTO;

    public EditUser(UserDescriptionDTO userDescriptionDTO) {
        this.userDescriptionDTO = userDescriptionDTO;
    }

    public UserDescriptionDTO getUserDescriptionDTO() {
        return userDescriptionDTO;
    }

    public Type<EditUserHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(EditUserHandler handler) {
        handler.onEditUser(this);
    }
}
