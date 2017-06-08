package com.dashingqi.test;

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
 * Created by zhangqi on 2017/5/30.
 */

public class DownloadAsyncTask extends AsyncTask<String, Integer, Integer> {
    private static final int TYPE_FAILED = 0;
    private static final int TYPE_SUCCESS = 1;
    private static final int TYPE_PAUSED = 2;
    private static final int TYPE_CANCEL = 3;
    private DownloadListner listner;

    private boolean isPaused = false;
    private boolean isCanceled = false;
    private int lastProgress = 0;


    public DownloadAsyncTask(DownloadListner listener) {
        this.listner = listener;

    }

    @Override
    protected Integer doInBackground(String... params) {

        File file = null;
        InputStream is = null;
        RandomAccessFile savesFile = null;

        try {
            String downloadUrl = params[0];
            long downloadedLength = 0;

            String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
            String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            file = new File(directory + fileName);

            if (file.exists()) {
                downloadedLength = file.length();
            }

            //获取到下载文件的总长度
            long contentLength = getContentLength(downloadUrl);

            if (contentLength == 0) {
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength) {
                return TYPE_SUCCESS;
            }

            //断点下载逻辑

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    .url(downloadUrl)
                    .build();
            Response response = client.newCall(request).execute();
            if (response != null) {
                is = response.body().byteStream();
                savesFile = new RandomAccessFile(file, "rw");
                savesFile.seek(downloadedLength);

                byte[] bytes = new byte[1024];
                int len;
                int total = 0;
                while (((len) = is.read(bytes)) != -1) {
                    if (isPaused) {
                        return TYPE_PAUSED;
                    } else if (isCanceled) {
                        return TYPE_CANCEL;
                    } else {

                        total += len;
                        savesFile.write(bytes, 0, len);

                        //计算下载进度的百分比

                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        //更新进度
                        publishProgress(progress);
                    }
                }

                response.body().close();
                return TYPE_SUCCESS;

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (savesFile != null) {
                    savesFile.close();
                }
                if (file != null && isCanceled) {
                    file.delete();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return TYPE_FAILED;
    }

    /**
     * 获取下载的文件的总长度
     *
     * @param downloadUrl
     * @return
     */
    private long getContentLength(String downloadUrl) throws IOException {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl).build();
        Response response = client.newCall(request).execute();
        if (response!=null){
            long contentLength = response.body().contentLength();

            return contentLength;
        }
        return 0;


    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        if (progress > lastProgress) {
            listner.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer) {
            case TYPE_SUCCESS:
                listner.onSucess();
                break;
            case TYPE_FAILED:
                listner.onFailed();
                break;
            case TYPE_PAUSED:
                listner.onPaused();
                break;
            case TYPE_CANCEL:
                listner.onCanceled();
                break;
        }
    }

    public void isPaused() {
        isPaused = true;
    }

    public void isCanceled() {
        isCanceled = true;
    }
}
