package com.tgrigorov.postbox.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.tgrigorov.postbox.PostBox;
import com.tgrigorov.postbox.R;
import com.tgrigorov.postbox.data.entities.User;
import com.tgrigorov.postbox.http.data.MailDetailModel;
import com.tgrigorov.postbox.http.data.MailSingleModel;
import com.tgrigorov.postbox.services.background.IBackgroundResult;
import com.tgrigorov.postbox.utils.StringUtils;

public class MailEditActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_edit);

        Toolbar toolbar = (Toolbar) findViewById(R.id.editToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mail_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mailEditSend:
                onSendItemSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setData() {
        User user = PostBox.getUserService().loadCurrent();

        EditText from = (EditText) findViewById(R.id.editFrom);
        EditText to = (EditText) findViewById(R.id.editTo);
        from.setText(user.getPerson().getAddress());
        to.requestFocus();
    }

    private void onSendItemSelected() {
        if (isDataValid()) {
            EditText to = (EditText) findViewById(R.id.editTo);
            EditText subject = (EditText) findViewById(R.id.editSubject);
            EditText message = (EditText) findViewById(R.id.editMessage);

            MailDetailModel detail = new MailDetailModel();
            detail.subject = subject.getText().toString();
            MailDetailModel.MailSender recipient = new MailDetailModel().new MailSender();
            recipient.emailAddress.address = to.getText().toString();
            detail.toRecipients.add(recipient);
            detail.body = new MailDetailModel().new Body();
            detail.body.content = message.getText().toString();

            MailSingleModel model = new MailSingleModel();
            model.message = detail;

            PostBox.getMailService().send(model, new IBackgroundResult<MailSingleModel>() {
                public void success(MailSingleModel data) {
                    Intent intent = new Intent();
                    setResult(InboxActivity.EDIT_SENT_RESULT, intent);
                    finish();
                }

                public void cancel() {
                }
            });
        }
    }

    private boolean isDataValid() {
        boolean valid = true;

        EditText to = (EditText) findViewById(R.id.editTo);
        EditText subject = (EditText) findViewById(R.id.editSubject);
        EditText message = (EditText) findViewById(R.id.editMessage);

        String recipient = to.getText().toString();
        if (StringUtils.nullOrEmpty(recipient)) {
            valid = false;
            PostBox.getAlerter().show(MailEditActivity.this, getResources().getString(R.string.recipient_required));
        } else if (!StringUtils.nullOrEmpty(recipient) && !StringUtils.isValidEmail(recipient)) {
            valid = false;
            PostBox.getAlerter().show(MailEditActivity.this, getResources().getString(R.string.recipient_invalid));
        } else if (StringUtils.nullOrEmpty(subject.getText().toString())) {
            valid = false;
            PostBox.getAlerter().show(MailEditActivity.this, getResources().getString(R.string.subject_required));
        } else if (StringUtils.nullOrEmpty(message.getText().toString())) {
            valid = false;
            PostBox.getAlerter().show(MailEditActivity.this, getResources().getString(R.string.message_required));
        }

        return valid;
    }
}
