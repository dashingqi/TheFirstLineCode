package com.dashingqi.cameraalbumtest;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Target;

public class MainActivity extends AppCompatActivity {

    private static final int TAKE_PHONE =1 ;
    private static final int CHOOSE_PHONE =2 ;
    private Button takePhone;
    private Uri imageUri;
    private ImageView picture;
    private Button chooseFromAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chooseFromAlbum = (Button) findViewById(R.id.choose_from_album);
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                }else{
                    openAlbum();

                }
            }
        });
        takePhone = (Button) findViewById(R.id.take_phone);
        picture = (ImageView) findViewById(R.id.picture);
        takePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //创建File对象 用于储存拍照后的图片 储存在SD中应用关联的缓冲目录下
                File outputFile = new File(getExternalCacheDir(),"output_image.jpg");
                try {
                    if(outputFile.exists()){
                        outputFile.delete();
                    }
                    outputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //版本的判断
                if (Build.VERSION.SDK_INT>=24){
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.dashingqi.cameraalbumtest.fileprovider", outputFile);
                }else {
                    imageUri=Uri.fromFile(outputFile);
                }
                //启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PHONE);
            }
        });
    }

    /**
     * 打开相册
     */
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PHONE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case 1:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else {
                    Toast.makeText(MainActivity.this,"you denied the permission",Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PHONE:
                if(resultCode==RESULT_OK){
                    try {
                        //将拍摄的照片显示出来
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize=32;
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri),null,options);
                        picture.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHONE:
                if (requestCode==RESULT_OK){
                    //判断手机版本号  4.4版本以上
                    if(Build.VERSION.SDK_INT>=19){
                        handleImageOnKitKat(data);
                    }else{
                        //4.4版本一下
                        handleImageBeforeKitKat(data);

                    }
                }
            default:
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    //@Target(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        //如果document的类型是uri 那抹通过document id处理
        if(DocumentsContract.isDocumentUri(this,uri)){
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
            }else if ("content".equalsIgnoreCase(uri.getScheme())){
                //如果是content类型的uri 那么使用普通方式处理
                imagePath= getImagePath(uri,null);
            }else if ("file".equalsIgnoreCase(uri.getScheme())){
                //如果是file类型的uri 直接获取图片的路径就行。
                imagePath = uri.getPath();
            }
            displayImage(imagePath);
        }

    private void displayImage(String imagePath) {
        if (imagePath!=null){
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize=32;
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath,options);
            picture.setImageBitmap(bitmap);
        }else {
            Toast.makeText(MainActivity.this,"failed to get image",Toast.LENGTH_LONG).show();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过uri和selection来获取真实的图片路径
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}
