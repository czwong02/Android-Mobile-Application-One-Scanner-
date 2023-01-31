package com.example.onescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    View share, profile;
    CardView qrScanner, textRecognition, landmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        share = findViewById(R.id.miShare);
        profile = findViewById(R.id.miProfile);
        textRecognition = findViewById(R.id.textScanner);
        qrScanner = findViewById(R.id.qrScanner);
        landmark = findViewById(R.id.landmarkRecognition);

        share.setOnClickListener(view -> {
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);

            // type of the content to be shared
            sharingIntent.setType("text/plain");

            // Body of the content
            String shareBody = "Hey! I've just downloaded the One Scanner Mobile App! Download the App at App Gallery now!  https://appgallery.huawei.com/app/C107085323";

            // subject of the content. you can share anything
            String shareSubject = "One Scanner";

            // passing body of the content
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);

            // passing subject of the content
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
        });

        profile.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, profilePage.class);
            startActivity(intent);
        });

        qrScanner.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, qrScannerMainPage.class);
            startActivity(intent);            });

        textRecognition.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, textRecognitionPage.class);
            startActivity(intent);
        });

        landmark.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, landmarkRecognition.class);
            startActivity(intent);
        });

    }
}