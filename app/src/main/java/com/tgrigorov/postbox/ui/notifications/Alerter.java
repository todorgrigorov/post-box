package com.tgrigorov.postbox.ui.notifications;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Alerter implements IAlerter {
    public void show(Context context, String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(text);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setCancelable(true);
        builder.create().show();
    }
}
