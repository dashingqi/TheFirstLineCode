package com.dashingqi.downloadtest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.File;

import static android.R.id.progress;

public class DownloadService extends Service {

    private DownloadAsyncTask downloadAsyncTask;
    private String downloadUrl;
    public DownloadService() {
    }
    private DownloadListener listener = new DownloadListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().
                    notify(1,getNotification("Downloading....",progress));

        }

        @Override
        public void onSucess() {
            downloadAsyncTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Success",-1));
            Toast.makeText(DownloadService.this,"download success",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onFailed() {
            downloadAsyncTask=null;
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download failed",-1));
            Toast.makeText(DownloadService.this,"download failed",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadAsyncTask=null;
            Toast.makeText(DownloadService.this,"download paused",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onCanceled() {
            downloadAsyncTask=null;
            stopForeground(true);
            Toast.makeText(DownloadService.this,"download cancled",Toast.LENGTH_SHORT).show();

        }
    };

    private DownloadBinder mBind = new DownloadBinder();
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBind;
    }
    class DownloadBinder extends Binder{

        public void startDownload(String url){
            if (downloadAsyncTask==null){
                downloadUrl=url;
                downloadAsyncTask = new DownloadAsyncTask(listener);
                //执行AsyncTask 将地址传入
                downloadAsyncTask.execute(downloadUrl);
                startForeground(1,getNotification("Downloading....",0));
                Toast.makeText(DownloadService.this,"DownLoading.....",Toast.LENGTH_SHORT).show();
            }

        }
        public void pausedDownload(){

            if (downloadAsyncTask!=null){
                    downloadAsyncTask.pausedDownload();
            }

        }
        public void cancelDownload(){

            if (downloadAsyncTask!=null){
                //在下载过程中取消下载
                downloadAsyncTask.canceledDownload();
            }else{
                //是在下载的过程中暂停下载时取消下载
                if (downloadUrl!=null){
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()){
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this,"Canceled",Toast.LENGTH_SHORT).show();
                }
            }

        }

    }
    public NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }
    public Notification getNotification(String title, int progress){

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setContentIntent(pendingIntent);
        if (progress>0){
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);//设置进度条的进度

        }

        return builder.build();

    }
}
