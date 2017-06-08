package com.dashingqi.download1;

/**
 * Created by zhangqi on 2017/5/26.
 */

public interface DownloadListener {

    void onProgress(int progress);
    void onSuccess();
    void onPause();
    void onCancel();
    void onFailed();
}
