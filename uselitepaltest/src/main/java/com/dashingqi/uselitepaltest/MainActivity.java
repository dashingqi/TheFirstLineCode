package com.dashingqi.uselitepaltest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Button createDB;
    private Button addData;
    private Button updataData;
    private Button deleteData;
    private Button query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createDB = (Button) findViewById(R.id.create_db);
        addData = (Button) findViewById(R.id.addData);
        updataData = (Button) findViewById(R.id.updata);
        deleteData = (Button) findViewById(R.id.delete);
        query = (Button) findViewById(R.id.query);
        createDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connector.getDatabase();
            }
        });
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Book book = new Book();
                book.setName("The Da Vinci Code");
                book.setAuthor("Dan Brown");
                book.setPages(454);
                book.setPrice(14.3);
                book.setPress("UnKnown");
                book.save();
           }
        });

        updataData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Book book = new Book();
//                book.setName("The Lost Symbol");
//                book.setAuthor("Dan Brown");
//                book.setPages(510);
//                book.setPrice(19.95);
//                book.setPress("UnKnown");
//                book.save();
//                book.setPrice(10.99);
//                book.save();
                Book book = new Book();
                book.setPrice(14.95);
                book.setPress("Anchor");
                book.updateAll("name=? and author=?","The Lost Symbol","Dan Brown");

            }
        });
        deleteData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataSupport.deleteAll(Book.class,"price>?","14.90");
            }
        });
        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Book> Books = DataSupport.findAll(Book.class);
                for(Book book : Books){
                    Log.d("MainActivity","book name is "+book.getName());
                    Log.d("MainActivity","book author is "+book.getAuthor());
                    Log.d("MainActivity","book pages is "+book.getPages());
                    Log.d("MainActivity","book price is"+book.getPrice());
                    Log.d("MainActivity","book press is"+book.getPress());
                }
            }
        });
    }
}
