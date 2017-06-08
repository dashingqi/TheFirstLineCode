package com.dashingqi.recyclerviewtest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Fruit> mFruitList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        FruitAdapter fruitAdapter = new FruitAdapter(mFruitList);
        recyclerView.setAdapter(fruitAdapter);
    }

    private void initData() {
        for (int i=0;i<=3;i++){

            Fruit orange = new Fruit("橘子", R.mipmap.ic_launcher);
            mFruitList.add(orange);
            Fruit apple = new Fruit("苹果", R.mipmap.ic_launcher);
            mFruitList.add(apple);
            Fruit banana = new Fruit("香蕉", R.mipmap.ic_launcher);
            mFruitList.add(banana);
            Fruit pear = new Fruit("梨子", R.mipmap.ic_launcher);
            mFruitList.add(pear);
        }
    }

}
