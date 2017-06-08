package test.dashingqi.com.recyclertest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Fruit> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        RecyclerAdapter recyclerAdapter = new RecyclerAdapter(list, getApplicationContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void initData() {
        Fruit fruit1 = new Fruit();
        fruit1.setName("苹果");
        fruit1.setImage(R.mipmap.ic_launcher);
        list.add(fruit1);

        Fruit fruit = new Fruit();
        fruit.setName("梨子");
        fruit.setImage(R.mipmap.ic_launcher);
        list.add(fruit);

    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
    }
}
