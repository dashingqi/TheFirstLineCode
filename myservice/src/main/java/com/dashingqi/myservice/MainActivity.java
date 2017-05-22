package com.dashingqi.myservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button startService;
    private Button stopService;
    private Button bindService;
    private Button unbindService;

    private MyService.DownLoadBinder mBinder;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //活动与服务绑定的时候调用
            mBinder = (MyService.DownLoadBinder) service;
            mBinder.startDowanload();
            mBinder.getProgress();

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //活动与服务解绑的时候调用

        }
    };
    private Button startIntentService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService = (Button) findViewById(R.id.start_service);
        stopService = (Button) findViewById(R.id.stop_service);
        bindService = (Button) findViewById(R.id.bind_service);
        unbindService = (Button) findViewById(R.id.unbind_service);
        startIntentService = (Button) findViewById(R.id.start_intent_service);
        startService.setOnClickListener(this);
        stopService.setOnClickListener(this);
        bindService.setOnClickListener(this);
        unbindService.setOnClickListener(this);
        startIntentService.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.start_service:
                Intent startService = new Intent(MainActivity.this, MyService.class);
                startService(startService);//启动服务
                break;
            case R.id.stop_service:
                Intent stopService = new Intent(MainActivity.this, MyService.class);
                stopService(stopService);//停止服务
                break;
            case R.id.bind_service:
                //绑定服务操作
                Intent intent = new Intent(MainActivity.this, MyService.class);
                bindService(intent,connection,BIND_AUTO_CREATE);//绑定服务
                break;
            case R.id.unbind_service:
                //解绑服务操作
                unbindService(connection);

                break;
            case R.id.start_intent_service:
                Log.d("MainActivity" ,"thread id is "+Thread.currentThread().getId());
                Intent intentService = new Intent(MainActivity.this, MyIntentService.class);
                startService(intentService);
                break;
            default:
                break;

        }

    }
}
