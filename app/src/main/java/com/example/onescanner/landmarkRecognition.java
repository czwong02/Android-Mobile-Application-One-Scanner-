package com.example.onescanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLApplication;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzer;
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzerSetting;

import java.io.IOException;
import java.util.List;

public class landmarkRecognition extends AppCompatActivity {
    private MLRemoteLandmarkAnalyzer analyzer;
    private static final int REQUEST_SELECT_IMAGE = 22;

    ImageView ivBitmap;
    TextView tvResult, back;
    TextView buttonAddImage, search;
    String landmark;
    boolean canSearch = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landmark_recognition);

        ivBitmap = findViewById(R.id.iv_bitmap);
        tvResult = findViewById(R.id.tv_result);
        buttonAddImage = findViewById(R.id.button_add_image);
        back = findViewById(R.id.back);
        search = findViewById(R.id.search);

        search.setOnClickListener(view -> {
            if(canSearch) {
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                String term = landmark;
                intent.putExtra(SearchManager.QUERY, term);
                startActivity(intent);
            } else {
                Toast.makeText(landmarkRecognition.this, "Please add picture first", Toast.LENGTH_SHORT).show();
            }
        });

        buttonAddImage.setOnClickListener(v -> {
            selectLocalImage();
        });

        back.setOnClickListener(view -> {
            Intent intent = new Intent(landmarkRecognition.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        createMLRemoteLandmarkAnalyzer();
    }

    private void setApiKey() {
        AGConnectServicesConfig config = AGConnectServicesConfig.fromContext(getApplication());
        MLApplication.getInstance().setApiKey(config.getString("client/api_key"));
    }

    private void selectLocalImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivBitmap.setImageBitmap(selectedBitmap);
                asyncAnalyzeLandmark(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createMLRemoteLandmarkAnalyzer() {
        MLRemoteLandmarkAnalyzerSetting settings = new MLRemoteLandmarkAnalyzerSetting.Factory()
                .setLargestNumOfReturns(1)
                .setPatternType(MLRemoteLandmarkAnalyzerSetting.STEADY_PATTERN)
                .create();

        analyzer = MLAnalyzerFactory.getInstance().getRemoteLandmarkAnalyzer(settings);
    }

    private void asyncAnalyzeLandmark(Bitmap bitmap) {

        if (analyzer == null) {
            createMLRemoteLandmarkAnalyzer();
        }

        MLFrame mlFrame = new MLFrame.Creator().setBitmap(bitmap).create();
        setApiKey();
        Task<List<MLRemoteLandmark>> task = analyzer.asyncAnalyseFrame(mlFrame);

        task.addOnSuccessListener(mlRemoteLandmarks -> {
            tvResult.setText("Name: " + mlRemoteLandmarks.get(0).getLandmark() + "\nLocation: " + mlRemoteLandmarks.get(0).getBorder());
            tvResult.setMovementMethod(new ScrollingMovementMethod());
            landmark = mlRemoteLandmarks.get(0).getLandmark();
            canSearch = true;
            search.setVisibility(View.VISIBLE);
            Log.d("BitMapUtils", mlRemoteLandmarks.get(0).getLandmark());
        }).addOnFailureListener(new OnFailureListener() {

            public void onFailure(Exception e) {
                tvResult.setText(e.getMessage());
                Toast.makeText(landmarkRecognition.this, "Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (analyzer != null)
                analyzer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}