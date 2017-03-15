package com.tgrigorov.postbox.data.entities;

import java.util.Date;

public class Mail extends Entity {
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Person getSender() {
        return sender;
    }

    public void setSender(Person sender) {
        this.sender = sender;
    }

    public Date getReceivedDate() {
        return receivedDate;
    }

    public void setReceivedDate(Date receivedDate) {
        this.receivedDate = receivedDate;
    }

    public String getBodyPreview() {
        return bodyPreview;
    }

    public void setBodyPreview(String bodyPreview) {
        this.bodyPreview = bodyPreview;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    @DbColumn("SUBJECT")
    private String subject;

    @DbColumn("BODY")
    private String body;

    @DbColumn("BODY_PREVIEW")
    private String bodyPreview;

    @DbColumn("EXTERNAL_ID")
    private String externalId;

    @DbColumn("RECEIVED_DATE")
    private Date receivedDate;

    @DbColumn("USER_ID")
    @DbForeign()
    private User user;

    @DbColumn("SENDER_ID")
    @DbForeign()
    private Person sender;

    @DbColumn("SEEN")
    private boolean seen;
}
