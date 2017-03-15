package com.tgrigorov.postbox.services.data;

import com.tgrigorov.postbox.PostBox;
import com.tgrigorov.postbox.http.data.MailDetailModel;
import com.tgrigorov.postbox.http.data.MailListModel;
import com.tgrigorov.postbox.data.entities.Mail;
import com.tgrigorov.postbox.data.entities.Person;
import com.tgrigorov.postbox.data.entities.User;
import com.tgrigorov.postbox.http.data.MailSingleModel;
import com.tgrigorov.postbox.services.background.BackgroundTask;
import com.tgrigorov.postbox.services.background.IBackgroundProcess;
import com.tgrigorov.postbox.services.background.IBackgroundResult;
import com.tgrigorov.postbox.utils.IPredicate;
import com.tgrigorov.postbox.utils.ListUtils;

import java.util.List;

public class MailService implements IMailService {
    public void synchronize(final IBackgroundResult<Void> result) {
        final User user = PostBox.getUserService().loadCurrent();
        PostBox.getRestClient().getOperation(MailListModel.class, INBOX_GET, user.getToken()).get(new IBackgroundResult<MailListModel>() {
            public void success(final MailListModel data) {
                new BackgroundTask<>(new IBackgroundProcess<Void>() {
                    public Void run() {
                        synchronizeMails(data, user);
                        return null;
                    }
                }, result);
            }

            public void cancel() {
            }
        });
    }

    public Mail create(MailDetailModel detail, User user) {
        Mail mail = new Mail();
        mail.setExternalId(detail.id);
        mail.setSubject(detail.subject);
        mail.setBody(detail.body.content);
        mail.setBodyPreview(detail.bodyPreview);
        mail.setReceivedDate(detail.receivedDateTime);
        mail.setSeen(false);

        Person person = PostBox.getPersonService().create(detail.sender.emailAddress);
        mail.setSender(person);
        mail.setUser(user);
        mail = PostBox.getDbContext().getContext(Mail.class).create(mail);
        return mail;
    }

    public void send(MailSingleModel detail, IBackgroundResult<MailSingleModel> result) {
        User user = PostBox.getUserService().loadCurrent();
        PostBox.getRestClient().getOperation(MailSingleModel.class, SEND, user.getToken()).post(detail, result);
    }

    public void delete(MailDetailModel mail, IBackgroundResult<MailDetailModel> result) {
        User user = PostBox.getUserService().loadCurrent();
        PostBox.getRestClient().getOperation(MailDetailModel.class, String.format(DELETE, mail.id), user.getToken()).delete(result);
    }

    private void synchronizeMails(MailListModel data, final User user) {
        List<Mail> syncedMails = PostBox.getDbContext().getContext(Mail.class).list(); // load all mails in the DB

        if (syncedMails != null) {
            for (final MailDetailModel detail : data.value) {
                List<Mail> filtered = filterByExternalId(syncedMails, detail.id);
                Mail mail = ListUtils.firstOrDefault(filtered); // getOperation the corresponding one (if exists)
                if (mail == null) {
                    // mail has not been synced
                    create(detail, user);
                }
            }

            synchronizeDeletedMails(syncedMails, data);
        }
    }

    private void synchronizeDeletedMails(List<Mail> mails, MailListModel data) {
        for (Mail mail : mails) {
            boolean deleted = true;
            for (MailDetailModel model : data.value) {
                if (model.id.equals(mail.getExternalId())) {
                    deleted = false;
                    break;
                }
            }

            if (deleted) {
                PostBox.getDbContext().getContext(Mail.class).delete(mail.getId());
            }
        }
    }

    private List<Mail> filterByExternalId(List<Mail> mails, final String id) {
        return ListUtils.filter(mails, new IPredicate<Mail>() {
            public boolean filter(Mail item) {
                return item.getExternalId().equals(id);
            }
        });
    }

    private static final String INBOX_GET = "MailFolders/Inbox/messages";
    private static final String SEND = "sendmail";
    private static final String DELETE = "messages/%s";
}
