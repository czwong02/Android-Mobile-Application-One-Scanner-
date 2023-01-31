package com.example.onescanner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.mlsdk.MLAnalyzerFactory;
import com.huawei.hms.mlsdk.common.MLFrame;
import com.huawei.hms.mlsdk.text.MLLocalTextSetting;
import com.huawei.hms.mlsdk.text.MLText;
import com.huawei.hms.mlsdk.text.MLTextAnalyzer;

import java.io.IOException;

public class textRecognitionPage extends AppCompatActivity {

    private static final int GET_IMAGE_REQUEST_CODE = 22;

    private MLTextAnalyzer mTextAnalyzer;

    ImageView ivBitmap;
    TextView tvResult, back;
    TextView buttonAddImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_recognition_page);

        ivBitmap = findViewById(R.id.iv_bitmap);
        tvResult = findViewById(R.id.tv_result);
        buttonAddImage = findViewById(R.id.button_add_image);
        back = findViewById(R.id.back);

        buttonAddImage.setOnClickListener(v -> getImage());

        back.setOnClickListener(view -> {
            Intent intent = new Intent(textRecognitionPage.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        createMLTextAnalyzer();

    }

    private void getImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GET_IMAGE_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap selectedBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                ivBitmap.setImageBitmap(selectedBitmap);
                asyncAnalyzeText(selectedBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createMLTextAnalyzer() {
        MLLocalTextSetting setting = new MLLocalTextSetting.Factory()
                .setOCRMode(MLLocalTextSetting.OCR_DETECT_MODE)
                .setLanguage("en")
                .create();

        mTextAnalyzer = MLAnalyzerFactory.getInstance().getLocalTextAnalyzer(setting);
    }

    private void asyncAnalyzeText(Bitmap bitmap) {

        if (mTextAnalyzer == null) {
            createMLTextAnalyzer();
        }

        MLFrame frame = MLFrame.fromBitmap(bitmap);

        Task<MLText> task = mTextAnalyzer.asyncAnalyseFrame(frame);
        task.addOnSuccessListener(text -> {
            if(!text.getStringValue().equals("")) {
                tvResult.setText(text.getStringValue());
                tvResult.setMovementMethod(new ScrollingMovementMethod());
            } else {
                Toast.makeText(textRecognitionPage.this, "No result found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> tvResult.setText(e.getMessage()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            if (mTextAnalyzer != null)
                mTextAnalyzer.stop();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}