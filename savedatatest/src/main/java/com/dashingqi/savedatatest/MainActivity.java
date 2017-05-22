package com.dashingqi.savedatatest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private FileOutputStream out=null;
    private BufferedWriter writer=null;
    private EditText dataText;
    private FileInputStream input;
    private BufferedReader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataText = (EditText) findViewById(R.id.et_data);
        String InputText = load();
        if (!TextUtils.isEmpty(InputText)) {
            dataText.setText(InputText);
            //将光标的位置移动到文本末尾 好进行输入
            dataText.setSelection(dataText.length());
            Toast.makeText(this, "Restroing success", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String data = dataText.getText().toString().trim();
        save(data);

    }

    /**
     * 将数据写入文件中
     * @param inputText 数据
     */
    public void save(String inputText){
        try {
            out = openFileOutput("data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(writer!=null){
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 从文件中读取数据
     * @return 读取的数据
     */
    public String load(){
        StringBuilder append = new StringBuilder();
        try {
            input = openFileInput("data");
            reader = new BufferedReader(new InputStreamReader(input));

            String line="";
            while((line=reader.readLine())!=null){
                append.append(line);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return append.toString();
    }
}
