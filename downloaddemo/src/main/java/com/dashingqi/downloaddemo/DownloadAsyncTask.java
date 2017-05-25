package com.dashingqi.downloaddemo;

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
 * Created by zhangqi on 2017/5/25.
 */

public class DownloadAsyncTask extends AsyncTask<String,Integer,Integer> {
    private static final int TYPE_FAILED = 0;
    private static final int TYPE_SUCCESS = 1;
    private static final int TYPE_CANCELED = 2;
    private static final int TYPE_PAUSED = 3;
    private DownloadListener listener;

    private boolean isCaceled = false;
    private boolean isPaused = false;

    private int lastProgress;



    public DownloadAsyncTask(DownloadListener listener) {

        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream is = null;
        RandomAccessFile savedFile = null;
        File file=null;
        try {
            String url = params[0];//获取到下载的地址
            long downloadedlength = 0;//记录已近下载的文件长度

            String fileName = url.substring(url.lastIndexOf("/"));//构造文件名
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);

            if (file.exists()) {
                downloadedlength = file.length();
            }
            long contentLength = getContentLength(url);
            if (contentLength == 0) {
                return TYPE_FAILED;
            } else if (downloadedlength == contentLength) {
                return TYPE_SUCCESS;
            }


            //进行断点续传下载
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedlength + "-")
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file, "rw");
                savedFile.seek(downloadedlength);//从已近下载的地方开始下载
                byte[] b = new byte[1024];
                int len;
                int total = 0;
                while ((len = is.read(b)) != -1) {

                    if (isCaceled) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        total += len;
                        savedFile.write(b, 0, len);

                        //计算已近下载的百分比
                        int progress = (int) ((total + downloadedlength) * 100 / contentLength);
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
                if (is!=null){
                    is.close();
                }
                if (savedFile!=null){
                    savedFile.close();
                }
                if (isCaceled&&file!=null){
                    file.delete();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return TYPE_FAILED;
}

    /**
     * 获取到下载文件的字节数
     * @return
     */
    private long getContentLength(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response = client.newCall(request).execute();
        if (response!=null&&response.isSuccessful()){
            long contentLength = response.body().contentLength();
            return contentLength;
        }
        return 0;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];

        if (progress>lastProgress){
            listener.onProgress(progress);
            lastProgress=progress;
        }
    }

    public void pauseDownLoad(){
        isPaused=true;
    }
    public void cancleDownload(){
        isCaceled=true;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case TYPE_SUCCESS:
                listener.onSucess();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            default:
                break;
        }
    }
}
