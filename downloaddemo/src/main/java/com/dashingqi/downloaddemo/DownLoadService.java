package com.dashingqi.downloaddemo;

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

public class DownLoadService extends Service {

    private DownloadAsyncTask downloadAsyncTask;
    private String downloadUrl=null;
    public DownLoadService() {
    }

    private DownloadListener listener =new DownloadListener() {
        @Override
        public void onProgress(int progress) {

            getNotificationManager().notify(1,getNotification("Downloading....",progress));


        }

        @Override
        public void onSucess() {
            downloadAsyncTask=null;
            //下载成功关闭前台服务 创建一个下载成功的通知
            getNotificationManager().notify(1,getNotification("Download Success",-1));
            Toast.makeText(DownLoadService.this,"DownLaod Success",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onFailed() {

            downloadAsyncTask=null;
            //下载失败关闭前台服务 创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("Download Failed",-1));
            Toast.makeText(DownLoadService.this,"DownLaod Failed",Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPaused() {
            downloadAsyncTask=null;
            Toast.makeText(DownLoadService.this,"DownLaod Paused",Toast.LENGTH_SHORT).show();


        }

        @Override
        public void onCanceled() {
            downloadAsyncTask=null;
            //取消下载 关闭前台
            stopForeground(true);
            Toast.makeText(DownLoadService.this,"DownLaod Canceled",Toast.LENGTH_SHORT).show();

        }
    };

    private DownLoadBinder mBinder = new DownLoadBinder() ;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return mBinder;
    }
    class DownLoadBinder extends Binder{

        public void startDownload(String url){
            if (downloadAsyncTask==null){
                downloadUrl =url;
                downloadAsyncTask = new DownloadAsyncTask(listener);
                downloadAsyncTask.execute(downloadUrl);
                startForeground(1,getNotification("Downloading.....",0));
                Toast.makeText(DownLoadService.this,"DownLoading.....",Toast.LENGTH_SHORT).show();
            }

        }
        public void pauseDownload(){
            if (downloadAsyncTask!=null){
                downloadAsyncTask.pauseDownLoad();
            }

        }
        public void cancelDownload(){
            if (downloadAsyncTask!=null){
                downloadAsyncTask.cancleDownload();
            }else{
                if (downloadUrl!=null){
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()){
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    //不属于前天服务 随着内存的多 将被回收
                    stopForeground(true);
                    Toast.makeText(DownLoadService.this,"Canceled",Toast.LENGTH_SHORT).show();
                }
            }

        }

    }
    public NotificationManager getNotificationManager(){
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
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
