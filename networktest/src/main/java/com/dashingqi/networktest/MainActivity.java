package com.dashingqi.networktest;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button send;
    private TextView responseText;
//    private StringBuilder builder;
//    private Handler handler =new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what){
//                case 1:
//                    responseText.setText(builder.toString());
//                    break;
//                    default:
//                        break;
//            }
//        }
//    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send = (Button) findViewById(R.id.send);
        responseText = (TextView) findViewById(R.id.response_text);
        send.setOnClickListener(this);
    }

    private void sendRequestWithHttpUrlConnection() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://10.0.2.2/get_data.xml")
                            .build();
                    Response response = client.newCall(request).execute();
                    String result = response.body().string();
                    parseXMlWithSAX(result);
                    //parseXMLWithPull(result);
                   // showResponse(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();
//        //开启子线程 访问网络是个耗时的操作
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                 HttpURLConnection connection=null;
//                 BufferedReader reader=null;
//                try {
//                    URL url = new URL("http://www.baidu.com");
//                    connection = (HttpURLConnection) url.openConnection();
//                    connection.setRequestMethod("GET");
//                    //设置连接超时时间
//                    connection.setConnectTimeout(8000);
//                    //System.out.println("连接超时");
//                    //设置读取超时时间
//                    connection.setReadTimeout(8000);
//                   // System.out.println("读取时间超时");
//                    //获取服务器的返回流
//                    InputStream in = connection.getInputStream();
//                    int code = connection.getResponseCode();
//                    if (code==200){
//                        System.out.println("请求成功");
//                        reader = new BufferedReader(new InputStreamReader(in));
//                        builder = new StringBuilder();
//                        String line;
//                        while((line = reader.readLine())!=null){
//                            builder.append(line);
//                        }
////                    Message message = new Message();
////                    message.what=1;
////                    handler.sendMessage(message);
////                        showResponse(builder.toString());

//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }finally {
//                    if (reader!=null){
//                        try {
//                            reader.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    if (connection!=null){
//                        //关闭连接
//                        connection.disconnect();
//                    }
//                }
//            }
//        }).start();
    }

    /**
     * 用sax 解析数据
     * @param result
     */
    private void parseXMlWithSAX(String result) {
        try {
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();
            ConterHandler conterHandler = new ConterHandler();
            //将实例设置到XMLReader中
            xmlReader.setContentHandler(conterHandler);
            //开始解析
            xmlReader.parse(new InputSource(new StringReader(result)));
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析数据 pull方式
     * @param result
     */
    private void parseXMLWithPull(String result) {
        try {
            XmlPullParserFactory xmlPullParserFactory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
            //将服务器返回的数据设置进去
            xmlPullParser.setInput(new StringReader(result));
            int eventType = xmlPullParser.getEventType();
            String id="";
            String name="";
            String version="";
            while(eventType!=XmlPullParser.END_DOCUMENT){
                String nodeName = xmlPullParser.getName();
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        if ("id".equals(nodeName)){
                            //获取文本内容
                            id=xmlPullParser.nextText();
                        }else if ("name".equals(nodeName)){
                            name=xmlPullParser.nextText();
                        }else if ("version".equals(nodeName)){
                            version=xmlPullParser.nextText();
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("app".equals(nodeName)){
                            Log.d("MainActivity","id is "+id);
                            Log.d("MainActivity","name is"+name);
                            Log.d("MainActivity","version is"+version);
                        }
                        break;
                        default:
                            break;
                }
                eventType=xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showResponse(final String s) {
        //将线程切换到组主程中 也叫UI线程
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //进行UI操作 不能再子线程中操作
                responseText.setText(s);
            }
        });
    }
    @Override
    public void onClick(View v) {
        sendRequestWithHttpUrlConnection();
    }
}
