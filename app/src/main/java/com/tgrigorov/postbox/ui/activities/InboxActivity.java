package com.tgrigorov.postbox.ui.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tgrigorov.postbox.PostBox;
import com.tgrigorov.postbox.R;
import com.tgrigorov.postbox.data.entities.User;
import com.tgrigorov.postbox.http.data.MailDetailModel;
import com.tgrigorov.postbox.data.entities.Mail;
import com.tgrigorov.postbox.services.background.IBackgroundResult;
import com.tgrigorov.postbox.ui.adapters.MailAdapter;
import com.tgrigorov.postbox.utils.IPredicate;
import com.tgrigorov.postbox.utils.ListUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class InboxActivity extends AppCompatActivity {
    public static final int DETAIL_REQUEST = 100;
    public static final int DETAIL_DELETED_RESULT = 101;
    public static final int EDIT_REQUEST = 110;
    public static final int EDIT_SENT_RESULT = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inbox);

        Toolbar toolbar = (Toolbar) findViewById(R.id.inboxToolbar);
        setSupportActionBar(toolbar);

        initListView();
        setAdapter(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == DETAIL_REQUEST) {
            if (resultCode == DETAIL_DELETED_RESULT) {
                deleteMail(data);
            }
        } else if (requestCode == EDIT_REQUEST) {
            if (resultCode == EDIT_SENT_RESULT) {
                PostBox.getToaster().make(getBaseContext(), getResources().getString(R.string.mail_sent));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inbox, menu);
        initSearchView(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.inboxLogout:
                onLogoutItemSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void initListView() {
        listView = (ListView) findViewById(R.id.inboxListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(view, position);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                PostBox.getMailService().synchronize(new IBackgroundResult<Void>() {
                    public void success(Void data) {
                        setAdapter(null);
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    public void cancel() {

                    }
                });
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.inboxFab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onFabClicked();
            }
        });
    }

    private void setAdapter(IPredicate<Mail> filter) {
        List<Mail> mails = PostBox.getDbContext().getContext(Mail.class).list();

        if (filter != null) {
            mails = ListUtils.filter(mails, filter);
        }

        Collections.sort(mails, new Comparator<Mail>() {
            public int compare(Mail o1, Mail o2) {
                int result = 0;
                if (o1.getReceivedDate().before(o2.getReceivedDate())) {
                    result = 1;
                } else if (o1.getReceivedDate().after(o2.getReceivedDate())) {
                    result = -1;
                }
                return result;
            }
        });
        listView.setAdapter(new MailAdapter(this, R.layout.mail_row, mails));

        if (mails.size() == 0) {
            listView.setVisibility(View.GONE);
            findViewById(R.id.inboxEmptyMessage).setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            findViewById(R.id.inboxEmptyMessage).setVisibility(View.GONE);
        }
    }

    private void onItemClicked(View view, int position) {
        Mail mail = (Mail) (listView.getItemAtPosition(position));

        if (!mail.isSeen()) {
            updateSeen(view, mail);
        }

        Intent intent = new Intent(getBaseContext(), MailDetailActivity.class);
        intent.putExtra("id", mail.getId());
        startActivityForResult(intent, DETAIL_REQUEST);
    }

    private void onFabClicked() {
        Intent intent = new Intent(getBaseContext(), MailEditActivity.class);
        startActivityForResult(intent, EDIT_REQUEST);
    }

    private void updateSeen(View view, Mail mail) {
        mail.setSeen(true);
        PostBox.getDbContext().getContext(Mail.class).update(mail);

        TextView from = (TextView) view.findViewById(R.id.senderLabel);
        TextView time = (TextView) view.findViewById(R.id.timeLabel);
        TextView subject = (TextView) view.findViewById(R.id.subjectLabel);

        from.setTypeface(Typeface.DEFAULT);
        time.setTypeface(Typeface.DEFAULT);
        subject.setTypeface(Typeface.DEFAULT);
    }

    private void deleteMail(Intent data) {
        final Mail mail = PostBox.getDbContext().getContext(Mail.class).load(data.getIntExtra("id", 0));

        MailDetailModel detail = new MailDetailModel();
        detail.id = mail.getExternalId();
        PostBox.getMailService().delete(detail, new IBackgroundResult<MailDetailModel>() {
            public void success(MailDetailModel data) {
                PostBox.getDbContext().getContext(Mail.class).delete(mail.getId());
                PostBox.getToaster().make(getBaseContext(), getResources().getString(R.string.mail_deleted));
                setAdapter(null);
            }

            public void cancel() {

            }
        });
    }

    private void onLogoutItemSelected() {
        User user = PostBox.getUserService().loadCurrent();
        user.setCurrent(false);
        PostBox.getDbContext().getContext(User.class).update(user);

        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        startActivity(intent);
    }

    private void initSearchView(Menu menu) {
        final MenuItem item = menu.findItem(R.id.inboxSearch);
        final SearchView searchView = (SearchView) item.getActionView();

        searchView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            public void onViewAttachedToWindow(View v) {

            }

            public void onViewDetachedFromWindow(View v) {
                setAdapter(null);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                setAdapter(new IPredicate<Mail>() {
                    public boolean filter(Mail item) {
                        return item.getSubject().toLowerCase().contains(query.toLowerCase());
                    }
                });
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                EditText searchText = (EditText) findViewById(R.id.search_src_text);
                searchText.setTextColor(getResources().getColor(R.color.light_text));
                return false;
            }
        });

        ImageView closeButton = (ImageView) searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText searchText = (EditText) findViewById(R.id.search_src_text);
                searchText.setText("");
                searchView.setQuery("", false);
                searchView.onActionViewCollapsed();
                item.collapseActionView();
            }
        });
    }

    private ListView listView;
    private SwipeRefreshLayout swipeRefreshLayout;
}
