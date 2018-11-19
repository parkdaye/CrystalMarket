package com.android.crystalmarket.activity;

import android.graphics.Bitmap;

public class CommentItem {
    private String userimg;
    private String usernic;
    private String comment;
    private String id;

    public String getUserimg(){  return userimg; }
    public String getUsernic(){ return usernic; }
    public String getReview() { return comment; }
    public String getId() { return id; }

    public CommentItem(String a, String b, String d, String id){
        this.userimg = a;
        this.usernic = b;
        this.comment = d;
        this.id = id;
    }
}
