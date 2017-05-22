package com.dashingqi.notificationtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                Intent intent = new Intent(MainActivity.this, NotificationActivity.class);
                PendingIntent pi = PendingIntent.getActivity(MainActivity.this, 0, intent, 0);
                Notification notification = new NotificationCompat.Builder(MainActivity.this)
                        .setContentText("this is content text")
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                        .setContentTitle("this is content title")
//                        .setSound(Uri.fromFile(new File("/system/media/audio/ringtones/luna.ogg")))//设置通知音乐
//                        .setVibrate(new long[]{0,1000,1000,1000})//设置振动的频率以及时长  需要权限
//                        .setLights(Color.GREEN,1000,1000)//设置LED通知
                        .setDefaults(NotificationCompat.DEFAULT_ALL)//一切的通知设置为默认效果 有手机的环境决定
                        .setContentIntent(pi)
                       // .setAutoCancel(true)
                        .build();
                manager.notify(1,notification);

            }
        });
    }
}
