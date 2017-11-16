package com.geocento.webapps.eobroker.admin.client.events;

import com.geocento.webapps.eobroker.admin.shared.dtos.ChallengeDTO;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created by thomas on 16/11/2017.
 */
public class RemoveChallenge extends GwtEvent<RemoveChallengeHandler> {

    public static Type<RemoveChallengeHandler> TYPE = new Type<RemoveChallengeHandler>();

    private final ChallengeDTO challengeDTO;

    public RemoveChallenge(ChallengeDTO challengeDTO) {
        this.challengeDTO = challengeDTO;
    }

    public ChallengeDTO getChallengeDTO() {
        return challengeDTO;
    }

    public Type<RemoveChallengeHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(RemoveChallengeHandler handler) {
        handler.onRemoveChallenge(this);
    }
}
