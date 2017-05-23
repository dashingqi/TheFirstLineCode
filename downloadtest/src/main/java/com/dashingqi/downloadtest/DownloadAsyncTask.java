package com.dashingqi.downloadtest;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by zhangqi on 2017/5/22.
 */

public class DownloadAsyncTask extends AsyncTask<String,Integer,Integer> {

    private static final int TYPE_FAILED =0 ;
    private static final int TYPE_SUCCESS =1 ;
    private static final int TYPE_PAUSED =2 ;
    private static final int TYPE_CANCELED =3;


    private DownloadListener listener;

    private boolean isCanceled =false;
    private boolean isPaused=false;

    private int lastProgress;



    public DownloadAsyncTask(DownloadListener listener){
        this.listener=listener;

    }
    @Override
    protected Integer doInBackground(String... params) {

         InputStream is=null;
         RandomAccessFile rw=null;
         File file=null;
        try {
            long downloadedLength = 0;//记录已经下载的文件长度
            String downloadUrl = params[0];
            //构建存储路径文件
            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .getPath();
            file = new File(directory+fileName);
            if (file.exists()){
                downloadedLength= file.length();//将当前文件长度付给已经下载的变量
            }

            long contentLength = getContentLength(downloadUrl);//获取到下载文件的总长度;

            //如果文件的长度为0说明文件有问题 下载失败
            if (contentLength==0){
                return TYPE_FAILED;
            }else if (contentLength==downloadedLength){
                //已经下载的字节数与总长度相同 说明文件下载成功
                return TYPE_SUCCESS;
            }

            //下面进行断点续传下载
            OkHttpClient client = new OkHttpClient();
            Request build = new Request.Builder()
                    //断点下载 指定下载的位置
                    .addHeader("RANGE","bytes+"+downloadedLength+"-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(build).execute();

            if (response!=null){
                is = response.body().byteStream();

                rw = new RandomAccessFile(file, "rw");

                rw.seek(downloadedLength);//跳过已经下载的文件

                byte[] bytes = new byte[1024];
                int len;
                int total=0;
                while((len=is.read(bytes))!=-1){
                    if (isCanceled){
                        return TYPE_CANCELED;
                    }else if (isPaused){
                        return TYPE_PAUSED;
                    }else {
                        total+=len;
                        rw.write(bytes,0,len);

                        //计算下载的百分比
                        int progress = (int) ((total+downloadedLength)*100/contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();

                return TYPE_SUCCESS;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {

            if (is!=null) {
                is.close();
                }
                if (rw!=null) {
                    rw.close();
                }
                if (isCanceled && file!=null){
                    //取消下载 就删除文件
                    file.delete();
                }
            } catch (Exception e) {
                    e.printStackTrace();
                }
        }


        return TYPE_FAILED;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        //此方法里进行的是UI的更新
        int progress  = values[0];//获取下载进度的百分比
        if (progress>lastProgress){
            listener.onProgress(progress);
            lastProgress=progress;
        }

    }

    @Override
    protected void onPostExecute(Integer status) {
        switch (status){
            case TYPE_SUCCESS:
                listener.onSucess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;

        }
    }

    public void pausedDownload(){
        isPaused = true;
    }

    public void canceledDownload(){
        isCanceled=true;
    }
    /**
     * 获取文件的总长度
     * @param url
     * @return
     */
    private long getContentLength(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();

        Request builder = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(builder).execute();
        if (response!=null && response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }

        return 0;
    }
}
