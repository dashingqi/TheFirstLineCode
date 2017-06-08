package com.dashingqi.download1;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button startDownload;
    private Button pausedDownload;
    private Button cancelDownload;

    private DownloadService.MyDownloadBinder downloadBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.MyDownloadBinder) service;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        startDownload.setOnClickListener(this);
        pausedDownload.setOnClickListener(this);
        cancelDownload.setOnClickListener(this);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
        }

        Intent intent = new Intent(MainActivity.this, DownloadService.class);
        startService(intent);
        bindService(intent,connection,BIND_AUTO_CREATE);

    }
    private void initView() {
        startDownload = (Button) findViewById(R.id.start_download);
        pausedDownload = (Button) findViewById(R.id.pause_download);
        cancelDownload = (Button) findViewById(R.id.cancel_download);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.start_download:
                String url = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
                downloadBinder.startDownload(url);
                break;
            case R.id.pause_download:
                break;
            case R.id.cancel_download:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,"权限被拒绝 程序无法使用过",Toast.LENGTH_SHORT).show();
                    finish();//结束 权限都被拒绝了 干嘛要用呢、对吧。
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
