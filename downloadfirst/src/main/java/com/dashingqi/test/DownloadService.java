package com.dashingqi.test;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

public class DownloadService extends Service {

    private String downloadUrl;

    private DownloadAsyncTask downloadAsyncTask;
    private DownLoadBinder binder = new DownLoadBinder();
    public DownloadService() {
    }
    private DownloadListner listener = new DownloadListner() {
        @Override
        public void onProgress(int progress) {
            getNotification("downloading...",progress);

        }

        @Override
        public void onSucess() {
            downloadAsyncTask=null;
            //下载成功 退出前台服务
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Success",-1));
            Toast.makeText(DownloadService.this,"Download Success",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onFailed() {
            downloadAsyncTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download failed",-1));
            Toast.makeText(DownloadService.this,"Download failed",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPaused() {
            downloadAsyncTask=null;
            Toast.makeText(DownloadService.this,"Paused",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCanceled() {
            downloadAsyncTask=null;
            stopForeground(true);
            Toast.makeText(DownloadService.this,"Cancled",Toast.LENGTH_SHORT).show();

        }
    };

    class DownLoadBinder extends Binder{
        public void startDownload(String url){
            if (downloadAsyncTask==null){
                downloadUrl=url;
                downloadAsyncTask = new DownloadAsyncTask(listener);
                downloadAsyncTask.execute(downloadUrl);
                startForeground(1,getNotification("Downloading....",0));
                Toast.makeText(DownloadService.this,"Downloading...",Toast.LENGTH_SHORT).show();
            }

        }
        public void pasueDownload(){
            if (downloadAsyncTask!=null){
                downloadAsyncTask.isPaused();
            }
        }
        public void cancelDownload(){
            if (downloadAsyncTask!=null){
                downloadAsyncTask.isCanceled();
            }else{
                if (downloadUrl!=null){
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()){
                        file.delete();
                    }

                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this,"canceled",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return binder;
    }
    private NotificationManager getNotificationManager() {

        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(DownloadService.this);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentTitle(title);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setContentIntent(pendingIntent);
        if (progress>0){
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return builder.build();
    }
}
