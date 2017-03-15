package com.tgrigorov.postbox.services.data;

import com.tgrigorov.postbox.http.data.MailDetailModel;
import com.tgrigorov.postbox.http.data.MailListModel;
import com.tgrigorov.postbox.data.entities.Person;

public interface IPersonService {
    Person loadByAddress(final String address);

    Person create(final MailDetailModel.PersonInfo data);
}
