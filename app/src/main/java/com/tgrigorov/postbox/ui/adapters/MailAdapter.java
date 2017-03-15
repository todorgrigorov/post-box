package com.tgrigorov.postbox.ui.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tgrigorov.postbox.R;
import com.tgrigorov.postbox.data.entities.Mail;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class MailAdapter extends ArrayAdapter<Mail> {
    public MailAdapter(Context context, int resource, List<Mail> items) {
        super(context, resource, items);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(getContext());
            view = inflater.inflate(R.layout.mail_row, null);
        }

        Mail mail = getItem(position);
        if (mail != null) {
            TextView sender = (TextView) view.findViewById(R.id.senderLabel);
            TextView time = (TextView) view.findViewById(R.id.timeLabel);
            TextView subject = (TextView) view.findViewById(R.id.subjectLabel);
            TextView preview = (TextView) view.findViewById(R.id.previewLabel);

            sender.setText(mail.getSender().getName());
            time.setText(new SimpleDateFormat("dd/MM HH:mm").format(mail.getReceivedDate()));
            subject.setText(mail.getSubject());
            preview.setText(Html.fromHtml(mail.getBodyPreview()));

            if (!mail.isSeen()) {
                sender.setTypeface(null, Typeface.BOLD);
                time.setTypeface(null, Typeface.BOLD);
                subject.setTypeface(null, Typeface.BOLD);
            } else {
                sender.setTypeface(Typeface.DEFAULT);
                time.setTypeface(Typeface.DEFAULT);
                subject.setTypeface(Typeface.DEFAULT);
            }
        }

        return view;
    }
}
