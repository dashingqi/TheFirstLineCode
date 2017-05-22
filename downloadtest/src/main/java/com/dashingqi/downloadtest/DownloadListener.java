package com.dashingqi.downloadtest;

/**
 * Created by zhangqi on 2017/5/22.
 */

public interface DownloadListener {
    void onProgress(int progress);

    void onSucess();

    void onFailed();

    void onPaused();

    void onCanceled();
}
