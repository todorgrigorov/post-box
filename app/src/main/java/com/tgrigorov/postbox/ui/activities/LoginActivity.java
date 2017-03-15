package com.tgrigorov.postbox.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.microsoft.aad.adal.AuthenticationContext;
import com.tgrigorov.postbox.PostBox;
import com.tgrigorov.postbox.R;
import com.tgrigorov.postbox.data.entities.User;
import com.tgrigorov.postbox.services.background.IBackgroundResult;

import java.util.concurrent.Callable;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        PostBox.getOpenAuthenticationProvider().initContext(this, new Callable<Void>() {
            public Void call() throws Exception {
                onAuthenticationComplete();
                return null;
            }
        });

        checkLoggedInUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        AuthenticationContext context = PostBox.getOpenAuthenticationProvider().getAuthenticationContext();
        if (context != null) {
            context.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void checkLoggedInUser() {
        User user = PostBox.getUserService().loadCurrent();

        if (user != null) {
            PostBox.getOpenAuthenticationProvider().refreshToken(user);
        } else {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.loginButton).setVisibility(View.VISIBLE);

            findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                    findViewById(R.id.loginButton).setVisibility(View.INVISIBLE);

                    PostBox.getOpenAuthenticationProvider().acquireToken(LoginActivity.this);
                }
            });
        }
    }

    private void onAuthenticationComplete() {
        PostBox.getMailService().synchronize(new IBackgroundResult<Void>() {
            public void success(Void data) {
                Intent intent = new Intent(getBaseContext(), InboxActivity.class);
                startActivity(intent);
            }

            public void cancel() {

            }
        });
    }
}
