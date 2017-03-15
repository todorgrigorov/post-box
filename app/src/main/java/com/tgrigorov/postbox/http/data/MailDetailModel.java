package com.tgrigorov.postbox.http.data;


import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class MailDetailModel extends Model {
    public MailDetailModel() {
        toRecipients = new LinkedList<>();
    }

    public String id;
    public String subject;
    public Date receivedDateTime;
    public Date createdDateTime;
    public Date lastModifiedDateTime;
    public MailSender sender;
    public List<MailSender> toRecipients;
    public Body body;
    public String bodyPreview;

    public class MailSender {
        public MailSender() {
            emailAddress = new PersonInfo();
        }

        public PersonInfo emailAddress;
    }

    public class PersonInfo {
        public String name;
        public String address;
    }

    public class Body {
        public Body() {
            contentType = "Text";
        }

        public String contentType;
        public String content;
    }
}
