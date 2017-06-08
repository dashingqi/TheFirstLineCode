package com.dashingqi.imagelook;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity {

    private EditText et_path;
    private Button button;
    private ImageView iv;
    private String path;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    iv.setImageBitmap(bitmap);
                    break;
                case 2:
                    Bitmap catchBitmap = (Bitmap) msg.obj;
                    iv.setImageBitmap(catchBitmap);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        path = et_path.getText().toString().trim();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {

                    private FileOutputStream fos;
                    private InputStream in;

                    @Override
                    public void run() {
                        try {
                            File file = new File(getCacheDir(), Base64.encodeToString(path.getBytes(), Base64.DEFAULT));
                            if (file.exists() && file.length() > 0) {
                                Log.d("MainActivity:", "使用缓存加载图片");
                                Bitmap catchBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                                Message message = Message.obtain();
                                message.obj = catchBitmap;
                                message.what = 2;
                                handler.sendMessage(message);
                            } else {
                                Log.d("MainActivity:","第一次加载图片");
                                URL url = new URL(path);
                                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                connection.setRequestMethod("GET");//设置请求的方法
                                connection.setConnectTimeout(5000);//设置连接超时的时间
                                int code = connection.getResponseCode();//获取服务器的返回码
                                if (code == 200) {
                                    //服务器返回的数据 以流的形式
                                    in = connection.getInputStream();
                                    fos = new FileOutputStream(file);
                                    int len;
                                    byte[] bytes = new byte[1024];
                                    while((len= in.read(bytes))!=-1){
                                        fos.write(bytes,0,len);
                                    }
                                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());// 将流/文件 转换成bitmap
                                    Message msg = Message.obtain();
                                    msg.obj = bitmap;
                                    msg.what = 1;
                                    handler.sendMessage(msg);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            try{
                                if (fos!=null){
                                    fos.close();
                                }
                                if (in!=null){
                                    in.close();
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        });
    }

    private void initView() {
        et_path = (EditText) findViewById(R.id.et_path);
        button = (Button) findViewById(R.id.button);
        iv = (ImageView) findViewById(R.id.iv);
    }
}
