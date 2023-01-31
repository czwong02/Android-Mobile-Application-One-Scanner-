package com.example.onescanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;

public class qrScannerMainPage extends AppCompatActivity {

    TextView back;
    TextView scan;
    TextView output;
    public static final int DEFINED_CODE = 222;
    public static final int REQUEST_CODE_SCAN = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner_main_page);
        back = findViewById(R.id.backButton);
        back.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(qrScannerMainPage.this, MainActivity.class);
            startActivity(intent);
        });
        scan = findViewById(R.id.scan);
        scan.setOnClickListener(view -> scan());
    }

    public void scan() {
        // Replace DEFINED_CODE with the code that you customize for receiving the permission verification result.
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE}, DEFINED_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions == null || grantResults == null || grantResults.length < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (requestCode == DEFINED_CODE) {
            // Go to the customized scanning UI DefinedActivity.
// Display the scanning UI in Default View mode.
            int result = ScanUtil.startScan(qrScannerMainPage.this, REQUEST_CODE_SCAN, new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create());        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Process the result after the scanning is complete.
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) {
            return;
        }
        // Use ScanUtil.RESULT as the key value to obtain the return value of HmsScan from data returned by the onActivityResult method.
        if (requestCode == REQUEST_CODE_SCAN) {
            Object obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj instanceof HmsScan) {
                if (!TextUtils.isEmpty(((HmsScan) obj).getOriginalValue())) {
                    Toast.makeText(this, "Scan successfully",
                            Toast.LENGTH_SHORT).show();
                    output = findViewById(R.id.output);
                    String value = "<html><font color=#757b86><b><a href=\"" + ((HmsScan) obj).getOriginalValue().toString() + "\">" + ((HmsScan) obj).getOriginalValue().toString() + "</a></b></font> </html>";
                    Spannable spannedText = (Spannable)
                            Html.fromHtml(value);
                    output.setMovementMethod(LinkMovementMethod.getInstance());

                    Spannable processedText = removeUnderlines(spannedText);
                    output.setText(processedText);
                }
                return;
            }
        }
    }

    public static Spannable removeUnderlines(Spannable p_Text) {
        URLSpan[] spans = p_Text.getSpans(0, p_Text.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = p_Text.getSpanStart(span);
            int end = p_Text.getSpanEnd(span);
            p_Text.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            p_Text.setSpan(span, start, end, 0);
        }
        return p_Text;
    }
}