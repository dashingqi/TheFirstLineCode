package com.dashingqi.download1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import java.io.File;

public class DownloadService extends Service {

    private DownloadAsyncTask downloadAsyncTask;
    private String downloadUrl;

    public DownloadService() {
    }

    private  DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1,getNotification("downloding...",progress));

        }

        @Override
        public void onSuccess() {
            downloadAsyncTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Success",-1));
            Toast.makeText(DownloadService.this,"download success",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPause() {
            downloadAsyncTask=null;
            Toast.makeText(DownloadService.this,"download pause",Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onCancel() {
            downloadAsyncTask=null;
            stopForeground(true);
            Toast.makeText(DownloadService.this,"download canceled",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onFailed() {
            downloadAsyncTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("download failed",-1));
            Toast.makeText(DownloadService.this,"download failed",Toast.LENGTH_SHORT).show();


        }
    };

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private MyDownloadBinder mBinder = new MyDownloadBinder();

    class MyDownloadBinder extends Binder{
        public void startDownload(String url){
            if (downloadAsyncTask==null){
                downloadUrl = url;
                downloadAsyncTask = new DownloadAsyncTask(listener);
                downloadAsyncTask.execute(downloadUrl);
                startForeground(1,getNotification("Downloding....",0));
                Toast.makeText(DownloadService.this,"Downloading....",Toast.LENGTH_SHORT).show();
            }

        }
        public void pauseDownload(){
            if (downloadAsyncTask!=null){
                downloadAsyncTask.isCanceled();
            }

        }
        public void cancelDownload(){
            if (downloadAsyncTask!=null){
                downloadAsyncTask.isPaused();
            }else {
                if (downloadUrl!=null){
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()){
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this,"Download canceled",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Notification getNotification(String title, int progress) {
        Notification.Builder builder = new Notification.Builder(DownloadService.this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setContentTitle(title);
        if (progress>0){
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }
}
