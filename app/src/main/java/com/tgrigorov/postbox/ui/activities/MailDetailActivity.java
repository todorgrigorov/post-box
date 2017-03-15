package com.tgrigorov.postbox.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;

import com.tgrigorov.postbox.PostBox;
import com.tgrigorov.postbox.R;
import com.tgrigorov.postbox.data.entities.Mail;

public class MailDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.detailToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int id = bundle.getInt("id");
            mail = PostBox.getDbContext().getContext(Mail.class).load(id);
            setData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mail_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mailDetailDelete:
                Intent intent = new Intent();
                intent.putExtra("id", mail.getId());
                setResult(InboxActivity.DETAIL_DELETED_RESULT, intent);
                finish();
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
        TextView from = (TextView) findViewById(R.id.detailFrom);
        TextView subject = (TextView) findViewById(R.id.detailSubject);
        WebView message = (WebView) findViewById(R.id.detailMessage);

        String fromText = mail.getSender().getName() + " " + mail.getSender().getAddress();
        from.setText(fromText);
        subject.setText(mail.getSubject());

        message.getSettings().setJavaScriptEnabled(true);
        message.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        message.setScrollbarFadingEnabled(false);
        message.loadDataWithBaseURL("", mail.getBody(), "text/html", "UTF-8", "");
    }

    private Mail mail;
}
