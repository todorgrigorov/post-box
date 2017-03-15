package com.tgrigorov.postbox.ui.notifications;


import android.content.Context;
import android.widget.Toast;

public class Toaster implements IToaster {
    public void make(Context context, String text) {
        Toast.makeText(context, text, DURATION).show();
    }

    private static final int DURATION = Toast.LENGTH_SHORT;
}
