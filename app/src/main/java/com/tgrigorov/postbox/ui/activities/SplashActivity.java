package com.tgrigorov.postbox.ui.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.tgrigorov.postbox.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        switchActivity();
    }

    private void switchActivity() {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                timer.cancel();
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }
}
