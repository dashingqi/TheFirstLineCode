package com.dashingqi.downloaddemo;

/**
 * Created by zhangqi on 2017/5/25.
 */

public interface DownloadListener {
    void onProgress(int progress);

    void onSucess();

    void onFailed();

    void onPaused();

    void onCanceled();
}
