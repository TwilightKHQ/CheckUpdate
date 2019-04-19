package com.example.zhongzhiqiang.checkupdate;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private String versionName;
    private int versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView versionInfo = (TextView) findViewById(R.id.version_info);
        //获取PackageManager的实例 获取当前的版本名
        PackageManager packageManager = getPackageManager();
        try {
            //getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            versionName =  packInfo.versionName;
            versionCode = packInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //显示版本信息
        versionInfo.setText("当前版本： " + versionName);

        //点击发送网络请求
        Button checkButton = (Button) findViewById(R.id.check_button);
        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestWithOkHttp(getString(R.string.update_info));
                Toast.makeText(MainActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendRequestWithOkHttp(final String address){
        //OkHttp 开启线程来发送网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(address)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithGSON(responseData);        //用GSON解析JSON
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //解析Json数据
    private void parseJSONWithGSON(String jsonData) {
        Gson gson = new Gson();
        UpdateBean updateBean = gson.fromJson(jsonData, UpdateBean.class);
        //验证Json解析是否正确
        Log.d("MainActivity", "VersionCode is " + updateBean.getVersionCode());
        Log.d("MainActivity", "VersionName is " + updateBean.getVersionName());
        Log.d("MainActivity", "DownloadUrl is " + updateBean.getDownloadUrl());

    }
}
