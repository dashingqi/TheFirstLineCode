package com.dashingqi.webviewtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings()//设置浏览器的属性
                .setJavaScriptCanOpenWindowsAutomatically(true);//设置webview支持JavaScript脚本
        //从一个网页跳转到另一个时 任然希望在该 webview中显示
        webView.setWebViewClient(new WebViewClient());
        //加载地址
        webView.loadUrl("http://www.baidu.com");
    }
}
