package com.tgrigorov.postbox.ui.notifications;


import android.content.Context;

public interface IToaster {
    void make(Context context, String text);
}
