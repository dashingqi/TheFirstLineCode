package com.dashingqi.download1;

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
 * Created by zhangqi on 2017/5/26.
 */

public class DownloadAsyncTask extends AsyncTask<String,Integer,Integer> {
    private static final int TYPE_SUCCESS = 0;
    private static final int TYPE_FAILED =1 ;
    private static final int TYPE_CANCEL =2 ;
    private static final int TYPE_PAUSE =3 ;


    private boolean isCaceled=false;
    private boolean isPaused = false;

    private int lastProgress=0;

    private DownloadListener listener;

    public DownloadAsyncTask(DownloadListener listener){
        this.listener=listener;

    }


    @Override
    protected Integer doInBackground(String... params) {
        InputStream is=null;
        RandomAccessFile savedFile=null;
         File file=null;
        try {

        String downloadUrl = params[0];
        long downloadedLength =0;//已近下载的字节数
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        file = new File(directory + fileName);//构造出存储的路径

        if (file.exists()) {
            downloadedLength = file.length();
        }
        long contentLength = getContentLength(downloadUrl);//获取到文件的总长度
            if (contentLength==0){
                return TYPE_FAILED;
            }
            else if (downloadedLength==contentLength){
                return TYPE_SUCCESS;
            }

            //实现断点续传

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();

            Response response = client.newCall(request).execute();
            if (response!=null){

                is = response.body().byteStream();
                savedFile = new RandomAccessFile(file,"rw");
                savedFile.seek(downloadedLength);

                int len;
                int total=0;
                byte[] b = new byte[1024];
                while((len=is.read(b))!=-1){
                    if (isCaceled){
                        return TYPE_CANCEL;
                    }
                    else if (isPaused){
                        return TYPE_PAUSE;
                    }else {
                        total+=len;
                        savedFile.write(b,0,len);
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
                if (is!=null){
                    is.close();
                }
                if (savedFile!=null){
                    savedFile.close();
                }
                if (file!=null&&isCaceled){
                    file.delete();

                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }

        return TYPE_FAILED;
    }

    /**
     * 获取文件总长度
     * @param url
     * @return
     */
    private long getContentLength(String url) throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request= new Request.Builder().url(url).build();

        Response response = client.newCall(request).execute();
        if (response!=null){
            long contentLength = response.body().contentLength();
            return  contentLength;
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

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSE:
                listener.onPause();
                break;
            case TYPE_CANCEL:
                listener.onCancel();
                break;
        }
    }

    public void isCanceled() {
        isCaceled=true;
    }

    public void isPaused() {
        isPaused=true;
    }
}
