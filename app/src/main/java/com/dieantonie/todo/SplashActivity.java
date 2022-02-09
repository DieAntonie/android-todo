package com.dieantonie.todo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // display splash screen
        setContentView(R.layout.activity_splash);
        // hide support action bar
        getSupportActionBar().hide();

        // set main activity intent
        final Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        // set 1 second timeout
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // start main activity
                startActivity(intent);
                // finish
                finish();
            }
        }, 1000);
    }
}