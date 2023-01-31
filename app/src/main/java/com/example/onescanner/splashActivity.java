package com.example.onescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splashActivity extends AppCompatActivity {

    Handler mHandler;
    Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler = new Handler();
        mRunnable = () -> {
            Intent intent = new Intent(splashActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        };

        // its trigger runnable after 4000 millisecond.
        mHandler.postDelayed(mRunnable,4000);
    }
}