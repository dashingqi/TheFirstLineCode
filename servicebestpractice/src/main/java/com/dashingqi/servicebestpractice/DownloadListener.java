package com.dashingqi.servicebestpractice;

/**
 * Created by zhangqi on 2017/5/22.
 * 下载过程中的回调接口
 */

public interface DownloadListener {

    void onProgress(int progress);

    void onSucess();

    void onFailed();

    void onPaused();

    void onCanceled();

}

