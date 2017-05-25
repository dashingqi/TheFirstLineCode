package com.dashingqi.downloaddemo;

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

    private DownLoadService.DownLoadBinder mBinder ;


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mBinder = (DownLoadService.DownLoadBinder) service;
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
        Intent intent = new Intent(MainActivity.this,DownLoadService.class);
        startService(intent);//开启服务
        bindService(intent,connection,BIND_AUTO_CREATE);//使服务长期在后台运行

        //进行服务的申请
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }

    private void initView() {
        startDownload = (Button) findViewById(R.id.start_download);
        pausedDownload = (Button) findViewById(R.id.pause_download);
        cancelDownload = (Button) findViewById(R.id.cancel_download);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_download:
                String url = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
                mBinder.startDownload(url);
                break;
            case R.id.pause_download:
                mBinder.pauseDownload();
                break;
            case R.id.cancel_download:
                mBinder.cancelDownload();
                break;
        }

    }
    //服务申请的回调方法

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(MainActivity.this,"权限被拒绝应用无法使用",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);//解绑服务 防止内存泄露
    }
}
