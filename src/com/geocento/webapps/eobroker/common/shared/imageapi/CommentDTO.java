package com.geocento.webapps.eobroker.common.shared.imageapi;

import java.util.Date;

/**
 * Created by thomas on 09/03/2016.
 */
public class CommentDTO {
    private String comment;
    private String from;
    private Date created;

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }
}
