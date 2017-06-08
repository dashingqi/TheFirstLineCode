package com.dashingqi.test;

/**
 * Created by zhangqi on 2017/5/30.
 */

public interface DownloadListner {
    void onProgress(int progress);

    void onSucess();

    void onFailed();

    void onPaused();

    void onCanceled();
}
