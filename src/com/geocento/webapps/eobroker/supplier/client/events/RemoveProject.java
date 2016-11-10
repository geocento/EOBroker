package com.geocento.webapps.eobroker.supplier.client.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 08/11/2016.
 */
public class RemoveProject extends GwtEvent<RemoveProjectHandler> {

    public static Type<RemoveProjectHandler> TYPE = new Type<RemoveProjectHandler>();

    private final Long projectId;

    public RemoveProject(Long id) {
        this.projectId = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Type<RemoveProjectHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveProjectHandler handler) {
        handler.onRemoveProject(this);
    }
}
