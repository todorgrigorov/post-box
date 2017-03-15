package com.tgrigorov.postbox.services.data;

import com.tgrigorov.postbox.http.data.MailDetailModel;
import com.tgrigorov.postbox.http.data.MailListModel;
import com.tgrigorov.postbox.data.entities.Mail;
import com.tgrigorov.postbox.data.entities.User;
import com.tgrigorov.postbox.http.data.MailSingleModel;
import com.tgrigorov.postbox.services.background.IBackgroundResult;

public interface IMailService {
    void synchronize(final IBackgroundResult<Void> result);
    Mail create(MailDetailModel detail, User user);
    void send(MailSingleModel detail, IBackgroundResult<MailSingleModel> result);
    void delete(MailDetailModel mail, IBackgroundResult<MailDetailModel> result);
}
