package com.dashingqi.intenttest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.Serializable;

public class AnotherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another);
        Person person = (Person) getIntent().getSerializableExtra("person_data");
        Log.d("AnotherActivity",person.getAge());
        Log.d("AnotherActivity",person.getName());
    }
}
