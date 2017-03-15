package com.tgrigorov.postbox.http.data;

import java.util.LinkedList;
import java.util.List;

public class MailListModel extends Model {
    public MailListModel() {
        value = new LinkedList<>();
    }

    public List<MailDetailModel> value;
}
