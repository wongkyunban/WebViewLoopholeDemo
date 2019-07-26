package com.ti.webviewloopholedemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private WebView mWebView;
    private FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkMyAppPermission();

        frameLayout = findViewById(R.id.container);
        mWebView = new WebView(getApplicationContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(layoutParams);
        mWebView.setWebViewClient(new WebViewClient());
        frameLayout.addView(mWebView);
//        mWebView.loadUrl("http://www.baidu.com");
//        mWebView.loadUrl("file:///android_asset/html/index.html");
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "/aaa/index.html");
        Uri uri = FileProvider.getUriForFile(getApplicationContext(),"com.mydomain.fileprovider",file);
        String urlString = uri.toString();
        Log.d("MM##",urlString);
        mWebView.loadUrl(urlString);

    }

    private static final  int PERMISSION_REQUEST_CODE = 1;

    private void checkMyAppPermission() {
        // android6.0 API 23后需要动态申请权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkRs = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkRs == PackageManager.PERMISSION_DENIED) {
                // 没有权限要去申请
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        }
    }

    // requestCode是requestPermissions(String[], int)传进来的int值
    // permissions是申请的权限
    // grantResults与permissions一一对应的申请结果，它的取值只能是PackageManager.PERMISSION_GRANTED或PackageManager.PERMISSION_DENIED
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PERMISSION_REQUEST_CODE == requestCode) {
            for (int i = 0; i < permissions.length; i++) {
                if((Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[i]) &&
                PackageManager.PERMISSION_DENIED == grantResults[i])
                ||(Manifest.permission.READ_EXTERNAL_STORAGE.equals(permissions[i]) &&
                        PackageManager.PERMISSION_DENIED == grantResults[i])) {
                    Toast.makeText(MainActivity.this,"获取SD卡的权限失败",Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadData(null, "text/html", "utf-8");
            mWebView.clearHistory();
            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}
