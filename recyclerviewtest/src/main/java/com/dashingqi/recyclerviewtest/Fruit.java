package com.dashingqi.recyclerviewtest;

/**
 * Created by zhangqi on 2017/5/26.
 */

public class Fruit {
    private String name;
    private int imageId;
    public Fruit(String name,int imagedId){
        this.name=name;
        this.imageId=imagedId;
    }
    public String getName(){
        return name;
    }
    public int getImageId(){
        return imageId;
    }
}
