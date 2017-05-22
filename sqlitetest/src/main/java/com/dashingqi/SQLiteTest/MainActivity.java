package com.dashingqi.SQLiteTest;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    //private Button createDatabase;
    private MyOpenHelper myOpenHelper;
    private Button addData;
    private Button updata;
    private Button delete;
    private Button query;
    private String newId;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myOpenHelper = new MyOpenHelper(this,"BookStore.db",null,2);
        //createDatabase = (Button) findViewById(R.id.create);
        addData = (Button) findViewById(R.id.add_data);
        updata = (Button) findViewById(R.id.updata);
        query = (Button) findViewById(R.id.query);

        delete = (Button) findViewById(R.id.delete);
//        createDatabase.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                myOpenHelper.getWritableDatabase();
//            }
//        });
        addData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri uri = Uri.parse("content://com.dashingqi.sqlitetest.provider/book");
                ContentValues values = new ContentValues();
                values.put("name","A Class of Kings");
                values.put("author","George Martin");
                values.put("pages",1040);
                values.put("price",22.85);
                Uri newUri = getContentResolver().insert(uri, values);
                newId = newUri.getPathSegments().get(1);
//                SQLiteDatabase db = myOpenHelper.getWritableDatabase();
//                ContentValues values = new ContentValues();
//
//                values.put("name","The Da Code");
//                values.put("author","Dan Brown");
//                values.put("pages",509);
//                values.put("price",16.96);
//                db.insert("Book",null,values);

            }
        });
        updata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //指定刚才插入的数据
                Uri uri = Uri.parse("content://com.dashingqi.sqlitetest.provider/book/" + newId);
                ContentValues values = new ContentValues();
                values.put("name","A Storm of Awords");
                values.put("pages",1216);
                values.put("price",24.05);
                getContentResolver().update(uri,values,null,null);

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://com.dashingqi.sqlitetest.provider/book/" + newId);
                getContentResolver().delete(uri,null,null);
//                SQLiteDatabase db = myOpenHelper.getReadableDatabase();
//                db.delete("Book","pages>?",new String[]{"500"});
            }
        });

        query.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://com.dashingqi.sqlitetest.provider/book");
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);
                if (cursor!=null){
                while (cursor.moveToNext()){
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                        double price = cursor.getDouble(cursor.getColumnIndex("price"));

                        Log.d("MainActivity","the name is"+name);
                        Log.d("MainActivity","the author is"+author);
                        Log.d("MainActivity","the pages is"+pages);
                        Log.d("MainActivity","the price is"+price);
                    }
                }
                cursor.close();
            }
        });
    }
}
