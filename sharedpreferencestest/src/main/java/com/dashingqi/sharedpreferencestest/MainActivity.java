package com.dashingqi.sharedpreferencestest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button save;
    private String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        save = (Button) findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将数据储存到sharedpreferences中
               /* SharedPreferences sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = sp.edit();
                edit.putString("name","Tom");
                edit.putInt("age",22);
                edit.putBoolean("married",false);
                edit.apply();*/

               //从sharedpreferences读取数据
                SharedPreferences sp = getSharedPreferences("sp", Context.MODE_PRIVATE);
                String name = sp.getString("name", "");
                int age = sp.getInt("age", 0);
                boolean married = sp.getBoolean("married", true);



                Log.d(TAG,"name is "+name);
                Log.d(TAG,"age is "+age);
                Log.d(TAG,"married is "+married);
            }
        });
    }
}
