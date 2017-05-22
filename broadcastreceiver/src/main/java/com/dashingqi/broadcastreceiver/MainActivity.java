package com.dashingqi.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
    private Button forceOffline;

//    private IntentFilter intentFilter;
//    private MyBroadcastReceiver myBroadcastReceicer;
//    private LocalBroadcastManager instance;
//    private LocalReceiver localReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        forceOffline = (Button) findViewById(R.id.btn);
        forceOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.dashingqi.broadcastreceiver.offline");
                sendBroadcast(intent);
            }
        });
//        intentFilter = new IntentFilter();
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        myBroadcastReceicer = new MyBroadcastReceiver();
//        registerReceiver(myBroadcastReceicer,intentFilter);
//        Button btn = (Button) findViewById(R.id.btn);
//        instance = LocalBroadcastManager.getInstance(this);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent("com.dashingqi.broadcastreceiver.LocalReceiver");
//                instance.sendBroadcast(intent);
//            }
//        });
//        intentFilter = new IntentFilter();
//        intentFilter.addAction("com.dashingqi.broadcastreceiver.LocalReceiver");
//        localReceiver = new LocalReceiver();
//        instance.registerReceiver(localReceiver,intentFilter);
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        instance.unregisterReceiver(localReceiver);
//    }
////     class MyBroadcastReceiver extends BroadcastReceiver{
////        @Override
////        public void onReceive(Context context, Intent intent) {
////            ConnectivityManager systemService = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
////            NetworkInfo activeNetworkInfo = systemService.getActiveNetworkInfo();
////            if(activeNetworkInfo!=null && activeNetworkInfo.isAvailable()){
////                Toast.makeText(context,"network is Available",Toast.LENGTH_LONG).show();
////            }else {
////                Toast.makeText(context,"network is UnAvailable",Toast.LENGTH_LONG).show();
////            }
////        }
////    }
//    class LocalReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Toast.makeText(context,"receiver local",Toast.LENGTH_LONG).show();
//        }
    }
}
